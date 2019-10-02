package com.zonar.zonarapp.ui.layout;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.james.views.FreeLayout;
import com.james.views.FreeTextView;
import com.zonar.zonarapp.R;
import com.zonar.zonarapp.ui.view.CircleWaveView;
import com.zonar.zonarapp.ui.view.TabTextView;

public class ZaMajorLayout extends FreeLayout {

    public TabTextView intelligenceTab;
    public TabTextView hifiTab;
    public CircleWaveView circleWaveView;
    public ImageView rimImage;
    public FreeTextView bassText;
    public FreeTextView richText;
    public FreeTextView vocalText;
    public FreeTextView detailedText;
    public FreeTextView spaciousText;
    public ListView modeList;

    public ZaMajorLayout(Context context) {
        super(context);
        this.setBackgroundColor(0xff050505);

        //
        FreeLayout bottomLayout = (FreeLayout) this.addFreeView(new FreeLayout(context),
                LayoutParams.MATCH_PARENT, 116,
                new int[]{ALIGN_PARENT_BOTTOM});
        bottomLayout.setBackgroundColor(0xffc48c5c);

        intelligenceTab = (TabTextView) bottomLayout.addFreeView(new TabTextView(context),
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        intelligenceTab.tabIcon.setImageResource(R.drawable.ic_ai);
        intelligenceTab.tabText.setText("智慧模式");

        hifiTab = (TabTextView) bottomLayout.addFreeView(new TabTextView(context),
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                intelligenceTab,
                new int[]{END_OF});
        hifiTab.tabIcon.setImageResource(R.drawable.ic_hifi);
        hifiTab.tabText.setText("Hi-Fi模式");

        View divider = bottomLayout.addFreeView(new View(context),
                1, 72,
                new int[]{CENTER_VERTICAL},
                intelligenceTab,
                new int[]{END_OF});
        divider.setBackgroundColor(0xff000000);

        //
        FreeLayout baseLayout = (FreeLayout) this.addFreeView(new FreeLayout(context),
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
                bottomLayout,
                new int[]{ABOVE});

//        FreeLayout scrollLayout = baseLayout.addFreeScrollView(new FreeLayout(context),
//                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        FreeLayout adjustLayout = (FreeLayout) baseLayout.addFreeView(new FreeLayout(context),
                664, 664,
                new int[]{CENTER_HORIZONTAL});
        setMargin(adjustLayout, 0, 34, 0, 0);

        circleWaveView = (CircleWaveView) adjustLayout.addFreeView(new CircleWaveView(context),
                560, 560,
                new int[]{CENTER_IN_PARENT});

        rimImage = (ImageView) adjustLayout.addFreeView(new ImageView(context),
                664, 664,
                new int[]{CENTER_HORIZONTAL});
        rimImage.setImageResource(R.drawable.rim_gradient);
        rimImage.setRotation(0);

//        ImageView rimTextImage = (ImageView) adjustLayout.addFreeView(new ImageView(context),
//                664, 664,
//                new int[]{CENTER_HORIZONTAL});
//        rimTextImage.setImageResource(R.drawable.rim_text);

        FreeLayout textLayout = (FreeLayout) baseLayout.addFreeView(new FreeLayout(context),
                750, 664,
                new int[]{CENTER_HORIZONTAL});
        setMargin(textLayout, 0, 34, 0, 0);

        bassText = (FreeTextView) textLayout.addFreeView(new FreeTextView(context),
                153, 60,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        bassText.setTextColor(0xff7d7d7d);
        bassText.setTextSizeFitResolution(50);
        bassText.setText("Bass");
        bassText.setRotation(-36);

        richText = (FreeTextView) textLayout.addFreeView(new FreeTextView(context),
                520, 60,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        richText.setTextColor(0xff7d7d7d);
        richText.setTextSizeFitResolution(50);
        richText.setText("Rich");
        richText.setRotation(36);

        vocalText = (FreeTextView) textLayout.addFreeView(new FreeTextView(context),
                623, 390,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        vocalText.setTextColor(0xff000000);
        vocalText.setTextSizeFitResolution(50);
        vocalText.setText("Vocal");
        vocalText.setRotation(108);

        detailedText = (FreeTextView) textLayout.addFreeView(new FreeTextView(context),
                308, 610,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        detailedText.setTextColor(0xff7d7d7d);
        detailedText.setTextSizeFitResolution(50);
        detailedText.setText("Detailed");
        detailedText.setRotation(0);

        spaciousText = (FreeTextView) textLayout.addFreeView(new FreeTextView(context),
                10, 400,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        spaciousText.setTextColor(0xff7d7d7d);
        spaciousText.setTextSizeFitResolution(50);
        spaciousText.setText("Spacious");
        spaciousText.setRotation(252);

        //
        modeList = (ListView) baseLayout.addFreeView(new ListView(context),
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        modeList.setCacheColorHint(Color.TRANSPARENT);
        modeList.setBackgroundColor(0xaa4a3727);
        modeList.setVisibility(GONE);

        //
        setListener();
    }

    private void setListener() {

        // TODO 圓圈內移動時，會回傳一個角度的callback，藉此控制外圈文字顏色
        circleWaveView.setOnAngleChangedListener(new CircleWaveView.OnAngleChangedListener() {
            @Override
            public void onAngleChanged(float angle) {
//                LogUtils.v("ZaMajorLayout", "[OnAngleChangedListener] angle -> " + angle);

                rimImage.setRotation(angle);

                bassText.setTextColor(angle < 208 || angle > 260 ? 0xff7d7d7d : 0xff000000);
                richText.setTextColor(angle < 278 || angle > 330 ? 0xff7d7d7d : 0xff000000);
                vocalText.setTextColor(angle >= 345 || angle <= 39 ? 0xff000000 : 0xff7d7d7d);
                detailedText.setTextColor(angle < 63 || angle > 111 ? 0xff7d7d7d : 0xff000000);
                spaciousText.setTextColor(angle < 132 || angle > 181 ? 0xff7d7d7d : 0xff000000);

            }
        });
    }
}
