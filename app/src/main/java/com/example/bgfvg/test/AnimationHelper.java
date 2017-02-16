package com.example.bgfvg.test;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

/**
 * 动画工具类
 * Created by hanj on 15-6-4.
 */
public class AnimationHelper {
    /**
     * 创建水平方向的水平移动画
     */
    public static Animation createHorizontalTranslateAnim(Context context, int fromX, int toX) {
        TranslateAnimation tlAnim = new TranslateAnimation(fromX, toX, 0, 0);
        //自动计算时间
        long duration = (long) (Math.abs(toX - fromX) * 1.0f / ScreenUtils.getScreenW(context) * 10000);
        tlAnim.setDuration(duration);
        tlAnim.setInterpolator(new LinearInterpolator());
        tlAnim.setFillAfter(true);
        return tlAnim;
    }


    /**
     * 创建垂直方向的平移动画
     */
    public static Animation  createVerticalTranslateAnim(Context context, int fromY, int toY) {
        TranslateAnimation tlAnim = new TranslateAnimation(0, 0, fromY, toY);
        //自动计算时间
        long duration = (long) (Math.abs(toY - fromY) * 1.0f / ScreenUtils.getScreenH(context) * 15000);
        tlAnim.setDuration(duration);
        tlAnim.setInterpolator(new LinearInterpolator());
        tlAnim.setFillAfter(true);
        return tlAnim;
    }

    /**
     * 创建平移动画
     */
    public static Animation createTranslateAnim(Context context, int fromX, int toX) {
        TranslateAnimation tlAnim = new TranslateAnimation(fromX, toX, 0, 0);
        //自动计算时间
        long duration = (long) (Math.abs(toX - fromX) * 1.0f / ScreenUtils.getScreenW(context) * 12000);
        tlAnim.setDuration(duration);
        tlAnim.setInterpolator(new LinearInterpolator());
        tlAnim.setFillAfter(true);

        return tlAnim;
    }
}
