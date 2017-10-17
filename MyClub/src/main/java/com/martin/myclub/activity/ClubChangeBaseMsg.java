package com.martin.myclub.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

import com.bumptech.glide.Glide;
import com.martin.myclub.R;
import com.martin.myclub.bean.ClubApply;
import com.martin.myclub.util.PhotoUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by 1033834071 on 2017/10/16.
 */

public class ClubChangeBaseMsg extends AppCompatActivity implements View.OnClickListener{

    private String clubObjId;
    private EditText et_name;
    private EditText et_realName;
    private EditText et_email;
    private EditText et_applicatQQ;
    private EditText et_applicatPhone;
    private EditText et_phone;
    private EditText et_introduction;
    private EditText et_remarks;
    private ImageView iv_pic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clubObjId = getIntent().getStringExtra("clubObjId");
        setContentView(R.layout.activity_club_basemsg);
        initView();
    }

    private void initView() {
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("基本信息");

        ImageView iv_return = (ImageView) findViewById(R.id.iv_return);
        iv_return.setOnClickListener(this);

        ImageView iv_send = (ImageView) findViewById(R.id.iv_send);
        iv_send.setClickable(false);
        iv_send.setVisibility(View.GONE);

        Button btn_update = (Button) findViewById(R.id.btn_update);
        btn_update.setOnClickListener(this);

        et_name = (EditText) findViewById(R.id.et_name);
        et_realName = (EditText) findViewById(R.id.et_realName);
        et_email = (EditText) findViewById(R.id.et_email);
        et_applicatQQ = (EditText) findViewById(R.id.et_applicatQQ);
        et_applicatPhone = (EditText) findViewById(R.id.et_applicatPhone);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_introduction = (EditText) findViewById(R.id.et_introduction);
        et_remarks = (EditText) findViewById(R.id.et_remarks);

        iv_pic = (ImageView) findViewById(R.id.iv_pic);
        iv_pic.setOnClickListener(this);

        requestData();
    }

    private void requestData() {
        BmobQuery<ClubApply> query = new BmobQuery<>();
        query.getObject(clubObjId, new QueryListener<ClubApply>() {
            @Override
            public void done(ClubApply clubApply, BmobException e) {
                if( e== null ){

                    setContentUI(clubApply);
                }else{
                    Toast.makeText(getApplicationContext(),"数据获取失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 设置数据
     * @param clubApply
     */
    private void setContentUI(ClubApply clubApply) {
        et_applicatPhone.setText(clubApply.getApplicatPhone());
        et_applicatQQ.setText(clubApply.getApplicatQQ());
        et_email.setText(clubApply.getEmail());
        et_introduction.setText(clubApply.getIntroduction());
        et_name.setText(clubApply.getName());
        et_phone.setText(clubApply.getPhone());
        et_realName.setText(clubApply.getRealName());
        et_remarks.setText(clubApply.getRemarks());

        if(clubApply.getLogo() != null){
            Glide.with(getApplicationContext()).load(clubApply.getLogo().getUrl()).into(iv_pic);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_return:
                finish();
                break;
            case R.id.btn_update:
                update();
                break;
            case R.id.iv_pic:
                openAlbum();
                break;
        }
    }

    ClubApply clubApply;
    BmobFile bmobFile;
    private void update() {
        showWaitingDialog(true);

        String applicatPhone = et_applicatPhone.getText().toString();
        String applicatQQ = et_applicatQQ.getText().toString();
        String email = et_email.getText().toString();
        String introduction = et_introduction.getText().toString();
        String name = et_name.getText().toString();
        String phone = et_phone.getText().toString();
        String realName = et_realName.getText().toString();
        String remarks = et_remarks.getText().toString();

        if(!TextUtils.isEmpty(applicatPhone) && !TextUtils.isEmpty(applicatQQ) && !TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(introduction) && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone)
                && !TextUtils.isEmpty(realName) && !TextUtils.isEmpty(remarks)){
            clubApply = new ClubApply();
            clubApply.setObjectId(clubObjId);
            clubApply.setApplicatPhone(applicatPhone);
            clubApply.setApplicatQQ(applicatQQ);
            clubApply.setEmail(email);
            clubApply.setIntroduction(introduction);
            clubApply.setName(name);
            clubApply.setPhone(phone);
            clubApply.setRealName(realName);
            clubApply.setRemarks(remarks);

            if(photoAlbumFile != null){  //修改了社团logo
                 bmobFile = new BmobFile(photoAlbumFile);
                bmobFile.upload(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e == null){
                            clubApply.setLogo(bmobFile);
                            clubApply.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e == null){
                                        Toast.makeText(getApplicationContext(),"修改信息成功！",Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(getApplicationContext(),"修改信息失败",Toast.LENGTH_SHORT).show();
                                    }
                                    showWaitingDialog(false);
                                }
                            });
                        }else{
                            Toast.makeText(getApplicationContext(),"上传logo失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else{
                clubApply.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e == null){
                            Toast.makeText(getApplicationContext(),"修改信息成功！",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(),"修改信息失败",Toast.LENGTH_SHORT).show();
                        }
                        showWaitingDialog(false);
                    }
                });
            }


        }else{
            showWaitingDialog(false);
            Toast.makeText(getApplicationContext(),"数据获取失败",Toast.LENGTH_SHORT).show();
        }
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
}
