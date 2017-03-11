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
import android.widget.ImageView;

import com.amap.api.services.core.LatLonPoint;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.xmx.androidmapbase.R;
import com.xmx.androidmapbase.common.data.callback.InsertCallback;
import com.xmx.androidmapbase.common.map.amap.poi.CollectionManager;
import com.xmx.androidmapbase.common.map.amap.poi.POI;
import com.xmx.androidmapbase.common.map.amap.utils.ToastUtil;
import com.xmx.androidmapbase.utils.ExceptionUtil;

import java.util.List;
import java.util.UUID;

/**
 * Created by The_onE on 2017/2/28.
 * 收藏POI对话框
 *
 * @property[mContext] 当前上下文
 * @property[position] 收藏点的位置
 * @property[title] 标题框默认显示的标题
 * @property[onSuccess] 收藏成功后的操作
 */
public class ACollectDialog extends DialogFragment {
    Context mContext;
    LatLonPoint mPosition;
    String mTitle;
    ACollectCallback mCallback;
    String mType;
    // 是否为修改对话框
    boolean mModifyFlag = false;
    // 要修改的收藏
    POI mCollection;

    public void initCreateDialog(Context context, LatLonPoint position, String title, ACollectCallback callback) {
        mContext = context;
        mPosition = position;
        mTitle = title;
        mCallback = callback;
    }

    /**
     * 修改收藏对话框
     *
     * @param[context] 当前上下文
     * @param[collection] 要修改的收藏
     * @param[onSuccess] 修改成功的操作
     */
    public void initModifyDialog(Context context, POI collection, ACollectCallback callback) {
        mContext = context;
        mPosition = collection.getLatLonPoint();
        mTitle = collection.getTitle();
        mCallback = callback;
        mModifyFlag = true;
        mCollection = collection;
        mType = collection.mType;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_collect, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 不显示默认标题栏
        getDialog().requestWindowFeature(STYLE_NO_TITLE);

        final EditText editTitle = (EditText) view.findViewById(R.id.editTitle);
        final EditText editContent = (EditText) view.findViewById(R.id.editContent);
        final ImageView imgType = (ImageView) view.findViewById(R.id.imgType);

        // 填充标题框
        if (mTitle != null) {
            editTitle.setText(mTitle);
        }
        // 填充描述框
        if (mCollection != null) {
            editContent.setText(mCollection.getSnippet());
        }
        // 设置类型相关事件
        if (AMapConstantsManager.getInstance().getTypeList().size() > 0) {
            final List<String> list = AMapConstantsManager.getInstance().getTypeList();
            if (mType == null || mType.length() <= 0) {
                // 默认显示第一种类型
                mType = list.get(0);
            }
            imgType.setImageResource(AMapConstantsManager.getInstance().getIconId(mType));
            // 点击类型图标
            imgType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 弹出选择类型对话框
                    new AlertDialog.Builder(mContext)
                            .setTitle("类型")
                            // 将可选类型列出
                            .setItems(list.toArray(new String[list.size()]),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            // 获取选择的类型
                                            mType = list.get(i);
                                            // 更改选择的图标
                                            int iconId = AMapConstantsManager.getInstance().getIconId(mType);
                                            imgType.setImageResource(iconId);
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
                if (mType == null || mType.length() <= 0) {
                    // 若未设置类型
                    if (AMapConstantsManager.getInstance().getTypeList().size() > 0) {
                        final List<String> list = AMapConstantsManager.getInstance().getTypeList();
                        // 设置为默认类型
                        mType = list.get(0);
                    } else {
                        return;
                    }
                }
                if (!mModifyFlag) {
                    // 添加新收藏
                    // 生成收藏
                    final POI col = new POI(UUID.randomUUID().toString(),
                            mPosition,
                            editTitle.getText().toString(),
                            editContent.getText().toString(),
                            mType);
                    // 添加收藏
                    CollectionManager.getInstance().insertToCloud(col, new InsertCallback() {
                        @Override
                        public void success(AVObject user, String objectId) {
                            ToastUtil.show(mContext, "收藏成功");
                            col.mCloudId = objectId;
                            mCallback.onSuccess(col);
                            dismiss();
                        }

                        @Override
                        public void syncError(int error) {
                            CollectionManager.defaultError(error, mContext);
                        }

                        @Override
                        public void syncError(AVException e) {
                            ToastUtil.show(mContext, "收藏失败");
                            ExceptionUtil.filterException(e);
                        }
                    });
                } else {
                    // 修改收藏
                    if (mCollection != null) {
                        mCollection.mTitle = editTitle.getText().toString();
                        mCollection.mContent = editContent.getText().toString();
                        mCollection.mType = mType;
                        // 插入带有Cloud Id的实体会覆盖之前的实体
                        CollectionManager.getInstance().insertToCloud(mCollection, new InsertCallback() {
                            @Override
                            public void success(AVObject user, String objectId) {
                                ToastUtil.show(mContext, "修改成功");
                                mCallback.onSuccess(mCollection);
                                dismiss();
                            }

                            @Override
                            public void syncError(int error) {
                                CollectionManager.defaultError(error, mContext);
                            }

                            @Override
                            public void syncError(AVException e) {
                                ToastUtil.show(mContext, "修改失败");
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
