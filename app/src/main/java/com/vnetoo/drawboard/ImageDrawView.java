package com.vnetoo.drawboard;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.shizhefei.view.largeimage.LargeImageView;
import com.vnetoo.drawboard.drawobject.DrawObject;

import java.util.ArrayList;
import java.util.List;
/**
 * 有画板功能的图片View
 * @anthor Li Hongcai
 * @time 2018/9/13 15:29
 */
public class ImageDrawView extends LargeImageView {
    public ImageDrawView(Context context) {
        super(context);
    }

    public ImageDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageDrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    int imageWidth = 0;
    int imageHeight = 0;

    @Override
    public void onLoadImageSize(int imageWidth, int imageHeight) {
        super.onLoadImageSize(imageWidth, imageHeight);
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        drawBoard = new DrawBoard(imageWidth,imageHeight);
        if(onDrawBoardLoadListener!=null){
            onDrawBoardLoadListener.onLoadDrawBoard(drawBoard);
        }
    }



    private int getContentWidth() {
        if (hasLoad()) {
            return (int) (getMeasuredWidth() * getScale());
        }
        return 0;
    }

    private int getContentHeight() {
        if (hasLoad()) {
            if (getImageWidth() == 0) {
                return 0;
            }
            return (int) (1.0f * getMeasuredWidth() * getImageHeight() / getImageWidth() * getScale());
        }
        return 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(imageWidth==0 || imageHeight==0 || drawBoard==null){
            return;
        }
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        if (viewWidth == 0) {
            return;
        }
        int drawOffsetX = 0;
        int drawOffsetY = 0;
        int contentWidth = getContentWidth();
        int contentHeight = getContentHeight();
        if (viewWidth > contentWidth) {
            drawOffsetX = (viewWidth - contentWidth) / 2;
        }
        if (viewHeight > contentHeight) {
            drawOffsetY = (viewHeight - contentHeight) / 2;
        }

        float width = getScale() * getWidth();
        float imgWidth = imageWidth;

        float imageScale = imgWidth / width;
        float _imageScale = 1.0f/imageScale;

        int saveCount = canvas.save();

        canvas.translate(drawOffsetX,drawOffsetY);
        canvas.scale(_imageScale,_imageScale);
        if(drawBoard!=null){
            drawBoard.draw(canvas);
        }
        canvas.restoreToCount(saveCount);
    }


    DrawBoard drawBoard;
    boolean isDrawBoard = false;
    List<OnDrawListener> onDrawListenerList = new ArrayList<>();
    OnDrawBoardLoadListener onDrawBoardLoadListener;

    static final float TOUCH_TOLERANCE = 6.0f;
    float mX,mY;
    DrawObject drawObject;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(isDrawBoard){
            if(imageWidth==0 || imageHeight==0 || drawBoard==null){
                return false;
            }else{
                float x = event.getX();
                float y = event.getY();

                int viewWidth = getWidth();
                int viewHeight = getHeight();
                if (viewWidth == 0) {
                    return false;
                }
                int drawOffsetX = 0;
                int drawOffsetY = 0;
                int contentWidth = getContentWidth();
                int contentHeight = getContentHeight();
                if (viewWidth > contentWidth) {
                    drawOffsetX = (viewWidth - contentWidth) / 2;
                }
                if (viewHeight > contentHeight) {
                    drawOffsetY = (viewHeight - contentHeight) / 2;
                }

                float width = getScale() * getWidth();
                float imgWidth = imageWidth;
                float imageScale = imgWidth / width;

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        mX = x;
                        mY = y;
                        drawObject = drawBoard.addDrawObject().addPoint(
                                Math.min(Math.max((int)((-drawOffsetX + getScrollX()+x)*imageScale),0),imageWidth),
                                Math.min(Math.max((int)((-drawOffsetY + getScrollY()+y)*imageScale),0),imageHeight)
                        );
                        invalidate();
                        for(OnDrawListener onDrawListener : onDrawListenerList){
                            onDrawListener.addDrawObject(drawObject);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dx = Math.abs(x - mX);
                        float dy = Math.abs(y - mY);
                        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE){
                            mX = x;
                            mY = y;
                            drawObject.addPoint(
                                    Math.min(Math.max((int)((-drawOffsetX + getScrollX()+x)*imageScale),0),imageWidth),
                                    Math.min(Math.max((int)((-drawOffsetY + getScrollY()+y)*imageScale),0),imageHeight)
                            );
                            invalidate();
                            for(OnDrawListener onDrawListener : onDrawListenerList){
                                onDrawListener.modifyDrawObject(drawObject);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mX = x;
                        mY = y;
                        drawObject.addPoint(
                                Math.min(Math.max((int)((-drawOffsetX + getScrollX()+x)*imageScale),0),imageWidth),
                                Math.min(Math.max((int)((-drawOffsetY + getScrollY()+y)*imageScale),0),imageHeight)
                        ).end();
                        invalidate();
                        for(OnDrawListener onDrawListener : onDrawListenerList){
                            onDrawListener.modifyDrawObject(drawObject);
                        }
                        break;
                }
                return true;
            }
        }else{
            return super.onTouchEvent(event);
        }
    }

    public void setDrawBoard(boolean drawBoard) {
        isDrawBoard = drawBoard;
    }

    public boolean isDrawBoard() {
        return isDrawBoard;
    }

    public interface OnDrawListener {

        void addDrawObject(DrawObject drawObject);

        void modifyDrawObject(DrawObject drawObject);

        void removeDrawObject(DrawObject drawObject);

    }

    public void addOnDrawListener(OnDrawListener onDrawListener){
        onDrawListenerList.add(onDrawListener);
    }

    public void removeOnDrawListener(OnDrawListener onDrawListener){
        onDrawListenerList.remove(onDrawListener);
    }

    public interface OnDrawBoardLoadListener{
        void onLoadDrawBoard(DrawBoard drawBoard);
    }

    public OnDrawBoardLoadListener getOnDrawBoardLoadListener() {
        return onDrawBoardLoadListener;
    }

    public void setOnDrawBoardLoadListener(OnDrawBoardLoadListener onDrawBoardLoadListener) {
        this.onDrawBoardLoadListener = onDrawBoardLoadListener;
    }
}
