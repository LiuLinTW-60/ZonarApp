# Zonar APP 開發者文件 (ver 0.1.0)

## 以下僅針對比較常改到的頁面與功能提供說明

* com.zonar.zonarapp.model.Consumer.java
> 用於一些 function 的 callback 使用

* com.zonar.zonarapp.service.MediaPlayerReceiver.java
> 用於抓取外部播放器在播放音樂時的資訊

* com.zonar.zonarapp.ui
> app 裡的主要 UI 都放這邊
  * .adapter
  > 放置各種 adapter，目前只有切換模式的 adapter
  * .dialog
  > 放置各種 dialog，目前只有 InputDialog，用於輸入自建模式
  * .layout
  > 放置各種 Activity 的 Layout，比較重要的是 ZaMajorLayout
    * .ZaMajorLayout.java
    > 
