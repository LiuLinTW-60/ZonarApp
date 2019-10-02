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
  * .layout.ZaMajorLayout.java
  > 放置各種 Activity 的 Layout，比較重要的是 ZaMajorLayout
  > 圓圈內移動時，會回傳一個角度的callback，藉此控制外圈文字顏色
  * .slide.SlideMenuItem.java
  > 右邊 Menu 裡的客製化 Item
  * .slide.SlideMenuView.java
  > 右邊 Menu 裡的所有功能，其中每個元件都是一個 SlideMenuItem
  * .view.CircleWaveView.java
  > 繪製波形圖的客製化 View，包含波形、等高線圖、漸層、手勢控制等，詳細內容在 code 裡有另外註解
  * .view.TitleContentText.java
  > 客製化 View，其效果是，右邊是 Title，左邊是內容
  * .view.Toolbar.java
  > 客製化 Toolbar，位置在畫面最上方
  
