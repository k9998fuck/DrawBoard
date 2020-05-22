package com.vnetoo.drawboard.demo;

import android.graphics.Color;
import android.support.annotation.IntDef;

import com.vnetoo.drawboard.DrawObjectFactory;
import com.vnetoo.drawboard.drawobject.ArrowLineObject;
import com.vnetoo.drawboard.drawobject.DrawObject;
import com.vnetoo.drawboard.drawobject.EllipseObject;
import com.vnetoo.drawboard.drawobject.FreeStyleLineObject;
import com.vnetoo.drawboard.drawobject.LineObject;
import com.vnetoo.drawboard.drawobject.RectObject;
import com.vnetoo.drawboard.drawobject.RoundRectObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.UUID;
/**
 * @anthor Li Hongcai
 * @time 2018/9/13 15:31
 */
public class SimpleDrawObjectFactory implements DrawObjectFactory {

    public static final int LINE = 1;
    public static final int LINE_DASHED = 2;
    public static final int ARROWLINE = 3;
    public static final int ARROWLINE_DASHED = 4;
    public static final int FREESTYLELINE = 5;
    public static final int FREESTYLELINE_DASHED = 6;
    public static final int ELLIPSE = 7;
    public static final int ELLIPSE_SOLID = 8;
    public static final int RECT = 9;
    public static final int RECT_SOLID = 10;
    public static final int ROUNDRECT = 11;
    public static final int ROUNDRECT_SOLID = 12;

    @IntDef({LINE, LINE_DASHED, ARROWLINE, ARROWLINE_DASHED,
            FREESTYLELINE, FREESTYLELINE_DASHED, ELLIPSE, ELLIPSE_SOLID,
            RECT, RECT_SOLID, ROUNDRECT, ROUNDRECT_SOLID})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {

    }

    int type = FREESTYLELINE;
    int color = Color.BLACK;
    float strokeWidth = 6.0f;

    @Override
    public DrawObject getDrawObject() {
        String id = UUID.randomUUID().toString().replace("-", "");
        DrawObject drawObject;
        switch (type){
            case LINE:
                drawObject = new LineObject(id,color,strokeWidth,false);
                break;
            case LINE_DASHED:
                drawObject = new LineObject(id,color,strokeWidth,true);
                break;
            case ARROWLINE:
                drawObject = new ArrowLineObject(id,color,strokeWidth,false);
                break;
            case ARROWLINE_DASHED:
                drawObject = new ArrowLineObject(id,color,strokeWidth,true);
                break;
            case FREESTYLELINE:
                drawObject = new FreeStyleLineObject(id,color,strokeWidth,false);
                break;
            case FREESTYLELINE_DASHED:
                drawObject = new FreeStyleLineObject(id,color,strokeWidth,true);
                break;
            case ELLIPSE:
                drawObject = new EllipseObject(id,color,strokeWidth,false,false);
                break;
            case ELLIPSE_SOLID:
                drawObject = new EllipseObject(id,color,strokeWidth,false,true);
                break;
            case RECT:
                drawObject = new RectObject(id,color,strokeWidth,false,false);
                break;
            case RECT_SOLID:
                drawObject = new RectObject(id,color,strokeWidth,false,true);
                break;
            case ROUNDRECT:
                drawObject = new RoundRectObject(id,color,strokeWidth,false,false,12);
                break;
            case ROUNDRECT_SOLID:
                drawObject = new RoundRectObject(id,color,strokeWidth,false,true,12);
                break;
            default:
                drawObject = new FreeStyleLineObject(id,color,strokeWidth,false);
                break;
        }
        return drawObject;
    }

    public int getType() {
        return type;
    }

    public void setType(@Type int type) {
        this.type = type;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }
}
