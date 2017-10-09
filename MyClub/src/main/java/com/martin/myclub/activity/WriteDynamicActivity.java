package com.martin.myclub.activity;


import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.martin.myclub.R;
import com.martin.myclub.bean.DynamicMsg;
import com.martin.myclub.bean.MyUser;
import com.martin.myclub.util.PhotoUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by Martin on 2017/7/18.
 * Write by Edward on 2017/7/20
 * 写动态
 */
public class WriteDynamicActivity extends AppCompatActivity implements View.OnClickListener{


    private ImageView iv_return;  //  返回
    private ImageView iv_send;   //发送动态
    private MyUser currentUser;//  当前登录的用户
    private EditText et_content;
    private EditText et_title;
    private ImageView iv_camera;
    private ImageView iv_pic;
    private Button bt_submit;
    private Button btn_remove;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_dynamic);

        currentUser = BmobUser.getCurrentUser(MyUser.class);
        initViews();
        initListener();
    }

    private void initListener() {
        iv_return.setOnClickListener(this);
        iv_send.setOnClickListener(this);
    }

    private void initViews() {
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("发动态");

        iv_return = (ImageView) findViewById(R.id.iv_return);
        iv_send = (ImageView) findViewById(R.id.iv_send);
        iv_send.setClickable(false);
        iv_send.setVisibility(View.INVISIBLE);

        et_title = (EditText) findViewById(R.id.et_title);
        et_content = (EditText) findViewById(R.id.et_content);

        iv_camera = (ImageView) findViewById(R.id.iv_camera);
        iv_pic = (ImageView) findViewById(R.id.iv_pic);

        bt_submit = (Button) findViewById(R.id.bt_submit);
        btn_remove = (Button) findViewById(R.id.btn_remove);

        iv_camera.setOnClickListener(this);
        bt_submit.setOnClickListener(this);
        btn_remove.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_return:
                finish();
                break;
            case R.id.bt_submit:
                send();
                break;
            case R.id.iv_camera:
                openAlbum();
                break;
            case R.id.btn_remove:
                removePhoto();
                break;
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
                        setBitmapOpts(iv_pic, photoAlbumFile);
                        btn_remove.setVisibility(View.VISIBLE);  //显示移除图片的按钮
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
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
    }

    /**
     * 如果图片存在  移除图片
     */
    private void removePhoto() {
        if (iv_pic != null) {
            iv_pic.setImageDrawable(null);
            btn_remove.setVisibility(View.GONE);
        }
    }

    /**
     * 上传动态
     */
    DynamicMsg d;
    BmobFile bmobFile;
    private void send() {
        String title = et_title.getText().toString().trim();
        String content = et_title.getText().toString();
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
            showWaitingDialog(true);
            //发布
            d = new DynamicMsg();
            d.setUser(currentUser);
            d.setTitle(title);
            d.setContent(content);
            if (iv_pic.getDrawable() != null) {
                bmobFile = new BmobFile(photoAlbumFile);
                bmobFile.upload(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            saveMsg();   //上传发布的活动
                        } else {
                            showWaitingDialog(false);
                            Toast.makeText(WriteDynamicActivity.this, "上传图片出了点小问题...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                // 不包含图片
                saveMsg();
            }


        } else {
            Toast.makeText(this, "请将信息填写完整", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveMsg() {
        if (d != null) {
            if (bmobFile != null) {
                d.setPicture(bmobFile);
            }
            d.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        showWaitingDialog(false);
                        Toast.makeText(WriteDynamicActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        showWaitingDialog(false);
                        Toast.makeText(WriteDynamicActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * 请稍等dialog
     *
     * @param show
     */
    SweetAlertDialog mDialog;

    private void showWaitingDialog(boolean show) {
        if (mDialog == null) {
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
}
