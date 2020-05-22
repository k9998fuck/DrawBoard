package com.vnetoo.drawboard.drawobject;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
/**
 * @anthor Li Hongcai
 * @time 2018/9/13 15:32
 */
public class ArrowLineObject extends DrawObject{

    public ArrowLineObject(String id, int color, float strokeWidth, boolean dashed) {
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

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if(pointList.size()<2){
            return;
        }
        intPaint(paint);

        Point firstPoint = pointList.get(0);
        Point secondPoint = pointList.get(1);

        float px = secondPoint.x-firstPoint.x;
        float py = secondPoint.y-firstPoint.y;
        //箭头的高度，给的画笔越宽，度数越
        float arrowHeight = strokeWidth*6;
        // calculate the angle of the line
        //直线在坐标系中的角度
        double lineAngle = Math.atan2(py,px);
        //在原有的直线基础上，加上箭头的高度后，新的结束点的位
//        float newEndPoxX= (float) (arrowHeight* Math.cos(lineAngle)+secondPoint.x);
//        float newEndPoxY= (float) (arrowHeight* Math.sin(lineAngle)+secondPoint.y);
        float newStartPoxX= (float) (secondPoint.x-arrowHeight* Math.cos(lineAngle));
        float newStartPoxY= (float) (secondPoint.y-arrowHeight* Math.sin(lineAngle));
        float newEndPoxX= secondPoint.x;
        float newEndPoxY= secondPoint.y;



        //箭头与直线形成的夹角大小
        double arrowAngle = Math.PI/14;
        //箭头上部分在以结束点为中心的直角坐标系中的角
        double angle1 = Math.PI+lineAngle+arrowAngle;
        double angle2 = Math.PI+lineAngle-arrowAngle;

        // h is the line length of a side of the arrow head  计算斜边的长度
        double hypotenuse = Math.abs(arrowHeight/ Math.cos(arrowAngle)) ;

        //得到箭头上部分的点的坐标
        float topx1  =   (float) ((newEndPoxX+ Math.cos(angle1)*hypotenuse));
        float topy1  =   (float) ((newEndPoxY + Math.sin(angle1)*hypotenuse));

        //得到箭头下部分的点的坐标
        float botx  = (float) ((newEndPoxX+ Math.cos(angle2)*hypotenuse));
        float boty  = (float) ((newEndPoxY+ Math.sin(angle2)*hypotenuse));

        //绘制线
        Path path= new Path();
        path.moveTo(firstPoint.x, firstPoint.y);
        path.lineTo(newStartPoxX, newStartPoxY);
        canvas.drawPath(path, paint);

        //生成箭头的路
        path= new Path();
        path.moveTo(newEndPoxX, newEndPoxY);
        path.lineTo(topx1, topy1);
        path.lineTo(botx,boty);
        path.close();

        //画箭头
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, paint);
    }
}
