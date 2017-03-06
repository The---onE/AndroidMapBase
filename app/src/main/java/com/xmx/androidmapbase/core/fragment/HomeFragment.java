package com.xmx.androidmapbase.core.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.xmx.androidmapbase.module.map.BMapActivity;
import com.xmx.androidmapbase.module.map.AMapActivity;
import com.xmx.androidmapbase.module.map.AMapPOIActivity;
import com.xmx.androidmapbase.module.map.AMapRouteActivity;
import com.xmx.androidmapbase.module.map.BMapPOIActivity;
import com.xmx.androidmapbase.module.map.BMapRouteActivity;
import com.xmx.androidmapbase.R;
import com.xmx.androidmapbase.base.fragment.xUtilsFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_home)
public class HomeFragment extends xUtilsFragment {
    @Event(value = R.id.btn_amap)
    private void onClickAMapTest(View view) {
        startActivity(AMapActivity.class);
    }

    @Event(value = R.id.btn_amap_poi)
    private void onClickAMapPOITest(View view) {
        startActivity(AMapPOIActivity.class);
    }

    @Event(value = R.id.btn_amap_route)
    private void onClickAMapRouteTest(View view) {
        startActivity(AMapRouteActivity.class);
    }

    @Event(value = R.id.btn_bmap)
    private void onClickBMapTest(View view) {
        startActivity(BMapActivity.class);
    }

    @Event(value = R.id.btn_bmap_poi)
    private void onClickBMapPOITest(View view) {
        startActivity(BMapPOIActivity.class);
    }

    @Event(value = R.id.btn_bmap_route)
    private void onClickBMapRouteTest(View view) {
        startActivity(BMapRouteActivity.class);
    }


    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

}
