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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.martin.myclub.R;
import com.martin.myclub.bean.CLubAnnouncement;
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
 * 社长发布通告的Activity
 * Created by Edward on 2017/9/11.
 */

public class ClubWriteAnnouncementActivity extends AppCompatActivity implements View.OnClickListener {

    private MyUser currentUser;
    private String clubObjId;
    private EditText et_title;
    private EditText et_content;
    private ImageView iv_camera;
    private ImageView iv_pic;
    private Button btn_remove;
    private Button bt_submit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = BmobUser.getCurrentUser(MyUser.class);
        clubObjId = getIntent().getStringExtra("clubObjId");
        setContentView(R.layout.activity_write_announcement);
        initView();
    }

    private void initView() {

        TextView title = (TextView) findViewById(R.id.title);
        title.setText("发布通告");


        ImageView iv_return = (ImageView) findViewById(R.id.iv_return);
        ImageView iv_send = (ImageView) findViewById(R.id.iv_send);
        iv_return.setOnClickListener(this);
        iv_send.setOnClickListener(this);

        et_title = (EditText) findViewById(R.id.et_title);
        et_content = (EditText) findViewById(R.id.et_content);

        iv_camera = (ImageView) findViewById(R.id.iv_camera);
        iv_pic = (ImageView) findViewById(R.id.iv_pic);
        iv_camera.setOnClickListener(this);

        btn_remove = (Button) findViewById(R.id.btn_remove);
        bt_submit = (Button) findViewById(R.id.bt_submit);//和send作用相同

        btn_remove.setOnClickListener(this);
        bt_submit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                finish();
                break;
            case R.id.iv_send:
                sendAnnouncement();
                break;
            case R.id.btn_remove:
                removePhoto();
                break;
            case R.id.bt_submit:
                sendAnnouncement();
                break;
            case R.id.iv_camera:
                openAlbum();
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

    /**
     * 发布通告
     */
    CLubAnnouncement c;
    BmobFile bmobFile;

    private void sendAnnouncement() {
        String title = et_title.getText().toString().trim();
        String content = et_title.getText().toString();
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
            showWaitingDialog(true);
            //发布
            c = new CLubAnnouncement();
            c.setUser(currentUser);
            c.setClubObjId(clubObjId);
            c.setTitle(title);
            c.setContent(content);
            if (iv_pic.getDrawable() != null) {
                bmobFile = new BmobFile(photoAlbumFile);
                bmobFile.upload(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            saveMsg();   //上传发布的活动
                        } else {
                            showWaitingDialog(false);
                            Toast.makeText(ClubWriteAnnouncementActivity.this, "上传图片出了点小问题...", Toast.LENGTH_SHORT).show();
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
        if (c != null) {
            if (bmobFile != null) {
                c.setPic(bmobFile);
            }
            c.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        showWaitingDialog(false);
                        Toast.makeText(ClubWriteAnnouncementActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        showWaitingDialog(false);
                        Toast.makeText(ClubWriteAnnouncementActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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
