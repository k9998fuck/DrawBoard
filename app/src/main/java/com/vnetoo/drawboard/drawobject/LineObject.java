package com.vnetoo.drawboard.drawobject;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
/**
 * @anthor Li Hongcai
 * @time 2018/9/13 15:32
 */
public class LineObject extends FreeStyleLineObject{

    public LineObject(String id, int color, float strokeWidth, boolean dashed) {
        super(id, color, strokeWidth, dashed);
    }

    @Override
    public DrawObject addPoint(int x, int y) {
        if(pointList.size()>=2){
            Point point = pointList.get(1);
            point.x = x;
            point.y = y;
        }else{
            pointList.add(new Point(x,y));
        }
        return this;
    }
}
