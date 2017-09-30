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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.martin.myclub.R;
import com.martin.myclub.bean.ClubSendActivity;
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
 * 社长发布活动Activity
 * Created by Edward on 2017/9/11.
 */

public class ClubWriteActivityActivity extends AppCompatActivity implements View.OnClickListener {

    private MyUser currentUser;
    private TextView title;   //导航栏标题
    private EditText et_title;
    private EditText et_content;
    private EditText et_start_time;
    private EditText et_end_time;
    private ImageView iv_return;
    private ImageView iv_send;
    private ImageView iv_open_album;
    private ImageView iv_photo;
    private Button btn_remove;
    private String clubId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = BmobUser.getCurrentUser(MyUser.class);
        clubId = getIntent().getStringExtra("clubObjId");
        setContentView(R.layout.activity_club_w_activity);
        initView();
    }

    private void initView() {
        title = (TextView) findViewById(R.id.title);
        setTitle(title);

        et_title = (EditText) findViewById(R.id.et_title);
        et_content = (EditText) findViewById(R.id.et_content);
        et_start_time = (EditText) findViewById(R.id.et_start_time);
        et_end_time = (EditText) findViewById(R.id.et_end_time);

        iv_open_album = (ImageView) findViewById(R.id.iv_open_album);  //点击打开相册
        iv_photo = (ImageView) findViewById(R.id.iv_photo);  //显示图片内容
        iv_return = (ImageView) findViewById(R.id.iv_return);
        iv_send = (ImageView) findViewById(R.id.iv_send);

        btn_remove = (Button) findViewById(R.id.btn_remove);

        iv_open_album.setOnClickListener(this);
        iv_return.setOnClickListener(this);
        iv_send.setOnClickListener(this);
        btn_remove.setOnClickListener(this);
    }

    /**
     * 设置导航栏标题
     *
     * @param title
     */
    private void setTitle(TextView title) {
        title.setText("发布活动");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                finish();
                break;
            case R.id.iv_open_album:
                openAlbum();
                break;
            case R.id.btn_remove:
                removePhoto();
                break;
            case R.id.iv_send:
                send();
                break;
        }
    }

    /**
     * 如果图片存在  移除图片
     */
    private void removePhoto() {
        if (iv_photo != null) {
            iv_photo.setImageDrawable(null);
            btn_remove.setVisibility(View.GONE);
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

    private void send() {
        String title = et_title.getText().toString().trim();
        String content = et_content.getText().toString();
        String start_time = et_start_time.getText().toString();
        String end_time = et_end_time.getText().toString();

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content) &&
                !TextUtils.isEmpty(start_time) && !TextUtils.isEmpty(end_time)) {
            upload(title, content, start_time, end_time);
        } else {
            Toast.makeText(ClubWriteActivityActivity.this, "请将信息填写完整哦~", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 上传社长发布的消息
     *
     * @param title
     * @param content
     * @param start_time
     * @param end_time
     */
    ClubSendActivity c;
    BmobFile bmobFile;

    private void upload(String title, String content, String start_time, String end_time) {
        showWaitingDialog(true);
        c = new ClubSendActivity();
        c.setClubId(clubId);
        c.setUser(currentUser);
        c.setTitle(title);
        c.setContent(content);
        c.setStart_time(start_time);
        c.setEnd_time(end_time);
        if (iv_photo.getDrawable() != null) {
            bmobFile = new BmobFile(photoAlbumFile);
            bmobFile.upload(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        saveMsg();   //上传发布的活动
                    } else {
                        showWaitingDialog(false);
                        Toast.makeText(ClubWriteActivityActivity.this, "上传图片出了点小问题...", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // 不包含图片
            saveMsg();
        }
    }

    /**
     * 保存并上传活动信息
     */
    private void saveMsg() {
        if (c != null) {
            if (bmobFile != null) {
                c.setPic(bmobFile);
            }
            c.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        Toast.makeText(ClubWriteActivityActivity.this, "发布活动成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        showErrorDialog();
                    }
                }
            });
        }
    }

    /**
     * 发布失败弹窗
     */
    SweetAlertDialog dialog;
    private void showErrorDialog() {
        dialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        dialog.setTitleText("异常");
        dialog.setContentText("发布出了点小问题,要重试一次吗？");
        dialog.setConfirmText("再试试");
        dialog.setCancelText("取消");
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.setContentText("上传中...");
                        sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                        saveMsg();
                    }
                });
        dialog.setCancelClickListener(null);
        dialog.show();
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
                        setBitmapOpts(iv_photo, photoAlbumFile);
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
