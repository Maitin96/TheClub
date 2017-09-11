package com.martin.myclub.util;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

/**
 * Powered by Edward on 2017/8/10.
 */

public class UIUtils {
    public static SpannableString getColorStr(String s,String colorCode){
        SpannableString ss = new SpannableString(s);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor(colorCode));
        ss.setSpan(colorSpan,0,ss.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return ss;
    }
}
