package com.xmx.androidmapbase.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.xmx.androidmapbase.BaseMap.MapActivity;
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

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

}
