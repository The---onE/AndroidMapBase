package com.xmx.androidmapbase.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.xmx.androidmapbase.BaseMap.BMapActivity;
import com.xmx.androidmapbase.BaseMap.MapActivity;
import com.xmx.androidmapbase.BaseMap.MapPOIActivity;
import com.xmx.androidmapbase.BaseMap.MapRouteActivity;
import com.xmx.androidmapbase.R;
import com.xmx.androidmapbase.Tools.FragmentBase.xUtilsFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_home)
public class HomeFragment extends xUtilsFragment {
    @Event(value = R.id.btn_map)
    private void onClickMapTest(View view) {
        startActivity(MapActivity.class);
    }

    @Event(value = R.id.btn_map_poi)
    private void onClickMapPOITest(View view) {
        startActivity(MapPOIActivity.class);
    }

    @Event(value = R.id.btn_map_route)
    private void onClickMapRouteTest(View view) {
        startActivity(MapRouteActivity.class);
    }

    @Event(value = R.id.btn_bmap)
    private void onClickBMapTest(View view) {
        startActivity(BMapActivity.class);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

}
