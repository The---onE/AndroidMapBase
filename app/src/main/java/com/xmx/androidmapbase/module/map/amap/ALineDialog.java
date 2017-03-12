package com.xmx.androidmapbase.module.map.amap;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.xmx.androidmapbase.R;
import com.xmx.androidmapbase.common.data.callback.InsertCallback;
import com.xmx.androidmapbase.common.map.amap.line.Line;
import com.xmx.androidmapbase.common.map.amap.line.LineManager;
import com.xmx.androidmapbase.common.map.amap.utils.ToastUtil;
import com.xmx.androidmapbase.utils.ExceptionUtil;

import java.util.List;

/**
 * Created by The_onE on 2017/2/28.
 * 收藏POI对话框
 *
 * @property[mContext] 当前上下文
 * @property[position] 收藏点的位置
 * @property[title] 标题框默认显示的标题
 * @property[onSuccess] 收藏成功后的操作
 */
public class ALineDialog extends DialogFragment {
    Context mContext;
    String mTitle;
    LatLng mStart;
    LatLng mEnd;
    ALineCallback mCallback;
    String mColor;
    // 是否为修改对话框
    boolean mModifyFlag = false;
    // 要修改的收藏
    Line mLine;

    public void initCreateDialog(Context context, LatLng start, LatLng end, ALineCallback callback) {
        mContext = context;
        mStart = start;
        mEnd = end;
        mCallback = callback;
    }

    /**
     * 修改收藏对话框
     *
     * @param[context] 当前上下文
     * @param[collection] 要修改的收藏
     * @param[onSuccess] 修改成功的操作
     */
    public void initModifyDialog(Context context, Line line, ALineCallback callback) {
        mContext = context;
        mTitle = line.mTitle;
        mStart = line.mStart;
        mEnd = line.mEnd;
        mCallback = callback;
        mModifyFlag = true;
        mLine = line;
        mColor = AMapConstantsManager.getInstance().findColorName(line.mColor);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_line, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 不显示默认标题栏
        getDialog().requestWindowFeature(STYLE_NO_TITLE);

        final EditText editTitle = (EditText) view.findViewById(R.id.editTitle);
        final EditText editWidth = (EditText) view.findViewById(R.id.editWidth);
        final TextView txtColor = (TextView) view.findViewById(R.id.txtColor);

        // 填充标题框
        if (mTitle != null) {
            editTitle.setText(mTitle);
        }
        // 默认宽度为10
        editWidth.setText("10");
        // 如果为修改则显示原宽度
        if (mLine != null) {
            editWidth.setText("" + mLine.mWidth);
        }
        // 设置颜色相关事件
        if (AMapConstantsManager.getInstance().getColorList().size() > 0) {
            final List<String> list = AMapConstantsManager.getInstance().getColorList();
            if (mColor == null) {
                // 默认显示第一种颜色
                mColor = list.get(0);
            }
            txtColor.setText(mColor);
            // 点击选择颜色
            txtColor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 弹出选择颜色对话框
                    new AlertDialog.Builder(mContext).setTitle("颜色")
                            // 将可选颜色列出
                            .setItems(list.toArray(new String[list.size()]), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // 获取选择的颜色
                                    mColor = list.get(i);
                                    // 显示选择的颜色
                                    txtColor.setText(mColor);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dismiss();
                                }
                            })
                            .show();
                }
            });
        }

        // 确认
        view.findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Double.valueOf(editWidth.getText().toString()) == null) {
                    ToastUtil.show(mContext, "请输入宽度");
                    return;
                }
                if (mColor == null) {
                    // 若未设置颜色
                    if (AMapConstantsManager.getInstance().getColorList().size() > 0) {
                        // 设置为默认颜色
                        mColor = AMapConstantsManager.getInstance().getColorList().get(0);
                    } else {
                        return;
                    }
                }
                if (!mModifyFlag) {
                    // 添加新路线
                    // 获取选择的颜色代码
                    int color = AMapConstantsManager.getInstance().getColor(mColor);
                    // 生成路线
                    final Line line = new Line(mStart, mEnd,
                            editTitle.getText().toString(),
                            color,
                            Double.valueOf(editWidth.getText().toString()));
                    // 添加路线
                    LineManager.getInstance().insertToCloud(line, new InsertCallback() {
                        @Override
                        public void success(AVObject user, String objectId) {
                            // 添加成功
                            ToastUtil.show(mContext, "添加成功");
                            line.mCloudId = objectId;
                            mCallback.onSuccess(line);
                            dismiss();
                        }

                        @Override
                        public void syncError(int error) {
                            LineManager.defaultError(error, mContext);
                        }

                        @Override
                        public void syncError(AVException e) {
                            ToastUtil.show(mContext, "添加失败");
                            ExceptionUtil.filterException(e);
                        }
                    });
                } else {
                    // 修改收藏
                    // 获取选择的颜色代码
                    int color = AMapConstantsManager.getInstance().getColor(mColor);
                    if (mLine != null) {
                        mLine.mTitle = editTitle.getText().toString();
                        mLine.mColor = color;
                        mLine.mWidth = Double.valueOf(editWidth.getText().toString());
                        // 插入带有Cloud Id的实体会覆盖之前的实体
                        LineManager.getInstance().insertToCloud(mLine, new InsertCallback() {
                            @Override
                            public void success(AVObject user, String objectId) {
                                ToastUtil.show(mContext, "修改成功");
                                mCallback.onSuccess(mLine);
                                dismiss();
                            }

                            @Override
                            public void syncError(int error) {
                                LineManager.defaultError(error, mContext);
                            }

                            @Override
                            public void syncError(AVException e) {
                                ToastUtil.show(mContext, "添加失败");
                                ExceptionUtil.filterException(e);
                            }
                        });
                    }
                }
            }
        });
        // 取消
        view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
