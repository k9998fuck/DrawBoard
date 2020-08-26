package com.k9998.drawboard.drawobject;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * @anthor Li Hongcai
 * @time 2018/9/13 15:32
 */
public abstract class DrawObject {

    String id;
    int color;
    float strokeWidth;
    boolean dashed;

    List<Point> pointList = new ArrayList<>();
    boolean end = false;

    public DrawObject(String id, int color, float strokeWidth, boolean dashed) {
        this.id = id;
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.dashed = dashed;
    }

    public DrawObject addPoint(int x, int y){
        pointList.add(new Point(x,y));
        return this;
    }

    public void end(){
        end = true;
    }

    public boolean isEnd(){
        return end;
    }

    public void intPaint(Paint paint){
        paint.reset();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);
        if(dashed){
            paint.setPathEffect(new DashPathEffect(new float[]{strokeWidth*4,strokeWidth*4}, 0));
        }
    }

    public abstract void draw(Canvas canvas, Paint paint);

    @Override
    public boolean equals(Object obj) {
        if(obj==null){
            return false;
        }else if(obj instanceof DrawObject){
            return ((DrawObject)obj).id.equals(id);
        }else {
            return false;
        }
    }
}
