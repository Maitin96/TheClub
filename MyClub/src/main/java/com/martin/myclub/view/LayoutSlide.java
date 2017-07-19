package com.martin.myclub.view;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.martin.myclub.R;


public class LayoutSlide extends FrameLayout{

    private Context context;

    private boolean nightModel = true;

    public LayoutSlide(@NonNull Context context) {
        super(context);
        this.context = context;
        initViews();
    }

    public LayoutSlide(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initViews();
    }

    public LayoutSlide(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initViews();
    }

    private void initViews() {
        this.addView(LayoutInflater.from(context).inflate(R.layout.layout_slide,null));

    }
}
