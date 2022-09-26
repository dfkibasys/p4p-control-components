package de.dfki.cos.basys.p4p.controlcomponent.virtual.service.agv;

import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.pathfinding.AStarGrid;
import de.dfki.cos.basys.common.pathfinding.AStarLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

public class Unit {

    private Logger log = LoggerFactory.getLogger(getClass().getName());

    public interface Callback {
        void handleStateChange(State state);
        void handlePathFound(Path path);
        void handlePoseChanged(Vector3f position, Quaternion orientation);
    }

    public enum State { IDLE, PENDING, EXECUTING, DONE, FAILED, ABORTED }

    private final AStarGrid grid;
    private final AStarLogic logic;
    private final Transform transform;

    private State state = State.IDLE;

    private float speed = 1.0f;
    private int frameTime = 20; // 1000ms/20ms = 50Hz


    public float turnSpeed = 3.0f;
    public float turnDst = 0.2f;
    public float stoppingDst = 0.1f;

    private Callback callback = null;

    public Unit(Vector3f initialPosition, Quaternion initialRotation, AStarGrid grid) {
        this.transform = new Transform(initialPosition, initialRotation);
        this.grid = grid;
        this.logic = new AStarLogic(grid);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public Transform getTransform() {
        return transform;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
        if (callback != null) {
            callback.handleStateChange(state);
        }
    }

    public Vector3f getPosition() {return transform.getTranslation(); }

    public Quaternion getRotation() {return transform.getRotation(); }

    public void gotoPositionAsync(Vector3f targetPosition, Quaternion targetOrientation) {
        ComponentContext.getStaticContext().getScheduledExecutorService().submit(new Runnable() {
            @Override
            public void run() {
                setState(State.PENDING);
                Path path = findPath(targetPosition, targetOrientation);
                if (path != null) {
                    setState(State.EXECUTING);
                    //followPathSimple(path);
                    followPath(path);
                    setState(State.DONE);
                }
            }
        });
    }

    private Path findPath(Vector3f targetPosition, Quaternion targetOrientation) {
        log.info("findPath to {} with orientation {}", targetPosition, targetOrientation);
        List<Vector3f> waypoints = logic.getWaypoints(transform.getTranslation(), targetPosition);
        if (waypoints == null) {
            setState(State.FAILED);
            log.warn("no path found");
            return null;
        }



        Path path = new Path(waypoints.toArray(new Vector3f[0]), transform.getTranslation(), turnDst, stoppingDst);
        path.targetOrientation = targetOrientation;
        if (callback != null) {
            callback.handlePathFound(path);
        }
        return path;
    }

    private void followPathSimple(Path path) {

        //transform.setRotation(new Quaternion(0f,1f,0f,1f));
        int targetIndex = 0;
        Vector3f currentWaypoint = path.lookPoints[targetIndex];

        //List<Vector3f> traveled = new LinkedList<>();

        while (true) {
            if (transform.getTranslation().equals(currentWaypoint)) {
                targetIndex++;
                if (targetIndex >= path.lookPoints.length) {
                    break;
                }
                currentWaypoint = path.lookPoints[targetIndex];
                Vector3f newDirection = this.getPosition().subtract(currentWaypoint).normalize();
                Quaternion newOrientation = transform.getRotation().lookAt(newDirection,Vector3f.UNIT_Y);
                transform.setRotation(newOrientation);
            }

            Vector3f newPosition = moveTowards(currentWaypoint,speed * frameTime/1000);
            transform.setTranslation(newPosition);

            if (callback != null) {
                callback.handlePoseChanged(getPosition(), getRotation());
            }
            try {
                TimeUnit.MILLISECONDS.sleep(frameTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        //transform.setRotation(new Quaternion(0f,1f,0f,1f));
    }

    private void followPath(Path path) {
        log.info("followPath - start");
        boolean followingPath = true;
        int pathIndex = 0;

        //Quaternion targetRotation = LookRotation (path.lookPoints [pathIndex].subtract(transform.getTranslation()), Vector3f.UNIT_Y);
        //Quaternion targetRotation = LookRotation (transform.getTranslation().subtract(path.lookPoints [pathIndex]), Vector3f.UNIT_Y);
        //transform.getRotation().set(targetRotation);

        Vector3f newDirection = this.getPosition().subtract(path.lookPoints [pathIndex]).normalize();
        Quaternion newOrientation = new Quaternion(transform.getRotation()).lookAt(newDirection,Vector3f.UNIT_Y);


        float speedPercent = 1;

        log.info("followPath - perform initial rotation");
        while (true) {
            if (transform.getRotation().isSimilar(newOrientation, 0.04f)) {
                break;
            } else {
                float delta = frameTime / 1000f * turnSpeed;
                transform.getRotation().slerp(newOrientation, delta);
            }

            if (callback != null) {
                callback.handlePoseChanged(getPosition(), getRotation());
            }
            try {
                TimeUnit.MILLISECONDS.sleep(frameTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


        log.info("followPath - perform path traversal");
        while (followingPath) {
            Vector2f pos2D = new Vector2f (transform.getTranslation().x, transform.getTranslation().z);
            while (path.turnBoundaries [pathIndex].HasCrossedLine (pos2D)) {
                if (pathIndex == path.finishLineIndex) {
                    followingPath = false;
                    break;
                } else {
                    pathIndex++;
                }
            }

            if (followingPath) {

                if (pathIndex >= path.slowDownIndex && stoppingDst > 0) {
                    speedPercent = clamp(path.turnBoundaries [path.finishLineIndex].DistanceFromPoint (pos2D) / stoppingDst, 0, 1);
                    if (speedPercent < 0.01f) {
                        followingPath = false;
                    }
                }

                //targetRotation = LookRotation (path.lookPoints [pathIndex].subtract(transform.getTranslation()), Vector3f.UNIT_Y);
                Quaternion targetRotation = LookRotation (transform.getTranslation().subtract(path.lookPoints [pathIndex]), Vector3f.UNIT_Y);
                float delta = frameTime/1000f * turnSpeed;
                transform.getRotation().slerp(targetRotation, delta);

                Vector3f step = new Vector3f(Vector3f.UNIT_Z);
                targetRotation.multLocal(step);
                float scale = frameTime/1000f * speed * speedPercent;
                step.multLocal(scale);
                //transform.getTranslation().addLocal(step);
                transform.getTranslation().subtractLocal(step);
            }

            if (callback != null) {
                callback.handlePoseChanged(getPosition(), getRotation());
            }
            try {
                TimeUnit.MILLISECONDS.sleep(frameTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        log.info("followPath - perform final rotation");
        while (true) {
            if (transform.getRotation().isSimilar(path.targetOrientation, 0.025f)) {
                break;
            } else {
                float delta = frameTime / 1000f * turnSpeed;
                transform.getRotation().slerp(path.targetOrientation, delta);
            }

            if (callback != null) {
                callback.handlePoseChanged(getPosition(), getRotation());
            }
            try {
                TimeUnit.MILLISECONDS.sleep(frameTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        log.info("followPath - finished");
    }

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    private Vector3f moveTowards(Vector3f target, float maxDistanceDelta)
    {
        Vector3f current = transform.getTranslation();

        // avoid vector ops because current scripting backends are terrible at inlining
        float toVector_x = target.x - current.x;
        float toVector_y = target.y - current.y;
        float toVector_z = target.z - current.z;

        float sqdist = toVector_x * toVector_x + toVector_y * toVector_y + toVector_z * toVector_z;

        if (sqdist == 0 || (maxDistanceDelta >= 0 && sqdist <= maxDistanceDelta * maxDistanceDelta))
            return target;
        float dist = (float)Math.sqrt(sqdist);

        return new Vector3f(current.x + toVector_x / dist * maxDistanceDelta,
                current.y + toVector_y / dist * maxDistanceDelta,
                current.z + toVector_z / dist * maxDistanceDelta);
    }

    // from http://answers.unity3d.com/questions/467614/what-is-the-source-code-of-quaternionlookrotation.html
    private static Quaternion LookRotation(Vector3f forward, Vector3f up)
    {
        forward.normalizeLocal();
        Vector3f right = up.cross(forward);

        up = forward.cross(right);

        var m00 = right.x;
        var m01 = right.y;
        var m02 = right.z;
        var m10 = up.x;
        var m11 = up.y;
        var m12 = up.z;
        var m20 = forward.x;
        var m21 = forward.y;
        var m22 = forward.z;


        float num8 = (m00 + m11) + m22;
        var quaternion = new Quaternion();
        if (num8 > 0f)
        {
            var num = (float)Math.sqrt(num8 + 1f);
            float w = num * 0.5f;
            num = 0.5f / num;
            float x = (m12 - m21) * num;
            float y = (m20 - m02) * num;
            float z = (m01 - m10) * num;
            return quaternion.set(x,y,z,w);
        }
        if ((m00 >= m11) && (m00 >= m22))
        {
            var num7 = (float)Math.sqrt(((1f + m00) - m11) - m22);
            var num4 = 0.5f / num7;
            float x = 0.5f * num7;
            float y = (m01 + m10) * num4;
            float z = (m02 + m20) * num4;
            float w = (m12 - m21) * num4;
            return quaternion.set(x,y,z,w);
        }
        if (m11 > m22)
        {
            var num6 = (float)Math.sqrt(((1f + m11) - m00) - m22);
            var num3 = 0.5f / num6;
            float x = (m10 + m01) * num3;
            float y = 0.5f * num6;
            float z = (m21 + m12) * num3;
            float w = (m20 - m02) * num3;
            return quaternion.set(x,y,z,w);
        }
        var num5 = (float)Math.sqrt(((1f + m22) - m00) - m11);
        var num2 = 0.5f / num5;
        float x = (m20 + m02) * num2;
        float y = (m21 + m12) * num2;
        float z = 0.5f * num5;
        float w = (m01 - m10) * num2;
        return quaternion.set(x,y,z,w);
    }
}
