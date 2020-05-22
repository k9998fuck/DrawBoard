package com.vnetoo.drawboard.demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.vnetoo.drawboard.DrawBoard;
import com.vnetoo.drawboard.ImageDrawView;
import com.vnetoo.drawboard.PhotoDrawView;
import com.vnetoo.drawboard.R;
import com.vnetoo.drawboard.drawobject.DrawObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * @anthor Li Hongcai
 * @time 2018/9/13 15:30
 */
public class MainActivity extends AppCompatActivity implements ColorPickerFragment.OnColorChangedListener{

    private PhotoDrawView imageDrawView;
    private ToggleButton toggleButton;
    private View undoView,redoView;
    private Spinner typeSpinner;
    private Spinner widthSpinner;
    private View colorView;
    private ColorPickerFragment colorPickerFragment;
    private DrawBoard drawBoard;
    private SimpleDrawObjectFactory simpleDrawObjectFactory = new SimpleDrawObjectFactory();

    List<DrawObject> redoDrawObjectList = new ArrayList<>();
    String[] typeNames = new String[]{"直线","虚线"
            ,"直线箭头","虚线箭头"
            ,"画图","虚线画图"
            ,"椭圆","实心椭圆"
            ,"矩形","实心矩形"
            ,"圆角矩形","实心圆角矩形"};
    int[] types = new int[]{SimpleDrawObjectFactory.LINE,SimpleDrawObjectFactory.LINE_DASHED
            ,SimpleDrawObjectFactory.ARROWLINE,SimpleDrawObjectFactory.ARROWLINE_DASHED
            ,SimpleDrawObjectFactory.FREESTYLELINE,SimpleDrawObjectFactory.FREESTYLELINE
            ,SimpleDrawObjectFactory.ELLIPSE,SimpleDrawObjectFactory.ELLIPSE_SOLID
            ,SimpleDrawObjectFactory.RECT,SimpleDrawObjectFactory.RECT_SOLID
            ,SimpleDrawObjectFactory.ROUNDRECT,SimpleDrawObjectFactory.ROUNDRECT_SOLID};
    String[] widthNames = new String[]{"1px","2px"
            ,"3px","4px"
            ,"5px","6px"
            ,"7px","8px"};
    int[] widths = new int[]{1,2
            ,3,4
            ,5,6
            ,7,8
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageDrawView = findViewById(R.id.imageView);
        toggleButton = findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                imageDrawView.setDrawBoard(isChecked);
            }
        });
        undoView = findViewById(R.id.undo);
        redoView = findViewById(R.id.redo);
        typeSpinner = findViewById(R.id.type);
        typeSpinner.setAdapter(new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1, typeNames));
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                simpleDrawObjectFactory.setType(types[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        widthSpinner = findViewById(R.id.width);
        widthSpinner.setAdapter(new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1, widthNames));
        widthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                simpleDrawObjectFactory.setStrokeWidth(widths[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        widthSpinner.setSelection(7);
        colorView = findViewById(R.id.color);
        colorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(colorPickerFragment==null){
                    colorPickerFragment = new ColorPickerFragment();
                }
                colorPickerFragment.show(getSupportFragmentManager(),"ColorPickerFragment");
            }
        });
        colorView.setBackgroundColor(Color.BLACK);
        simpleDrawObjectFactory.setColor(Color.BLACK);

        imageDrawView.setOnDrawBoardLoadListener(new ImageDrawView.OnDrawBoardLoadListener() {
            @Override
            public void onLoadDrawBoard(DrawBoard drawBoard) {
                MainActivity.this.drawBoard = drawBoard;
                //加载画板时调用，这里要提供给画板一个生产绘制对象的工厂对象
                drawBoard.setDrawObjectFactory(simpleDrawObjectFactory);
                //可以加载默认的已有的绘制对象
                drawBoard.addDrawObject().addPoint(100,100).addPoint(200,100).end();
                //有加载绘制对象时需要主动刷新View
                imageDrawView.postInvalidate();
                //初始化控制面板状态
                initPanel();
            }
        });
        imageDrawView.addOnDrawListener(new ImageDrawView.OnDrawListener() {
            @Override
            public void addDrawObject(DrawObject drawObject) {
                redoDrawObjectList.clear();
                initPanel();
            }

            @Override
            public void modifyDrawObject(DrawObject drawObject) {

            }

            @Override
            public void removeDrawObject(DrawObject drawObject) {
            }



        });
//        try {
//            InputStream inputStream = getAssets().open("aaa.jpg");
//            imageDrawView.setImage(new InputStreamBitmapDecoderFactory(inputStream));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getResources().getAssets().open("111.jpg"));
            imageDrawView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Glide.with(this)
//                .load("file:///android_asset/aaa.jpg")
//                .thumbnail(0.2f)
//                .into(imageDrawView);

    }

    public void undo(View view){
        if(drawBoard==null)return;
        List<DrawObject> drawObjectList = drawBoard.getDrawObjectList();
        if(drawObjectList.size()>0){
            DrawObject drawObject = drawObjectList.get(drawObjectList.size()-1);
            drawBoard.removeDrawObject(drawObject);
            redoDrawObjectList.add(drawObject);
            imageDrawView.invalidate();
        }
        initPanel();
    }

    public void redo(View view){
        if(redoDrawObjectList.size()>0){
            DrawObject drawObject = redoDrawObjectList.remove(redoDrawObjectList.size()-1);
            drawBoard.addDrawObject(drawObject);
            imageDrawView.invalidate();
        }
        initPanel();
    }

    void initPanel(){
        if(drawBoard==null)return;
        //初始化控制面板状态
        if(drawBoard.getDrawObjectList().size()>0){
            undoView.setEnabled(true);
        }else{
            undoView.setEnabled(false);
        }
        if(redoDrawObjectList.size()>0){
            redoView.setEnabled(true);
        }else{
            redoView.setEnabled(false);
        }
    }


    @Override
    public void colorChanged(int color) {
        simpleDrawObjectFactory.setColor(color);
        colorView.setBackgroundColor(color);
    }
}
