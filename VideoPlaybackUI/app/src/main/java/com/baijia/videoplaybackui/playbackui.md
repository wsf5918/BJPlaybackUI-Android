#百家云Android回放UISDK集成文档
##简介
带UI的回放SDK基于回放核心SDK，提供标准的UI实现，方便用户快速集成投入使用。此SDK代码开源，开发者可以自己建立分支进行开发，也欢迎给我们提issue  
源码地址：[https://github.com/baijia/BJPlaybackUI-Android](https://github.com/baijia/BJPlaybackUI-Android)(dev分支)  
##项目截图  
 <img src="https://raw.githubusercontent.com/baijia/BJPlaybackUI-Android/dev/VideoPlaybackUI/screenshots/shot_02.jpg" width = "270" height = "480" alt="竖屏" align=bottom />
 <img src="https://raw.githubusercontent.com/baijia/BJPlaybackUI-Android/dev/VideoPlaybackUI/screenshots/shot_01.jpg" width = "480" height = "270" alt="竖屏" align=bottom />  
 apk文件位于apk_bin文件夹下  
##集成SDK  
由于SDK开源，不再支持aar依赖方式，可以直接使用源码依赖  
###集成前的准备  
1) 推荐使用最新版Android studio集成SDK [点击下载](https://developer.android.com/studio/index.html)（需科学上网）  
2）clone源码  
确保电脑配置git命令行工具，执行如下指令，将源码clone到您的文件目录中  
```shell
git clone -b dev git@github.com:baijia/BJPlaybackUI-Android.git
```
3)配置项目settings.gradle文件  
```groovy
include ':playbackui'  
...其他module

project(':playbackui').projectDir = new File('xxx(您的源码路径)/BJPlaybackUI-Android/VideoPlaybackUI/playbackui')
```  
4）添加依赖    
在需要的module的build.gradle文件中添加源码依赖：  
```groovy
compile project(path: ':playbackui')
```  
###快速集成  
在合适的时机，比如按钮点击事件，进入回放房间，会跳转新的Activity界面（PBRoomUI类）：  
```java
/**
 * 进入回放标准UI界面
 *
 * @param context                     Activity
 * @param roomId                      房间id
 * @param roomToken                   token
 * @param onEnterPBRoomFailedListener 进房间错误监听，可为null
 */
public static void enterPBRoom(Context context, String roomId, String roomToken, LPConstants.LPDeployType deployType, OnEnterPBRoomFailedListener onEnterPBRoomFailedListener)
```  
***由于带UI的回放SDK基于回放核心SDK，所有核心SDK特性此SDK都有。具体可参考回放核心SDK***
 