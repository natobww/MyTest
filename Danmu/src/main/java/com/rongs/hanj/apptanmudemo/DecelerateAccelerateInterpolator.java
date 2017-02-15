package com.rongs.hanj.apptanmudemo;

import android.view.animation.Interpolator;

/**
 * 先减速，后加速的Interpolator.
 * Created by hanj on 15-6-4.
 */
public class DecelerateAccelerateInterpolator implements Interpolator {

    //input从0～1，返回值也从0～1.返回值的曲线表征速度加减趋势
    @Override
    public float getInterpolation(float input) {
        return (float) (Math.tan((input * 2 - 1) / 4 * Math.PI)) / 2.0f + 0.5f;
    }
}
