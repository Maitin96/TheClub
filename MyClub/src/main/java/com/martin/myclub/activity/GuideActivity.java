package com.martin.myclub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.martin.myclub.R;
import com.martin.myclub.adapter.AdapterGuideViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martin on 2017/7/2.
 * 向导页面，第一次进入时显示。
 */
public class GuideActivity extends AppCompatActivity {

    private List<View> viewList;
    private int[] indicatorDotIds = {R.id.iv_dot1,R.id.iv_dot2,R.id.iv_dot3};
    private ImageView[] imageViews = new ImageView[3];
    private AdapterGuideViewPager adapterGuideViewPager;
    private ViewPager viewPager;
    private Button btnToMain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guide);

        //加载View到ViewList集合中
        final LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

        // Todo  outofmemory!
        viewList = new ArrayList<>();
        viewList.add(inflater.inflate(R.layout.guide_page1,null));
        viewList.add(inflater.inflate(R.layout.guide_page2,null));
        viewList.add(inflater.inflate(R.layout.guide_page3,null));

        //将每个页面的ID与dot绑定
        for (int i = 0; i < indicatorDotIds.length; i++){
            imageViews[i] = (ImageView) findViewById(indicatorDotIds[i]);
        }

        adapterGuideViewPager = new AdapterGuideViewPager(this,viewList);

        //获取ViewPager对象，并设置Adapter
        viewPager = (ViewPager) findViewById(R.id.vp_guide);
        viewPager.setAdapter(adapterGuideViewPager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            /**
             * 图片改变时，改变点的状态
             * @param position
             */
            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < indicatorDotIds.length; i++){
                    if (i != position){
                        imageViews[i].setImageResource(R.drawable.unselected);
                    } else {
                        imageViews[i].setImageResource(R.drawable.selected);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        btnToMain = (Button) viewList.get(2).findViewById(R.id.btn_to_main);
        btnToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuideActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
