package com.rongs.hanj.apptanmudemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;


/**
 * 简易弹幕效果实现
 * Created by admin on 15-6-4.
 */
public class MainActivity extends ActionBarActivity {
    private MyHandler handler;

    //弹幕内容  
    private TanmuBean tanmuBean;
    //放置弹幕内容的父组件  
    private RelativeLayout containerVG;

    //父组件的高度  
    private int validHeightSpace;
    public LinearLayout mHah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        containerVG = (RelativeLayout) findViewById(R.id.tanmu_container);
        tanmuBean = new TanmuBean();
        tanmuBean.setItems(new String[]{"测试一下", "弹幕这东西真不好做啊", "总是出现各种问题~~", "也不知道都是为什么？麻烦！", "哪位大神可以帮帮我啊？", "I need your help.",
                "测试一下", "弹幕这东西真不好做啊", "总是出现各种问题~~", "也不知道都是为什么？麻烦！", "哪位大神可以帮帮我啊？", "I need your help.",
                "测试一下", "弹幕这东西真不好做啊", "总是出现各种问题~~", "也不知道都是为什么？麻烦！",
                "哪位大神可以帮帮我啊？", "I need your help."});

        handler = new MyHandler(this);
    }

    public void click(View view) {
        if (containerVG.getChildCount() > 0) {
            return;
        }
        existMarginValues.clear();
        new Thread() {
            @Override
            public void run() {
                int N = tanmuBean.getItems().length;
                for (int i = 0; i < N; i++) {
                    handler.obtainMessage(1, i, 0).sendToTarget();
                    SystemClock.sleep(3000);
                }
            }
        }.start();
    }

    //每2s自动添加一条弹幕  
    public class CreateTanmuThread implements Runnable {
        @Override
        public void run() {

        }
    }

    //需要在主线城中添加组件  
    private class MyHandler extends Handler {
        private WeakReference<MainActivity> ref;

        MyHandler(MainActivity ac) {
            ref = new WeakReference<>(ac);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {
                final MainActivity ac = ref.get();
                if (ac != null && ac.tanmuBean != null) {
                    int index = msg.arg1;
                    final String content = ac.tanmuBean.getItems()[index];
                    final float textSize = (float) (ac.tanmuBean.getMinTextSize() * (1 + Math.random() * ac.tanmuBean.getRange()));
                    final int textColor = ac.tanmuBean.getColor();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ac.showTanmu(content, textSize, textColor);
                        }
                    });
                }
            }
        }
    }

    private void showTanmu(String content, float textSize, int textColor) {
        mHah = (LinearLayout) LayoutInflater.from(getBaseContext()).inflate(R.layout.hahha, null);
        TextView hhh = (TextView) mHah.findViewById(R.id.hhhh);
        hhh.setText(content);
        int leftMargin = containerVG.getRight() - containerVG.getLeft() - containerVG.getPaddingLeft();
        //计算本条弹幕的topMargin(随机值，但是与屏幕中已有的不重复)  
        int verticalMargin = getRandomTopMargin();
        mHah.setTag(verticalMargin);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.topMargin = verticalMargin;
        mHah.setLayoutParams(params);
        Animation anim = AnimationHelper.createTranslateAnim(this, leftMargin, -ScreenUtils.getScreenW(this));
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //移除该组件  
                containerVG.removeView(mHah);
                //移除占位  
                int verticalMargin = (int) mHah.getTag();
                existMarginValues.remove(verticalMargin);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mHah.startAnimation(anim);
        containerVG.addView(mHah);
    }

    //记录当前仍在显示状态的弹幕的位置（避免重复）  
    private Set<Integer> existMarginValues = new HashSet<>();
    private int linesCount;

    private int getRandomTopMargin() {
        //计算用于弹幕显示的空间高度  
        if (validHeightSpace == 0) {
            validHeightSpace = containerVG.getBottom() - containerVG.getTop()
                    - containerVG.getPaddingTop() - containerVG.getPaddingBottom();
        }
        if (linesCount == 0) {
            linesCount = validHeightSpace / ScreenUtils.dp2px(this, tanmuBean.getMinTextSize());
            if (linesCount == 0) {
                throw new RuntimeException("Not enough space to show text.");
            }
        }
        //检查重叠  
        while (true) {
            int randomIndex = (int) (Math.random() * linesCount);
            int marginValue = randomIndex * (validHeightSpace / (linesCount + 2));
            if (!existMarginValues.contains(marginValue)) {
                existMarginValues.add(marginValue);
                return marginValue;
            }
        }
    }
}  