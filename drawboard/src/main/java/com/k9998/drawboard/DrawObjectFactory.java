package com.k9998.drawboard;

import com.k9998.drawboard.drawobject.DrawObject;

/**
 * 提供绘制对象的工厂
 * @anthor Li Hongcai
 * @time 2018/9/13 15:30
 */
public interface DrawObjectFactory {

    DrawObject getDrawObject();

}
