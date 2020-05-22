package com.vnetoo.drawboard.drawobject;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
/**
 * @anthor Li Hongcai
 * @time 2018/9/13 15:32
 */
public class RoundRectObject extends RectObject{

    float radius;

    public RoundRectObject(String id, int color, float strokeWidth, boolean dashed, boolean solid, float radius) {
        super(id, color, strokeWidth, dashed, solid);
        this.radius = radius;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if(pointList.size()<2){
            return;
        }
        intPaint(paint);

        Point firstPoint = pointList.get(0);
        Point secondPoint = pointList.get(1);

        RectF rectF = new RectF();
        rectF.set(firstPoint.x, firstPoint.y, secondPoint.x, secondPoint.y);
        float temp;
        if (rectF.left > rectF.right) {
            temp = rectF.left;
            rectF.left = rectF.right;
            rectF.right = temp;
        }
        if (rectF.top > rectF.bottom) {
            temp = rectF.bottom;
            rectF.bottom = rectF.top;
            rectF.top = temp;
        }
        canvas.drawRoundRect(rectF, radius, radius, paint);
    }
}
