package com.martin.myclub.activity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.martin.myclub.R;
import com.martin.myclub.activity.fragment.Fragment_club_activity;
import com.martin.myclub.activity.fragment.Fragment_club_announcement;
import com.martin.myclub.activity.fragment.Fragment_club_dynamic;
import com.martin.myclub.activity.fragment.Fragment_club_management;
import com.martin.myclub.activity.fragment.Fragment_club_others;
import com.martin.myclub.adapter.AdapterClub;
import com.martin.myclub.bean.ApplyToAddClub;
import com.martin.myclub.bean.ClubApply;
import com.martin.myclub.bean.MyUser;
import com.martin.myclub.util.PhotoUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 社团页面
 * Created by Edward on 2017/8/24.
 */

public class ClubActivity extends AppCompatActivity {
    private String TAG = "ClubActivity";

    private String clubObjId;
    private MyUser currentUser;
    private LinearLayout loadingLayout;
    private AppBarLayout barLayout;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private ArrayList<TabLayout.Tab> tabList = new ArrayList<>();
    private ArrayList<Fragment> fragmentList = new ArrayList<>();
    private AdapterClub adapter;
    private CoordinatorLayout mainLayout;
    private Toolbar toolBar;
    private CollapsingToolbarLayout collapsing_toolbar;
    private FloatingActionButton floatingActionButton;

    private int type;
    private int admin = 2 << 2;
    private int member = 2 << 3;
    private int visitor = 2 << 4;
    private TextView error;
    private ImageView iv_club_bg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = BmobUser.getCurrentUser(MyUser.class);
        clubObjId = getIntent().getStringExtra("clubObjId");
        Log.e(TAG, "clubObjId: " + clubObjId);
        setContentView(R.layout.activity_club);
        initView();
    }

    private void initView() {

        error = (TextView) findViewById(R.id.error);
        iv_club_bg = (ImageView) findViewById(R.id.iv_club_bg);

        loadingLayout = (LinearLayout) findViewById(R.id.loading);
        barLayout = (AppBarLayout) findViewById(R.id.bar_layout);
        mainLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        collapsing_toolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ClubActivity", "floatingActionButton: 被点击了" + type);
                showPlusDialog(type);
            }
        });

        // 社长有权修改背景
        iv_club_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type == admin){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ClubActivity.this);
                    final AlertDialog dialog = builder.create();
                    View view = View.inflate(ClubActivity.this, R.layout.dialog_change_background, null);
                    TextView tv_change_bg = (TextView) view.findViewById(R.id.change_bg);
                    tv_change_bg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openAlbum();
                            dialog.dismiss();
                        }
                    });
                    TextView cancel = (TextView) view.findViewById(R.id.cancel);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setView(view);
                    dialog.show();
                }
            }
        });

        requestData();
    }

    private void requestData() {
//        showMainInterface(false);

        try{
            BmobQuery<ClubApply> query = new BmobQuery<>();
            query.getObject(clubObjId, new QueryListener<ClubApply>() {
                @Override
                public void done(ClubApply clubApply, BmobException e) {
                    if (e == null) {
                        setUIContent(clubApply);
                        queryIdentity();
                    } else {
                        Log.e("ClubActivity:", "requestData: " + e.toString());
                        showError("页面加载失败");
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 显示关于发布社团动态或
     */
    private void showPlusDialog(int type) {
        if (type == admin) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final AlertDialog dialog = builder.create();
            View view = View.inflate(this, R.layout.dialog_plus_admin, null);

            TextView tv_dynamic = (TextView) view.findViewById(R.id.tv_dynamic);
            TextView tv_activity = (TextView) view.findViewById(R.id.tv_activity);
            TextView tv_announcement = (TextView) view.findViewById(R.id.tv_announcement);
            tv_dynamic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(ClubActivity.this,ClubWriteDynamicActivity.class);
                    intent.putExtra("isAdmin",true);
                    intent.putExtra("clubObjId",clubObjId);
                    startActivity(intent);
                    dialog.dismiss();
                }
            });
            tv_activity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ClubActivity.this, ClubWriteActivityActivity.class);
                    intent.putExtra("clubObjId",clubObjId);
                    startActivity(intent);
                    dialog.dismiss();
                }
            });
            tv_announcement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ClubActivity.this, ClubWriteAnnouncementActivity.class);
                    intent.putExtra("clubObjId",clubObjId);
                    startActivity(intent);
                    dialog.dismiss();
                }
            });

            dialog.setView(view);
            dialog.show();
        }else if(type == member){
            //是成员
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final AlertDialog dialog = builder.create();
            View view = View.inflate(this, R.layout.dialog_plus_member, null);
            TextView tv_dynamic = (TextView) view.findViewById(R.id.tv_dynamic);
            TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
            tv_dynamic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent();
                    intent.setClass(ClubActivity.this,ClubWriteDynamicActivity.class);
                    intent.putExtra("isAdmin",false);
                    intent.putExtra("clubObjId",clubObjId);
                    startActivity(intent);
                }
            });
            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.setView(view);
            dialog.show();

        }else if(type == visitor){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final AlertDialog dialog = builder.create();
            View view = View.inflate(this, R.layout.dialog_plus_vistor, null);
            TextView tv_apply = (TextView) view.findViewById(R.id.tv_apply);
            TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
            tv_apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    showApplyDialog();
                }

                /**
                 * 申请人填写申请缘由
                 */
                EditText et_content;
                private void showApplyDialog() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ClubActivity.this);
                    final AlertDialog dialog = builder.create();
                    View view = View.inflate(ClubActivity.this, R.layout.dialog_apply_reason, null);
                    et_content = (EditText) view.findViewById(R.id.et_content);
                    Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
                    btn_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendApply(dialog);
                        }
                    });
                    dialog.setView(view);
                    dialog.show();
                }

                private void sendApply(final AlertDialog dialog) {
                    String content = et_content.getText().toString();
                    if(!TextUtils.isEmpty(content)){
                        showWaitingDialog(true);
                        ApplyToAddClub apply = new ApplyToAddClub();
                        apply.setUser(currentUser);
                        apply.setContent(content);
                        apply.setClubId(clubObjId);
                        apply.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if(e == null){
                                    showWaitingDialog(false);
                                    showSuccessgDialog(true);
                                    dialog.dismiss();
                                }else{
                                    showWaitingDialog(false);
                                    Toast.makeText(ClubActivity.this,"连接服务器失败，请重试",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.setView(view);
            dialog.show();
        }

    }

    /**
     * 设置界面要显示的内容
     *
     * @param club
     */
    private void setUIContent(ClubApply club) {
        collapsing_toolbar.setTitle(club.getName());
        if(club.getBg() != null){
            Glide.with(getApplicationContext()).load(club.getBg().getUrl()).into(iv_club_bg);
        }
    }

    /**
     * 验证用户身份
     */
    private void queryIdentity() {
        BmobQuery<MyUser> query = new BmobQuery<>();
        ClubApply club = new ClubApply();
        club.setObjectId(clubObjId);
        query.addWhereRelatedTo("admin", new BmobPointer(club));
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if (e == null) {
                    //管理员检测
                    for (MyUser user : list) {
                        if (currentUser.getObjectId().equals(user.getObjectId())) {
                            setViewPager(true);
                            type = admin;
                            showMainInterface(true);
                            return;
                        }
                    }
                    setViewPager(false);
                    // 社团成员检测
                    BmobQuery<MyUser> query = new BmobQuery<>();
                    ClubApply club = new ClubApply();
                    club.setObjectId(clubObjId);
                    query.addWhereRelatedTo("member", new BmobPointer(club));
                    query.findObjects(new FindListener<MyUser>() {
                        @Override
                        public void done(List<MyUser> list, BmobException e) {
                            if(e == null){
                                for(MyUser user : list){
                                    if(currentUser.getObjectId().equals(user.getObjectId())){
                                        type = member;
                                        showMainInterface(true);
                                        return;
                                    }
                                }
                                type = visitor;
                                showMainInterface(true);
                            }
                        }
                    });
                } else {
                    Log.e("ClubActivity:", "queryAdmin: " + e.toString());
                    showRetryDialog();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PhotoUtils.ALBUM && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                ContentResolver cr = this.getContentResolver();
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    saveAlbumAsFile(bitmap);
                    if (photoAlbumFile != null) {
                        Log.e("图片文件存在！！", "onActivityResult: ");
                        setBitmapOpts(iv_club_bg, photoAlbumFile);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 打开相册
     */
    PhotoUtils utils;

    private void openAlbum() {
        utils = new PhotoUtils(this);
        utils.openByType(PhotoUtils.ALBUM);
    }


    File photoAlbumFile;

    private void saveAlbumAsFile(Bitmap bitmap) {
        String path = Environment.getExternalStorageDirectory().getPath();
        File dirFile = new File(path + "/clubWriteDynamic/");
        if (dirFile.exists())
            utils.deleteDirWithFile(dirFile);  //删除掉目录重新创建
        dirFile.mkdirs();

        String photoName = System.currentTimeMillis() + ".jpg";
        photoAlbumFile = new File(dirFile.getPath() + "/" + photoName);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(photoAlbumFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBitmapOpts(ImageView imageView, File photoFile) {
        int width = imageView.getWidth();
        int height = imageView.getHeight();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFile.getPath(), options);
        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;

        int scaleFactor = Math.min(imageWidth / width, imageHeight
                / height);

        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;
        options.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getPath(),
                options);
        imageView.setImageBitmap(bitmap);

        uploadBg();
    }

    /**
     * 上传社团的封面背景
     */
    private void uploadBg() {

        final BmobFile bmobFile = new BmobFile(photoAlbumFile);
        bmobFile.upload(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    ClubApply clubApply = new ClubApply();
                    clubApply.setObjectId(clubObjId);
                    clubApply.setBg(bmobFile);
                    clubApply.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if( e== null){
                                Toast.makeText(ClubActivity.this,"封面上传成功",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        ClubApply clubApply = new ClubApply();
        clubApply.setObjectId(clubObjId);

    }

    /**
     * 是否显示主界面
     *
     * @param show
     */
    private void showMainInterface(boolean show) {
        if (show) {
            Log.e(TAG, "showMainInterface: true" );
            loadingLayout.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
        } else {
            Log.e(TAG, "showMainInterface: false" );
            loadingLayout.setVisibility(View.VISIBLE);
            mainLayout.setVisibility(View.GONE);
        }
    }

    private List<Fragment> setFragment(boolean isAdmin) {

        Fragment_club_dynamic fragment_club_dynamic = new Fragment_club_dynamic();
        Fragment_club_activity fragment_club_activity = new Fragment_club_activity();
        Fragment_club_announcement fragment_club_announcement = new Fragment_club_announcement();
        fragmentList.add(fragment_club_dynamic);
        fragmentList.add(fragment_club_activity);
        fragmentList.add(fragment_club_announcement);
        if (isAdmin) {
            fragmentList.add(new Fragment_club_management());
        }else{
            fragmentList.add(new Fragment_club_others());
        }
        return fragmentList;
    }

    /**
     * 设置ViewPager的显示
     *
     * @param isAdmin
     */
    private void setViewPager(boolean isAdmin) {
        Log.d(TAG, "setViewPager: 管理员验证：" + isAdmin);
        adapter = new AdapterClub(getSupportFragmentManager(), setFragment(isAdmin));
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        tabList.add(tabLayout.getTabAt(0));
        tabList.add(tabLayout.getTabAt(1));
        tabList.add(tabLayout.getTabAt(2));
        tabList.get(0).setText("社团");
        tabList.get(1).setText("活动");
        tabList.get(2).setText("通告");
        if (isAdmin) {
            tabList.add(tabLayout.getTabAt(3));
            tabList.get(3).setText("管理");
        }else{
            tabList.add(tabLayout.getTabAt(3));
            tabList.get(3).setText("其他");
        }
    }

    private void showRetryDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("异常")
                .setContentText("加载出了点小问题,要重试一次吗？")
                .setConfirmText("再试试")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        requestData();
                    }
                })
        .show();
    }

    SweetAlertDialog mDialog;
    private void showWaitingDialog(boolean show) {
        if(mDialog == null){
            mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        }
        if (show) {
            mDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            mDialog.setTitleText("请稍等");
            mDialog.setCancelable(false);
            mDialog.show();
        } else {
            mDialog.dismiss();
        }
    }


    private void showSuccessgDialog(boolean show) {
        SweetAlertDialog mSuccessDialog;
            mSuccessDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        if (show) {
            mSuccessDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            mSuccessDialog.setTitleText("申请成功！");
            mSuccessDialog.setConfirmText("好的");
            mSuccessDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();
                }
            });
            mSuccessDialog.show();
        }else{
            mSuccessDialog.dismiss();
        }
    }

    private void showError(String content) {
        error.setText(content);
        error.setVisibility(View.VISIBLE);
    }

}
