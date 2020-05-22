package com.vnetoo.drawboard.drawobject;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
/**
 * @anthor Li Hongcai
 * @time 2018/9/13 15:32
 */
public class FreeStyleLineObject extends DrawObject {


    public FreeStyleLineObject(String id, int color, float strokeWidth,boolean dashed) {
        super(id, color, strokeWidth, dashed);
    }

    @Override
    public void draw(Canvas canvas, Paint paint){
        if(pointList.size()<2){
            return;
        }
        intPaint(paint);
        Path path = new Path();
        int pointCount = pointList.size();
        Point point;
        for (int i = 0; i < pointCount; i++) {
            point = pointList.get(i);
            if (i == 0) {
                path.moveTo(point.x,point.y);
            }else{
                path.lineTo(point.x,point.y);
            }
        }
        canvas.drawPath(path, paint);
    }

}
