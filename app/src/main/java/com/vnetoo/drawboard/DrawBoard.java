package com.vnetoo.drawboard;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.vnetoo.drawboard.drawobject.DrawObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 画板
 * @anthor Li Hongcai
 * @time 2018/9/13 15:30
 */
public class DrawBoard {

    int width,height;
    Paint paint;

    DrawObjectFactory drawObjectFactory;
    List<DrawObject> drawObjectList = new ArrayList<>();

    public DrawBoard(int width,int height){
        this.width = width;
        this.height = height;
        this.paint = new Paint();
    }

    public void setDrawObjectFactory(DrawObjectFactory drawObjectFactory){
        this.drawObjectFactory = drawObjectFactory;
    }

    public DrawObject addDrawObject(){
        DrawObject drawObject = drawObjectFactory.getDrawObject();
        drawObjectList.add(drawObject);
        return drawObject;
    }

    public boolean addDrawObject(DrawObject drawObject){
        if(drawObjectList.indexOf(drawObject)!=-1){
            return false;
        }else{
            return drawObjectList.add(drawObject);
        }
    }

    public boolean removeDrawObject(DrawObject drawObject){
        return drawObjectList.remove(drawObject);
    }

    public List<DrawObject> getDrawObjectList() {
        return new ArrayList<>(drawObjectList);
    }

    public void draw(Canvas canvas){
        for(DrawObject drawObject : drawObjectList){
            drawObject.draw(canvas,paint);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
