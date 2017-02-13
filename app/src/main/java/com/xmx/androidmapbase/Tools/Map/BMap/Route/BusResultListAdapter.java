package com.xmx.androidmapbase.Tools.Map.BMap.Route;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.route.MassTransitRouteLine;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.xmx.androidmapbase.R;
import com.xmx.androidmapbase.Tools.Map.BMap.Utils.BMapUtil;

import java.util.List;

public class BusResultListAdapter extends BaseAdapter {
    private Context mContext;
    private List<MassTransitRouteLine> mBusPathList;
    private MassTransitRouteResult mBusRouteResult;

    public BusResultListAdapter(Context context, MassTransitRouteResult busrouteresult) {
        mContext = context;
        mBusRouteResult = busrouteresult;
        mBusPathList = busrouteresult.getRouteLines();
    }

    @Override
    public int getCount() {
        return mBusPathList.size();
    }

    @Override
    public Object getItem(int position) {
        return mBusPathList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_bus_result, null);
            holder.title = (TextView) convertView.findViewById(R.id.bus_path_title);
            holder.des = (TextView) convertView.findViewById(R.id.bus_path_des);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final MassTransitRouteLine item = mBusPathList.get(position);
        holder.title.setText(BMapUtil.getBusPathTitle(item));
        holder.des.setText(BMapUtil.getBusPathDes(item));

        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//				Intent intent = new Intent(mContext.getApplicationContext(),
//						BusRouteDetailActivity.class);
//				intent.putExtra("bus_path", item);
//				intent.putExtra("bus_result", mBusRouteResult);
//				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        TextView title;
        TextView des;
    }

}
