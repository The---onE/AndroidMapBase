package com.xmx.androidmapbase.module.map;

import android.graphics.Color;

import com.xmx.androidmapbase.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The_onE on 2017/3/1.
 * 收藏类型管理，用于将字符串与图标对应，单例对象
 */
public class AMapConstantsManager {
    private static AMapConstantsManager instance;

    public synchronized static AMapConstantsManager getInstance() {
        if (null == instance) {
            instance = new AMapConstantsManager();
        }
        return instance;
    }

    Map<String, Integer> typeMap = new LinkedHashMap<>();

    Map<String, Integer> colorMap = new LinkedHashMap<>();

    private AMapConstantsManager() {
        typeMap.put("一般", R.drawable.collection);
        typeMap.put("特殊", R.drawable.selected);
        typeMap.put("1", R.drawable.poi_marker_1);
        typeMap.put("2", R.drawable.poi_marker_2);
        typeMap.put("3", R.drawable.poi_marker_3);
        typeMap.put("4", R.drawable.poi_marker_4);
        typeMap.put("5", R.drawable.poi_marker_5);
        typeMap.put("6", R.drawable.poi_marker_6);
        typeMap.put("7", R.drawable.poi_marker_7);
        typeMap.put("8", R.drawable.poi_marker_8);
        typeMap.put("9", R.drawable.poi_marker_9);
        typeMap.put("10", R.drawable.poi_marker_10);
        typeMap.put("A", R.drawable.icon_marka);
        typeMap.put("B", R.drawable.icon_markb);
        typeMap.put("C", R.drawable.icon_markc);
        typeMap.put("D", R.drawable.icon_markd);
        typeMap.put("E", R.drawable.icon_marke);
        typeMap.put("F", R.drawable.icon_markf);
        typeMap.put("G", R.drawable.icon_markg);
        typeMap.put("H", R.drawable.icon_markh);
        typeMap.put("I", R.drawable.icon_marki);
        typeMap.put("J", R.drawable.icon_markj);

        colorMap.put("黑色", Color.BLACK);
        colorMap.put("白色", Color.WHITE);
        colorMap.put("红色", Color.RED);
        colorMap.put("蓝色", Color.BLUE);
        colorMap.put("绿色", Color.GREEN);
        colorMap.put("黄色", Color.YELLOW);
        colorMap.put("青色", Color.CYAN);
        colorMap.put("品红色", Color.MAGENTA);
        colorMap.put("灰色", Color.GRAY);
        colorMap.put("暗灰色", Color.DKGRAY);
        colorMap.put("亮灰色", Color.LTGRAY);
    }

    /**
     * 根据类型名称获取图标ID
     *
     * @return 图标drawable ID
     * @param[type] 类型名称
     */
    Integer getIconId(String type) {
        return typeMap.get(type);
    }

    /**
     * 获取类型名称列表
     *
     * @return 类型名称列表
     */
    List<String> getTypeList() {
        return new ArrayList<>(typeMap.keySet());
    }

    /**
     * 根据颜色名称获取颜色代码
     *
     * @return 颜色代码
     * @param[color] 颜色名称
     */
    Integer getColor(String color) {
        return colorMap.get(color);
    }

    /**
     * 根据颜色代码获取颜色名称
     *
     * @param[color] 颜色代码
     */
    String findColorName(int color) {
        // 找到所有值为该颜色代码的键值对，返回第一个键
        for (String key : colorMap.keySet()) {
            int value = colorMap.get(key);
            if (value == color) {
                return key;
            }
        }
        // 若未找到则返回空
        return null;
    }


    /**
     * 获取颜色名称列表
     *
     * @return 颜色名称列表
     */
    List<String> getColorList() {
        return new ArrayList<>(colorMap.keySet());
    }
}
