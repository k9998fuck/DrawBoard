package com.k9998.drawboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.github.chrisbanes.photoview.PhotoView;
import com.k9998.drawboard.drawobject.DrawObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 有画板功能的图片View
 * @anthor Li Hongcai
 * @time 2018/9/13 15:29
 */
public class PhotoDrawView extends PhotoView {
    public PhotoDrawView(Context context) {
        super(context);
        init();
    }

    public PhotoDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PhotoDrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    boolean init = false;
    void init(){
        if(!init){
            init = true;
            setOnTouchListener(onTouchListener);
        }
    }

    Matrix drawBoardMatrix = new Matrix();

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if(drawable!=null && drawable instanceof BitmapDrawable){
            BitmapDrawable bitmapDrawable = (BitmapDrawable)drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            drawBoard = new DrawBoard(bitmap.getWidth(),bitmap.getHeight());
            drawBoardMatrix.setScale((float) bitmap.getWidth()/(float)drawBoard.getWidth(),(float)bitmap.getHeight()/(float)drawBoard.getHeight());
            if(onDrawBoardLoadListener!=null){
                onDrawBoardLoadListener.onLoadDrawBoard(drawBoard);
            }
        }else{
            drawBoard = null;
        }
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(drawBoard==null){
            return;
        }



        Matrix mDrawMatrix = getImageMatrix();

        if (mDrawMatrix == null && getPaddingTop() == 0 && getPaddingLeft() == 0) {
            drawBoard.draw(canvas);
        } else {
            final int saveCount = canvas.getSaveCount();
            canvas.save();

            if (getCropToPadding()) {
                final int scrollX = getScrollX();
                final int scrollY = getScrollY();
                canvas.clipRect(scrollX + getPaddingLeft(), scrollY + getPaddingTop(),
                        scrollX + getRight() - getLeft() - getPaddingRight(),
                        scrollY + getBottom() - getTop() - getPaddingBottom());
            }

            canvas.translate(getPaddingLeft(), getPaddingTop());

            if (mDrawMatrix != null) {
                canvas.concat(mDrawMatrix);
            }

            canvas.concat(drawBoardMatrix);
            drawBoard.draw(canvas);
            canvas.restoreToCount(saveCount);
        }
    }


    DrawBoard drawBoard;
    boolean isDrawBoard = false;
    List<ImageDrawView.OnDrawListener> onDrawListenerList = new ArrayList<>();
    ImageDrawView.OnDrawBoardLoadListener onDrawBoardLoadListener;

    static final float TOUCH_TOLERANCE = 6.0f;
    float mX,mY;
    DrawObject drawObject;

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(isDrawBoard){
                if(drawBoard==null){
                    return false;
                }else{

                    final RectF displayRect = getDisplayRect();
                    if (displayRect == null) {
                        return false;
                    }

                    final float x = event.getX(), y = event.getY();

                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            mX = x;
                            mY = y;
                            drawObject = drawBoard.addDrawObject().addPoint(
                                    Math.min(Math.max((int)(drawBoard.getWidth() * ((x - displayRect.left) / displayRect.width())),0),drawBoard.getWidth()),
                                    Math.min(Math.max((int)(drawBoard.getHeight() * ((y - displayRect.top) / displayRect.height())),0),drawBoard.getHeight())
                            );
                            invalidate();
                            for(ImageDrawView.OnDrawListener onDrawListener : onDrawListenerList){
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
                                        Math.min(Math.max((int)(drawBoard.getWidth() * ((x - displayRect.left) / displayRect.width())),0),drawBoard.getWidth()),
                                        Math.min(Math.max((int)(drawBoard.getHeight() * ((y - displayRect.top) / displayRect.height())),0),drawBoard.getHeight())
                                );
                                invalidate();
                                for(ImageDrawView.OnDrawListener onDrawListener : onDrawListenerList){
                                    onDrawListener.modifyDrawObject(drawObject);
                                }
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            mX = x;
                            mY = y;
                            drawObject.addPoint(
                                    Math.min(Math.max((int)(drawBoard.getWidth() * ((x - displayRect.left) / displayRect.width())),0),drawBoard.getWidth()),
                                    Math.min(Math.max((int)(drawBoard.getHeight() * ((y - displayRect.top) / displayRect.height())),0),drawBoard.getHeight())
                            ).end();
                            invalidate();
                            for(ImageDrawView.OnDrawListener onDrawListener : onDrawListenerList){
                                onDrawListener.modifyDrawObject(drawObject);
                            }
                            break;
                    }
                    return true;
                }
            }else{
                return getAttacher().onTouch(v,event);
            }
        }
    };


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("333","333");
        return super.onTouchEvent(event);
    }

    public void setDrawBoard(boolean drawBoard) {
        isDrawBoard = drawBoard;
    }

    public boolean isDrawBoard() {
        return isDrawBoard;
    }


    public void addOnDrawListener(ImageDrawView.OnDrawListener onDrawListener){
        onDrawListenerList.add(onDrawListener);
    }

    public void removeOnDrawListener(ImageDrawView.OnDrawListener onDrawListener){
        onDrawListenerList.remove(onDrawListener);
    }


    public ImageDrawView.OnDrawBoardLoadListener getOnDrawBoardLoadListener() {
        return onDrawBoardLoadListener;
    }

    public void setOnDrawBoardLoadListener(ImageDrawView.OnDrawBoardLoadListener onDrawBoardLoadListener) {
        this.onDrawBoardLoadListener = onDrawBoardLoadListener;
    }
}
