package com.joy.qqmusic.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * ���õ�λת��������
 * Created by sks on 2016/4/9.
 */
public class DensityUtils {

    /**
     * dpתpx
     * @param context
     * @param dpVal
     * @return
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * spתpx
     * @param context
     * @param spVal
     * @return
     */
    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    /**
     * pxתdp
     * @param context
     * @param pxVal
     * @return
     */
    public static float px2dp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    /**
     * pxתsp
     * @param context
     * @param pxVal
     * @return
     */
    public static float px2sp(Context context, float pxVal) {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }

    public static String int2stringFormat(int duration) {
        String min = duration / 60000 < 10 ? 0 + "" + duration / 60000 : duration / 60000 + "";
        String sec = (duration / 1000 % 60 < 10) ? (0 + "" + duration / 1000 % 60) : (duration / 1000 % 60 + "");
        String durstr = min + ":" + sec;
        return durstr;
    }
}
