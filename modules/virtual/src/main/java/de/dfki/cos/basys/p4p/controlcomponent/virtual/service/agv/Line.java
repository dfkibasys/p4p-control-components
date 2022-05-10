package de.dfki.cos.basys.p4p.controlcomponent.virtual.service.agv;

import com.jme3.math.Vector2f;

public class Line {

    final float verticalLineGradient = 1e5f;

    float gradient;
    float y_intercept;
    Vector2f pointOnLine_1;
    Vector2f pointOnLine_2;

    float gradientPerpendicular;

    boolean approachSide;

    public Line(Vector2f pointOnLine, Vector2f pointPerpendicularToLine) {
        float dx = pointOnLine.x - pointPerpendicularToLine.x;
        float dy = pointOnLine.y - pointPerpendicularToLine.y;

        if (dx == 0) {
            gradientPerpendicular = verticalLineGradient;
        } else {
            gradientPerpendicular = dy / dx;
        }

        if (gradientPerpendicular == 0) {
            gradient = verticalLineGradient;
        } else {
            gradient = -1 / gradientPerpendicular;
        }

        y_intercept = pointOnLine.y - gradient * pointOnLine.x;
        pointOnLine_1 = pointOnLine;
        pointOnLine_2 = pointOnLine.add(new Vector2f (1, gradient));

        approachSide = false;
        approachSide = GetSide (pointPerpendicularToLine);
    }

    boolean GetSide(Vector2f p) {
        return (p.x - pointOnLine_1.x) * (pointOnLine_2.y - pointOnLine_1.y) > (p.y - pointOnLine_1.y) * (pointOnLine_2.x - pointOnLine_1.x);
    }

    public boolean HasCrossedLine(Vector2f p) {
        return GetSide (p) != approachSide;
    }

    public float DistanceFromPoint(Vector2f p) {
        float yInterceptPerpendicular = p.y - gradientPerpendicular * p.x;
        float intersectX = (yInterceptPerpendicular - y_intercept) / (gradient - gradientPerpendicular);
        float intersectY = gradient * intersectX + y_intercept;
        return p.distance(new Vector2f (intersectX, intersectY));
    }
}
