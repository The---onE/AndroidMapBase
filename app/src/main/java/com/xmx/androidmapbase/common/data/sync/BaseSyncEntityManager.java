package com.xmx.androidmapbase.common.data.sync;

import android.content.Context;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.xmx.androidmapbase.R;
import com.xmx.androidmapbase.common.data.callback.DelCallback;
import com.xmx.androidmapbase.common.data.callback.InsertCallback;
import com.xmx.androidmapbase.common.data.callback.SelectLoginCallback;
import com.xmx.androidmapbase.common.data.callback.UpdateCallback;
import com.xmx.androidmapbase.common.data.cloud.BaseCloudEntityManager;
import com.xmx.androidmapbase.common.data.DataConstants;
import com.xmx.androidmapbase.common.data.sql.BaseSQLEntityManager;
import com.xmx.androidmapbase.common.user.UserData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by The_onE on 2016/5/29.
 */
public abstract class BaseSyncEntityManager<Entity extends ISyncEntity> {
    String userId = null;

    public class SQLManager extends BaseSQLEntityManager<Entity> {
        void setTableName(String tableName) {
            this.tableName = tableName;
        }

        void setEntityTemplate(Entity entityTemplate) {
            this.entityTemplate = entityTemplate;
        }
    }

    class CloudManager extends BaseCloudEntityManager<Entity> {
        void setTableName(String tableName) {
            this.tableName = tableName;
        }

        void setEntityTemplate(Entity entityTemplate) {
            this.entityTemplate = entityTemplate;
        }

        public void setUserField(String userField) {
            this.userField = userField;
        }
    }

    SQLManager sqlManager = new SQLManager();
    CloudManager cloudManager = new CloudManager();

    //使用数据库管理器查询数据
    public SQLManager getSQLManager() {
        return sqlManager;
    }

    //子类构造函数中调用方法初始化下列变量！
    private String tableName = null;
    private Entity entityTemplate = null; //空模版，不需要数据
    private String userField = null; //用户字段，保存当前登录用户的ObjectId，为空时不保存用户字段

    //设置表名，数据库和云端表名相同
    protected void setTableName(String tableName) {
        sqlManager.setTableName(tableName);
        cloudManager.setTableName(tableName);
        this.tableName = tableName;
    }

    //设置模版，不需要数据
    public void setEntityTemplate(Entity entityTemplate) {
        sqlManager.setEntityTemplate(entityTemplate);
        cloudManager.setEntityTemplate(entityTemplate);
        this.entityTemplate = entityTemplate;
    }

    //设置用户字段，保存当前登录用户的ObjectId，不设置不保存用户字段
    public void setUserField(String userField) {
        cloudManager.setUserField(userField);
        this.userField = userField;
    }

    protected boolean checkDatabase() {
        return tableName != null && entityTemplate != null;
    }

    protected boolean switchAccount(String id) {
        if (!id.equals(userId)) {
            userId = id;
            sqlManager.openDatabase(userId);
            return true;
        }
        return false;
    }

    //将云端数据同步至数据库
    public void syncFromCloud(final Map<String, Object> conditions,
                              final SelectLoginCallback<Entity> callback) {
        if (!checkDatabase()) {
            callback.syncError(DataConstants.NOT_INIT);
            return;
        }
        cloudManager.selectByCondition(conditions, null, false, new SelectLoginCallback<Entity>() {

            @Override
            public void success(UserData user, List<Entity> entities) {
                switchAccount(user.objectId);
                for (Entity entity : entities) {
                    if (sqlManager.selectByCloudId(entity.getCloudId()) == null) {
                        sqlManager.insertData(entity);
                    }
                }
                callback.success(user, entities);
            }

            @Override
            public void syncError(int error) {
                callback.syncError(error);
            }

            @Override
            public void syncError(AVException e) {
                callback.syncError(e);
            }
        });
    }

    //向云端添加数据，成功后添加至数据库
    public void insertData(final Entity entity, final InsertCallback callback) {
        if (!checkDatabase()) {
            callback.syncError(DataConstants.NOT_INIT);
            return;
        }
        cloudManager.insertToCloud(entity, new InsertCallback() {
            @Override
            public void success(UserData user, String objectId) {
                switchAccount(user.objectId);
                entity.setCloudId(objectId);
                sqlManager.insertData(entity);
                callback.success(user, objectId);
            }

            @Override
            public void syncError(int error) {
                callback.syncError(error);
            }

            @Override
            public void syncError(AVException e) {
                callback.syncError(e);
            }
        });
    }

    //从云端删除一条记录，成功后删除数据库中对应记录
    public void deleteData(final String objectId, final DelCallback callback) {
        if (!checkDatabase()) {
            callback.syncError(DataConstants.NOT_INIT);
            return;
        }
        cloudManager.deleteFromCloud(objectId, new DelCallback() {
            @Override
            public void success(UserData user) {
                switchAccount(user.objectId);
                sqlManager.deleteByCloudId(objectId);
                callback.success(user);
            }

            @Override
            public void syncError(int error) {
                callback.syncError(error);
            }

            @Override
            public void syncError(AVException e) {
                callback.syncError(e);
            }
        });
    }

    //在云端更新一条记录，成功后更新数据库中对应记录
    public void updateData(final String objectId, final Map<String, Object> update,
                           final UpdateCallback callback) {
        if (!checkDatabase()) {
            callback.syncError(DataConstants.NOT_INIT);
            return;
        }
        cloudManager.updateToCloud(objectId, update, new UpdateCallback() {
            @Override
            public void success(UserData user) {
                switchAccount(user.objectId);
                List<String> strings = new ArrayList<>();
                for (String key : update.keySet()) {
                    Object value = update.get(key);
                    String string;
                    if (value instanceof Date) {
                        string = key + " = " + ((Date) value).getTime();
                    } else if (value instanceof String) {
                        string = key + " = '" + value + "'";
                    } else {
                        string = key + " = " + value.toString();
                    }
                    strings.add(string);
                }
                sqlManager.updateData(objectId, strings.toArray(new String[strings.size()]));
                callback.success(user);
            }

            @Override
            public void syncError(int error) {
                callback.syncError(error);
            }

            @Override
            public void syncError(AVException e) {
                callback.syncError(e);
            }
        });
    }

    public static void defaultError(int error, Context context) {
        switch (error) {
            case DataConstants.NOT_INIT:
                Toast.makeText(context, R.string.failure, Toast.LENGTH_SHORT).show();
                break;
            case DataConstants.NOT_LOGGED_IN:
                Toast.makeText(context, R.string.not_loggedin, Toast.LENGTH_SHORT).show();
                break;
            case DataConstants.USERNAME_ERROR:
                Toast.makeText(context, R.string.username_error, Toast.LENGTH_SHORT).show();
                break;
            case DataConstants.CHECKSUM_ERROR:
                Toast.makeText(context, R.string.not_loggedin, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
