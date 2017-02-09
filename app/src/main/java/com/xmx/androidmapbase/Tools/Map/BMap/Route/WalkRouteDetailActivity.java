package com.xmx.androidmapbase.Tools.Map.BMap.Route;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.xmx.androidmapbase.R;
import com.xmx.androidmapbase.Tools.Map.BMap.Utils.BMapUtil;

public class WalkRouteDetailActivity extends Activity {
	private WalkingRouteLine mWalkPath;
	private TextView mTitle,mTitleWalkRoute;
	private ListView mWalkSegmentList;
	private WalkSegmentListAdapter mWalkSegmentListAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bmap_route_detail);
		getIntentData();
		mTitle = (TextView) findViewById(R.id.title_center);
		mTitle.setText("步行路线详情");
		mTitleWalkRoute = (TextView) findViewById(R.id.firstline);
		int dis = mWalkPath.getDistance();
		int dur = mWalkPath.getDuration();
		String des = BMapUtil.getFriendlyTime(dur) + "(" + BMapUtil.getFriendlyLength(dis) + ")";
		mTitleWalkRoute.setText(des);
		mWalkSegmentList = (ListView) findViewById(R.id.bus_segment_list);
		mWalkSegmentListAdapter = new WalkSegmentListAdapter(
				this.getApplicationContext(), mWalkPath.getAllStep());
		mWalkSegmentList.setAdapter(mWalkSegmentListAdapter);

	}

	private void getIntentData() {
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		mWalkPath = intent.getParcelableExtra("walk_path");
	}

	public void onBackClick(View view) {
		this.finish();
	}

}
