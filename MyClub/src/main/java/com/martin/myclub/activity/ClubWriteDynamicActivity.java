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
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.martin.myclub.R;
import com.martin.myclub.bean.ClubDynamic;
import com.martin.myclub.bean.MyUser;
import com.martin.myclub.util.Base64Util;
import com.martin.myclub.util.PhotoUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 社团内部发布动态的页面
 * Created by Edward on 2017/9/11.
 */

public class ClubWriteDynamicActivity extends AppCompatActivity implements View.OnClickListener {

    private int PHOTO = 1 << 3;
    private int ALBUM = 1 << 4;

    private MyUser currentUser;
    private Intent intent;
    private ImageView iv_club_photo;
    private ImageView iv_send;
    private ImageView iv_return;
    private EditText et_content;
    private PhotoUtils photoUtils;
    private boolean isAdmin;
    private String clubObjId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = BmobUser.getCurrentUser(MyUser.class);
        intent = getIntent();
        isAdmin = intent.getBooleanExtra("isAdmin", false);
        clubObjId = intent.getStringExtra("clubObjId");

        setContentView(R.layout.activity_club_write_dynamic);
        initView();
    }

    private void initView() {
        iv_return = (ImageView) findViewById(R.id.iv_return);
        iv_send = (ImageView) findViewById(R.id.iv_send);
        iv_club_photo = (ImageView) findViewById(R.id.iv_club_photo);
        et_content = (EditText) findViewById(R.id.et_content);
        iv_return.setOnClickListener(this);
        iv_send.setOnClickListener(this);
        iv_club_photo.setOnClickListener(this);
    }

    String content;  //内容
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                finish();
                break;
            case R.id.iv_send:
                content = et_content.getText().toString();
                if(!TextUtils.isEmpty(content.trim())){
                    if(photoFile != null){
                        uploadPic(PHOTO);
                    }else if(photoAlbumFile != null){
                        uploadPic(ALBUM);
                    }
                }


                break;
            case R.id.iv_club_photo:
                showChooseDialog();
                break;
        }
    }

    /**
     * 上传图片和动态
     */
    ClubDynamic clubDynamic;
    BmobFile bmobFile = null;
    private void uploadPic(final int type) {
        showWaitingDialog(true);

        clubDynamic = new ClubDynamic();

        if(type == PHOTO){
            bmobFile = new BmobFile(photoFile);
        }else if(type == ALBUM){
            bmobFile = new BmobFile(photoAlbumFile);
        }
        if(bmobFile != null){
            bmobFile.upload(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if(e == null){
                        upload(clubDynamic);
                    }else{
                        showRetryDialog(type);
                    }
                }
            });
        }

    }

    /**
     * 上传社团动态
     * @param clubDynamic
     */
    private void upload(ClubDynamic clubDynamic) {
        clubDynamic.setUser(currentUser);
        clubDynamic.setClubId(clubObjId);
        clubDynamic.setContent(content);
        clubDynamic.setPicture(bmobFile);
        clubDynamic.setIdentity("暂未识别");
        clubDynamic.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e == null){
                    Toast.makeText(ClubWriteDynamicActivity.this,"发表动态成功",Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                    finish();
                }else{
                    Toast.makeText(ClubWriteDynamicActivity.this,"发表动态失败",Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }
            }
        });
    }

    /**
     * 上传图片失败重试
     * @param type
     */
    private void showRetryDialog(final int type) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("异常")
                .setContentText("加载出了点小问题,要重试一次吗？")
                .setConfirmText("再试试")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        uploadPic(type);
                    }
                });
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

    /**
     * 选择图片方式
     */
    private void showChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_change_head_pic, null);
        TextView tv_photo = (TextView) view.findViewById(R.id.tv_photo);
        TextView tv_album = (TextView) view.findViewById(R.id.tv_album);

        photoUtils = new PhotoUtils(ClubWriteDynamicActivity.this);
        tv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoUtils.openByType(PhotoUtils.PHOTO);
                dialog.dismiss();
            }
        });
        tv_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoUtils.openByType(PhotoUtils.ALBUM);
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
    }

    File photoFile;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PhotoUtils.PHOTO && resultCode == RESULT_OK) {
            photoAlbumFile = null;
            try {
                //将拍摄的照片显示出来
                photoFile = photoUtils.getPhotoFile(PhotoUtils.PHOTO);
                Log.e("获取到的图片路径:", photoFile.getPath() );
                if (photoFile != null) {
                    setBitmapOpts(iv_club_photo,photoFile);  //展示图片
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PhotoUtils.ALBUM && resultCode == RESULT_OK) {
            photoFile = null;  //先清除photoFile
            if (data != null) {
                Uri uri = data.getData();
                ContentResolver cr = this.getContentResolver();
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    saveAlbumAsFile(bitmap);
                    if(photoAlbumFile != null)
                        setBitmapOpts(iv_club_photo,photoAlbumFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将bitmap转为file
     *
     * @param bitmap
     */
    File photoAlbumFile;
    private void saveAlbumAsFile(Bitmap bitmap) {
        String path = Environment.getExternalStorageDirectory().getPath();
        File dirFile = new File(path + "/clubWriteDynamic/");
        if (dirFile.exists())
            photoUtils.deleteDirWithFile(dirFile);  //删除掉目录重新创建
        dirFile.mkdirs();

        //Todo
        String photoName = System.currentTimeMillis() + ".jpg";
        photoAlbumFile = new File(dirFile.getPath() + "/" + photoName);
        try{
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(photoAlbumFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setBitmapOpts(ImageView imageView,File photoFile) {
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
}
