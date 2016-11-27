# AndroidMapBase 安卓基础地图框架
### 本程序是基于[高德开放平台](http://lbs.amap.com/)搭建的，致力于快速开发安卓LBS应用的基础框架。
##### 地图功能完全基于高德地图API，开发前需申请开发者帐号，部分功能参考自高德开放平台[官方Demo](http://lbs.amap.com/api/android-sdk/download/)
##### 基础框架基于精简版[安卓基础框架](https://github.com/The---onE/AndroidFrameworkBase)，保留数据存储功能和常用工具类，基于[LeanCloud](https://leancloud.cn/)的云存储及用户管理功能请参考该框架进行操作

## 开发准备
#### 复制文件
- 在Android Studio中打开本框架，若成功创建名为“基础框架”的应用则已成功配置
- 创建本框架的副本，作为新应用的基础文件、修改根目录文件夹名为自定义名称
- 在AS中打开副本文件夹，即可开始进行项目的开发

#### 修改包名
- 参考[修改包名](http://www.jianshu.com/p/557e1906db1a)中的方法将com.xmx.androidframeworkbase修改为新应用的包名
- 修改包名后即可尝试打包生成新应用，开始开发调试

#### 修改应用名
- 打开res/values/strings.xml文件，其中包含了一下常用的提示语等字符串，修改app_name的值即可修改应用名

#### 修改启动界面
- 修改res/values/splash.png为自定义图片，即可在打开APP时看到启动启动界面

#### 云端初始化
- 在[LeanCloud](https://leancloud.cn/)注册帐号，创建一个新应用
- 在云端控制台设置中，在应用Key页面查看应用信息，将对应信息保存在java/Constants类中APP_ID和APP_KEY常量中

#### 用户表初始化
- 在存储页面分别创建：管理用户帐号密码信息的表、管理用户基本数据的表、管理登录日志的日志表。将表名分别存于java根目录Constants类中：USER_INFO_TABLE、USER_DATA_TABLE、LOGIN_LOG_TABLE常量中。这些的表的权限需要设置为无限制
- 用户表初始化后即可实现登录注册功能
- 可以使用java/User/LoginActivity和RegisterActivity并修改res/drawable/login.png文件即可快速实现登录注册界面
- 根据具体需要可以修改Activity的样式等实现自定义效果，或新建Activity并调用UserManager用户管理器中的方法即可实现登录注册功能
- 注册帐号并登录后即可进入应用主界面

## 自定义页面
- 本框架主体全部运行在MainActivity中，通过ViewPager对自定义页面进行管理
- MainActivity的initView方法fragments和titles对应保存着要显示的Fragment和其标题。
- 框架中已添加的SQLFragment、CloudFragment、SyncFragment是分别用于演示SQLite数据库、LeanCloud数据库、本地云端数据同步的Fragment，不将其添加至列表即可不再显示，在java根目录下的SQL、Cloud、Sync可以查看对应实体管理器的使用方法
- 要添加自定义页面，只需创建好Fragment，之后将其添加到对应的列表即可，添加顺序即为滑动显示顺序
