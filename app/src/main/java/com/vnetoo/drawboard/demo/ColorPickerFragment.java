/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vnetoo.drawboard.demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
/**
 * @anthor Li Hongcai
 * @time 2018/9/13 15:29
 */
public class ColorPickerFragment extends DialogFragment {

    public interface OnColorChangedListener {
        void colorChanged(int color);
    }

    private OnColorChangedListener l = new OnColorChangedListener() {
        public void colorChanged(int color) {
            if(getTargetFragment() instanceof OnColorChangedListener){
                ((OnColorChangedListener)getTargetFragment()).colorChanged(color);
            }
            if(getParentFragment() instanceof OnColorChangedListener){
                ((OnColorChangedListener)getParentFragment()).colorChanged(color);
            }
            if(getActivity() instanceof OnColorChangedListener){
                ((OnColorChangedListener)getActivity()).colorChanged(color);
            }
            dismiss();
        }
    };
    private int mInitialColor = Color.BLACK;

    private static class ColorPickerView extends View {
        private Paint mPaint;
        private Paint mCenterPaint;
        private final int[] mColors;
        private OnColorChangedListener mListener;
        
        ColorPickerView(Context c, OnColorChangedListener l, int color) {
            super(c);
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);

            WIDTH = outMetrics.widthPixels;
            HEIGHT = outMetrics.heightPixels;
            CENTER_X = WIDTH/2;
            CENTER_Y = HEIGHT/2;
            CENTER_RADIUS = CENTER_X*32/100;

            mListener = l;
            mColors = new int[] {
                0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF, 0xFF00FF00,
                0xFFFFFF00, 0xFFFF0000
            };
            Shader s = new SweepGradient(0, 0, mColors, null);
            
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setShader(s);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(CENTER_RADIUS);
            
            mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mCenterPaint.setColor(color);
            mCenterPaint.setStrokeWidth(5);
        }
        
        private boolean mTrackingCenter;
        private boolean mHighlightCenter;

        @Override 
        protected void onDraw(Canvas canvas) {
            float r = CENTER_X - mPaint.getStrokeWidth()*0.5f;
            
            canvas.translate(CENTER_X, CENTER_X);
            
            canvas.drawOval(new RectF(-r, -r, r, r), mPaint);            
            canvas.drawCircle(0, 0, CENTER_RADIUS, mCenterPaint);
            
            if (mTrackingCenter) {
                int c = mCenterPaint.getColor();
                mCenterPaint.setStyle(Paint.Style.STROKE);
                
                if (mHighlightCenter) {
                    mCenterPaint.setAlpha(0xFF);
                } else {
                    mCenterPaint.setAlpha(0x80);
                }
                canvas.drawCircle(0, 0,
                                  CENTER_RADIUS + mCenterPaint.getStrokeWidth(),
                                  mCenterPaint);
                
                mCenterPaint.setStyle(Paint.Style.FILL);
                mCenterPaint.setColor(c);
            }
        }
        
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);

            switch (widthMode){
                case MeasureSpec.UNSPECIFIED:
                    width = WIDTH;
                    break;
                case MeasureSpec.EXACTLY:
                    break;
                case MeasureSpec.AT_MOST:
                    width = Math.min(width,WIDTH);
                    break;
            }
            switch (heightMode){
                case MeasureSpec.UNSPECIFIED:
                    height = HEIGHT;
                    break;
                case MeasureSpec.EXACTLY:
                    break;
                case MeasureSpec.AT_MOST:
                    height = Math.min(height,HEIGHT);
                    break;
            }

            width = height = Math.min(width,height);
            CENTER_X = width/2;
            CENTER_Y = height/2;
            CENTER_RADIUS = CENTER_X*32/100;
            setMeasuredDimension(width, height);


        }

        int WIDTH = 400;
        int HEIGHT = 400;

        int CENTER_X = 200;
        int CENTER_Y = 200;
        int CENTER_RADIUS = 64;

        private int floatToByte(float x) {
            int n = Math.round(x);
            return n;
        }
        private int pinToByte(int n) {
            if (n < 0) {
                n = 0;
            } else if (n > 255) {
                n = 255;
            }
            return n;
        }
        
        private int ave(int s, int d, float p) {
            return s + Math.round(p * (d - s));
        }
        
        private int interpColor(int colors[], float unit) {
            if (unit <= 0) {
                return colors[0];
            }
            if (unit >= 1) {
                return colors[colors.length - 1];
            }
            
            float p = unit * (colors.length - 1);
            int i = (int)p;
            p -= i;

            // now p is just the fractional part [0...1) and i is the index
            int c0 = colors[i];
            int c1 = colors[i+1];
            int a = ave(Color.alpha(c0), Color.alpha(c1), p);
            int r = ave(Color.red(c0), Color.red(c1), p);
            int g = ave(Color.green(c0), Color.green(c1), p);
            int b = ave(Color.blue(c0), Color.blue(c1), p);
            
            return Color.argb(a, r, g, b);
        }
        
        private int rotateColor(int color, float rad) {
            float deg = rad * 180 / 3.1415927f;
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);
            
            ColorMatrix cm = new ColorMatrix();
            ColorMatrix tmp = new ColorMatrix();

            cm.setRGB2YUV();
            tmp.setRotate(0, deg);
            cm.postConcat(tmp);
            tmp.setYUV2RGB();
            cm.postConcat(tmp);
            
            final float[] a = cm.getArray();

            int ir = floatToByte(a[0] * r +  a[1] * g +  a[2] * b);
            int ig = floatToByte(a[5] * r +  a[6] * g +  a[7] * b);
            int ib = floatToByte(a[10] * r + a[11] * g + a[12] * b);
            
            return Color.argb(Color.alpha(color), pinToByte(ir),
                              pinToByte(ig), pinToByte(ib));
        }
        
        private static final float PI = 3.1415926f;

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX() - CENTER_X;
            float y = event.getY() - CENTER_Y;
            Log.d("onTouchEvent",String.format("event.getX()=%s event.getY()=%s x=%s y=%s",event.getX(),event.getY(),x,y));
            boolean inCenter = Math.sqrt(x*x + y*y) <= CENTER_RADIUS;
            Log.d("onTouchEvent","inCenter="+inCenter);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mTrackingCenter = inCenter;
                    if (inCenter) {
                        mHighlightCenter = true;
                        invalidate();
                        break;
                    }
                case MotionEvent.ACTION_MOVE:
                    if (mTrackingCenter) {
                        if (mHighlightCenter != inCenter) {Log.d("onTouchEvent","mHighlightCenter="+mHighlightCenter);
                            mHighlightCenter = inCenter;
                            invalidate();
                        }
                    } else {
                        float angle = (float) Math.atan2(y, x);
                        // need to turn angle [-PI ... PI] into unit [0....1]
                        float unit = angle/(2*PI);
                        if (unit < 0) {
                            unit += 1;
                        }
                        mCenterPaint.setColor(interpColor(mColors, unit));
                        invalidate();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (mTrackingCenter) {
                        if (inCenter) {
                            mListener.colorChanged(mCenterPaint.getColor());
                        }
                        mTrackingCenter = false;    // so we draw w/o halo
                        invalidate();
                    }
                    break;
            }
            return true;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return new ColorPickerView(getContext(), l, mInitialColor);
    }

}
