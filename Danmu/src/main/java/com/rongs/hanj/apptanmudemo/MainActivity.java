package com.rongs.hanj.apptanmudemo;

import android.animation.Animator;
import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 简易弹幕效果实现
 * Created by hanj on 15-6-4.
 */
public class MainActivity extends ActionBarActivity {
    private MyHandler handler;

    //弹幕内容
    private TanmuBean tanmuBean;
    //放置弹幕内容的父组件
    private RelativeLayout containerVG;

    //父组件的高度
    private int validHeightSpace;
    private MyVideoView mVideoView;
    private Thread mDanMuThread;
    private TextView mTextView;
    private Animation mAnim;
    private ObjectAnimator mAnimator;
    private AlertDialog mAlertDialog;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAlertDialog!=null){
            mAlertDialog.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        containerVG = (RelativeLayout) findViewById(R.id.tanmu_container);
        mVideoView = (MyVideoView) findViewById(R.id.mm);
        tanmuBean = new TanmuBean();
        tanmuBean.setColor(getResources().getColor(R.color.colorAccent));
        tanmuBean.setItems(new String[]{
                "测试一下1", "弹幕这东西真不好做啊1", "总是出现各种问题~~1", "也不知道都是为什么？麻烦！1", "哪位大神可以帮帮我啊？1",
                "I need your help.1",
                "测试一下2", "弹幕这东西真不好做啊2", "总是出现各种问题~~2", "也不知道都是为什么？麻烦！2", "哪位大神可以帮帮我啊？2",
                "I need your help.2",
                "测试一下3", "弹幕这东西真不好做啊3", "总是出现各种问题~~3", "也不知道都是为什么？麻烦！3", "哪位大神可以帮帮我啊？3",
                "I need your help.3",
                "测试一下4", "弹幕这东西真不好做啊4", "总是出现各种问题~~4", "也不知道都是为什么？麻烦！4", "哪位大神可以帮帮我啊？4",
                "I need your help.4",
                "测试一下5", "弹幕这东西真不好做啊5", "总是出现各种问题~~5", "也不知道都是为什么？麻烦！5", "哪位大神可以帮帮我啊？5",
                "I need your help.5",
                "测试一下6", "弹幕这东西真不好做啊6", "总是出现各种问题~~6", "也不知道都是为什么？麻烦！6", "哪位大神可以帮帮我啊？6",
                "I need your help.6",
                "测试一下7", "弹幕这东西真不好做啊7", "总是出现各种问题~~7", "也不知道都是为什么？麻烦！7", "哪位大神可以帮帮我啊？7",
                "I need your help.7",
                "测试一下8", "弹幕这东西真不好做啊8", "总是出现各种问题~~8", "也不知道都是为什么？麻烦！8", "哪位大神可以帮帮我啊？8",
                "I need your help.8",
                "测试一下9", "弹幕这东西真不好做啊9", "总是出现各种问题~~9", "也不知道都是为什么？麻烦！9", "哪位大神可以帮帮我啊？9",
                "I need your help.9"});

        handler = new MyHandler(this);


        String url = "http://data.vod.itc.cn/?rb=1&prot=1&key=jbZhEJhlqlUN-Wj_HEI8BjaVqKNFvDrn&prod=flash&pt=1&new=/183/198/VcHpbubuTnGD3hoUEqoweC.mp4";
        String url2 = "http://www.hnxinlun.com/zr_1.mp4";
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mVideoView) {
                mVideoView.start();
                isStop = false;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startDanMu();
                    }
                }, 2000);
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Toast.makeText(MainActivity.this, "播放结束了", Toast.LENGTH_SHORT).show();
                isStop = true;
                if (animators != null && animators.size() > 0) {
                    for (ObjectAnimator animator : animators) {
                        if (animator != null) {
                            animator.cancel();
                        }
                    }
                }
                showVIP();
            }

        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Toast.makeText(MainActivity.this, "播放错误", Toast.LENGTH_SHORT).show();
                finish();
                return true;
            }
        });

        mVideoView.setVideoURI(Uri.parse(url));
    }

    private void showVIP() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
        builder.setTitle("提示"); //设置标题 
        builder.setMessage("是否确认退出?"); //设置内容
        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //关闭dialog
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //参数都设置完成了，创建并显示出来
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    private void stopAnimation() {
        if (mTextView != null)
            mTextView.clearAnimation();
    }

    private void startDanMu() {
        if (containerVG.getChildCount() > 0) {
            return;
        }
        existMarginValues.clear();
        mDanMuThread = new Thread(new CreateTanmuThread());
        mDanMuThread.start();
    }

    private static boolean isStop = false;

    //每2s自动添加一条弹幕
    private class CreateTanmuThread implements Runnable {
        @Override
        public void run() {
            int N = tanmuBean.getItems().length;
            for (int i = 0; i < N; i++) {
                handler.obtainMessage(1, i, 0).sendToTarget();
                SystemClock.sleep(800);
            }
        }
    }

    //需要在主线城中添加组件
    private static class MyHandler extends Handler {
        private WeakReference<MainActivity> ref;

        MyHandler(MainActivity ac) {
            ref = new WeakReference<>(ac);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {
                MainActivity ac = ref.get();
                if (ac != null && ac.tanmuBean != null) {
                    int index = msg.arg1;
                    String content = ac.tanmuBean.getItems()[index];
                    float textSize = (float) (ac.tanmuBean.getMinTextSize() * (1 + Math.random() * ac.tanmuBean.getRange()));
                    int textColor = ac.tanmuBean.getColor();
                    //ac.showDanMuHorizontal(content, textSize, textColor);
                    Log.e("qw", "MyHandler.handleMessage.isStop= "+isStop);
                    if (!isStop)
                        ac.showDanMuHorizontal(content, textSize, textColor);
                }
            }
        }
    }

    private void showDanMuVertical(String content, float textsize, int textcolor) {
        mTextView = new TextView(this);
        mTextView.setTextSize(textsize);
        mTextView.setText(content);
        mTextView.setTextColor(textcolor);
        mTextView.setSingleLine();
        int topMargin = containerVG.getBottom() - containerVG.getTop() - containerVG.getPaddingTop();
        int leftMargin = getRandomTLeftMargin();
        mTextView.setTag(leftMargin);

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.leftMargin = leftMargin;
        mTextView.setLayoutParams(params);
        mTextView.setGravity(Gravity.LEFT);

       /* mAnim = AnimationHelper.createVerticalTranslateAnim(this, topMargin, -ScreenUtils.getScreenH(this));
        mAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //移除该组件
                containerVG.removeView(mTextView);
                //移除占位
                int verticalMargin = (int) mTextView.getTag();
                existMarginValues.remove(verticalMargin);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });
        mTextView.startAnimation(mAnim);*/
        //使用属性动画移动弹幕
        showByAnimator(mTextView, topMargin);
        containerVG.addView(mTextView);
    }

    private List<ObjectAnimator> animators = new ArrayList<>();

    private void showByAnimator(View target, int topMargin) {
        float fromY = 0;
        float toY = 0;
        int bottom = target.getBottom();
        int top = target.getTop();
        fromY = bottom - top - target.getPaddingTop();
        int screenH = ScreenUtils.getScreenH(this);
        toY = -screenH;
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "translationY", topMargin, toY);
        //自动计算时间
        long duration = (long) (Math.abs(toY - fromY) * 1.0f / ScreenUtils.getScreenH(this) * 10000);
        animator.setDuration(duration);

        /*mAnimator.setEvaluator(new TypeEvaluator() {
            @Override
            public Object evaluate(float v, Object o, Object t1) {
                Log.e("qw", "MainActivity.evaluate.");
                return null;
            }
        });*/
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //Log.e("qw", "MainActivity.onAnimationUpdate.= " + animation.getAnimatedValue());
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.start();
        animators.add(animator);
    }

    //记录当前仍在显示状态的弹幕的位置（避免重复）
    private Set<Integer> existMarginValues = new HashSet<>();
    private int linesCount;
    private int validWidthSpace;

    private int getRandomTLeftMargin() {
        int marginValue = 0;
        //计算可用于弹幕显示的控件宽度
        if (validWidthSpace == 0) {
            validWidthSpace = containerVG.getRight() - containerVG.getLeft() - containerVG.getPaddingLeft() - containerVG.getPaddingRight();
        }

        //检查重叠
        while (true) {
            marginValue = (int) (Math.random() * validWidthSpace / 5);
            if (!existMarginValues.contains(marginValue)) {
                existMarginValues.add(marginValue);
                return marginValue;
            }
        }
    }

    private int getRandomTopMargin() {
        //计算用于弹幕显示的空间高度
        if (validHeightSpace == 0) {
            validHeightSpace = containerVG.getBottom() - containerVG.getTop()
                    - containerVG.getPaddingTop() - containerVG.getPaddingBottom();
        }

        //计算可用的行数
        if (linesCount == 0) {
            linesCount = validHeightSpace / ScreenUtils.dp2px(this, tanmuBean.getMinTextSize() * (1 + tanmuBean.getRange()));
            if (linesCount == 0) {
                throw new RuntimeException("Not enough space to show text.");
            }
        }

        //检查重叠
        while (true) {
            int randomIndex = (int) (Math.random() * linesCount);
            int marginValue = randomIndex * (validHeightSpace / linesCount);

            if (!existMarginValues.contains(marginValue)) {
                existMarginValues.add(marginValue);
                return marginValue;
            }
        }
    }

    private void showDanMuHorizontal(String content, float textSize, int textColor) {
        final TextView textView = new TextView(this);
        textView.setTextSize(textSize);
        textView.setText(content);
        textView.setTextColor(textColor);

        int leftMargin = containerVG.getRight() - containerVG.getLeft() - containerVG.getPaddingLeft();

        //计算本条弹幕的leftMargin(随机值，但是与屏幕中已有的不重复)
        int verticalMargin = getRandomTopMargin();
        textView.setTag(verticalMargin);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.topMargin = verticalMargin;
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        Animation anim = AnimationHelper.createHorizontalTranslateAnim(this, leftMargin, -ScreenUtils.getScreenW(this));
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //移除该组件
                containerVG.removeView(textView);
                //移除占位
                int verticalMargin = (int) textView.getTag();
                existMarginValues.remove(verticalMargin);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        textView.startAnimation(anim);
        containerVG.addView(textView);
    }

}
