package de.dfki.cos.basys.p4p.controlcomponent.virtual.service.agv;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class Path {
    public Vector3f[] lookPoints;
    public Line[] turnBoundaries;
    public int finishLineIndex;
    public int slowDownIndex;
    public Quaternion targetOrientation;

    public Path(Vector3f[] waypoints, Vector3f startPos, float turnDst, float stoppingDst) {
        lookPoints = waypoints;
        turnBoundaries = new Line[lookPoints.length];
        finishLineIndex = turnBoundaries.length - 1;

        Vector2f previousPoint = V3ToV2 (startPos);
        for (int i = 0; i < lookPoints.length; i++) {
            Vector2f currentPoint = V3ToV2 (lookPoints [i]);
            Vector2f dirToCurrentPoint = (currentPoint.subtract(previousPoint)).normalize();
            Vector2f turnBoundaryPoint = (i == finishLineIndex)?currentPoint : currentPoint.subtract(dirToCurrentPoint.mult(turnDst));
            turnBoundaries [i] = new Line (turnBoundaryPoint, previousPoint.subtract(dirToCurrentPoint.mult(turnDst)));
            previousPoint = turnBoundaryPoint;
        }

        float dstFromEndPoint = 0;
        for (int i = lookPoints.length - 1; i > 0; i--) {
            dstFromEndPoint += lookPoints[i].distance(lookPoints [i - 1]);
            if (dstFromEndPoint > stoppingDst) {
                slowDownIndex = i;
                break;
            }
        }
    }

    Vector2f V3ToV2(Vector3f v3) {
        return new Vector2f (v3.x, v3.z);
    }
}
