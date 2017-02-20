package com.example.bgfvg.test;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout containerVG;
    private TanmuBean tanmuBean;
    public String mContent;
    public float mTextSize;
    public int mTextColor;
    public LinearLayout mDanMulayout;
    public TextView mDanmuTextView;
    public ImageView mDanmuImageView;
    private MyHandler handler;
    private String url = "http://php.qyjuju.com/json2/movieCount.php?tb=tt&imei=12345&video_id=12213";
    public String[] mStrings;
    public VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
    }

    private void initData() {
        containerVG = (RelativeLayout) findViewById(R.id.tanmu_container);
        mVideoView = (VideoView) findViewById(R.id.vvv);
        tanmuBean = new TanmuBean();
        tanmuBean.setItems(new String[]{
                "测试一下111aaaaaaaaaaaaaa1", "弹幕这东西真不好做啊11111", "总是出现各种问题11111", "也不知道都是为什么？麻烦！1111", "哪位大神可以帮帮我啊？1111", "I need your help.1111",
                "测试一下2222", "弹幕这东西真不好做啊2222", "总是出现各种问题~~2222", "也不知道都是为什么？麻烦！2222", "哪位大神可以帮帮我啊？2222", "I need your help.2222",
                "测试一下3333", "弹幕这东西真不好做啊3333", "总是出现各种问题~~3333", "也不知道都是为什么？麻烦！3333", "哪位大神可以帮帮我啊？3333", "I need your help.3333"});
        handler = new MyHandler(this);

        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                Log.e("qw", "MainActivity.onError." + e.getMessage());
            }

            @Override
            public void onResponse(String response) {
                Log.e("qw", "MainActivity.onResponse.");
                processData(response);
            }
        });

        String palyUrl = "http://data.vod.itc.cn/?rb=1&prot=1&key=jbZhEJhlqlUN-Wj_HEI8BjaVqKNFvDrn&prod=flash&pt=1&new=/117/156/StuOUSwDQyy6LdVkSMhR9A.mp4";
        mVideoView.setVideoURI(Uri.parse(palyUrl));
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
    }

    private List<String> headList = new ArrayList<>();

    private void processData(String data) {
        DanMuDataBean danMuDataBean = GsonTools.changeGsonToBean(data, DanMuDataBean.class);
        List<DanMuDataBean.ConBean> con = danMuDataBean.getCon();
        mStrings = new String[con.size()];
        for (int i = 0; i < con.size(); i++) {
            mStrings[i] = con.get(i).getContent();
            headList.add(con.get(i).getAvatar());
        }
        tanmuBean.setItems(mStrings);
        Log.e("qw", "MainActivity.processData.strings.length= " + mStrings.length);
        Log.e("qw", "MainActivity.processData.strings.headList= " + headList.size());
    }

    public void click(View view) {
        if (containerVG.getChildCount() > 0) {
            return;
        }
        existMarginValues.clear();
        Toast.makeText(this, "弹出弹幕", Toast.LENGTH_SHORT).show();
        danMu();
    }

    private void danMu() {
        if (mStrings != null && headList != null && mStrings.length > 0 && headList.size() > 0) {
            new Thread() {
                @Override
                public void run() {
                    int N = tanmuBean.getItems().length;
                    for (int i = 0; i < N; i++) {
                        handler.obtainMessage(1, i, 0).sendToTarget();
                        SystemClock.sleep(5000);
                    }
                }
            }.start();
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
                    mContent = ac.tanmuBean.getItems()[index];
                    mTextSize = 23;
                    mTextColor = ac.tanmuBean.getColor();
                    disPlay(mContent, mTextSize, mTextColor, index);
                }
            }
        }
    }

    private Set<Integer> existMarginValues = new HashSet<>();

    private void disPlay(String content, float textSize, int textColor, int index) {
        mDanMulayout = (LinearLayout) LayoutInflater.from(getBaseContext()).inflate(R.layout.danmu, null);
        mDanmuTextView = (TextView) mDanMulayout.findViewById(R.id.hhhh);
        mDanmuImageView = (ImageView) mDanMulayout.findViewById(R.id.mmmm);
        mDanmuTextView.setText(content);
        Picasso.with(getApplicationContext()).load(headList.get(index)).transform(new CircleTransform()).error(R.drawable.ic_default_circle).into(mDanmuImageView);
        mDanmuTextView.setTextColor(textColor);
        int leftMargin = containerVG.getRight() - containerVG.getLeft() - containerVG.getPaddingLeft();
        int verticalMargin = getRandomTopMargin();
        mDanMulayout.setTag(verticalMargin);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.topMargin = verticalMargin;
        mDanMulayout.setLayoutParams(params);
        Animation anim = AnimationHelper.createTranslateAnim(this, leftMargin, -ScreenUtils.getScreenW(this));
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //移除该组件
                containerVG.removeView(mDanMulayout);
                //移除占位
                int verticalMargin = (int) mDanMulayout.getTag();
                existMarginValues.remove(verticalMargin);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mDanMulayout.startAnimation(anim);
        containerVG.addView(mDanMulayout);

    }

    //父组件的高度
    private int validHeightSpace;
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
