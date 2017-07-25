package com.martin.myclub.activity;


import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.martin.myclub.R;
import com.martin.myclub.bean.MyUser;
import com.martin.myclub.view.IdentityImageView;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;


/**
 * Created by Martin on 2017/7/12.
 * 个人信息
 */
public class PersonInfoActivity extends AppCompatActivity {

    private IdentityImageView headPic;
    private TextView personName;
    private CheckBox cbMale;
    private CheckBox cbFemale;
    private TextView tvSign;

    private Uri imageUri;

    MyUser user;
    String objectId;

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;

    public static final int REQUEST_NAME = 3;
    public static final int REQUEST_SIGN = 4;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preson_info);
        user = new MyUser();
        objectId = MyUser.getCurrentUser().getObjectId();
        initViews();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        user.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    Log.e("PersonInfoActivity","修改成功");
                } else {
                    Log.e("PersonInfoActivity","修改失败" + e);
                }
            }
        });
    }

    private void initViews(){
        headPic = (IdentityImageView) findViewById(R.id.info_hp);
        personName = (TextView) findViewById(R.id.person_name);
        tvSign = (TextView) findViewById(R.id.tv_sign);
        cbMale = (CheckBox) findViewById(R.id.cb_male);
        cbFemale = (CheckBox) findViewById(R.id.cb_female);

        BmobQuery<MyUser> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject(objectId, new QueryListener<MyUser>() {
            @Override
            public void done(MyUser myUser, BmobException e) {
                if (e == null){
                    //从服务器获取头像信息
                    String avatar = myUser.getAvatar();
                    headPic.getBigCircleImageView().setImageURI(Uri.parse(avatar));
                    //从服务器获取昵称
                    String name = myUser.getName();
                    personName.setText(name);
                    //获取签名
                    String sign = myUser.getSign();
                    tvSign.setText(sign);
                    //获取性别
                    boolean isMan = myUser.isMan();
                    cbMale.setChecked(isMan);

                    Log.e("QUERY!!!","初始化个人资料成功");
                } else {
                    Log.e("QUERY!!!","查询失败" + e);
                }

            }
        });
        /**
         * 设置性别CheckBox的逻辑
         */
        cbMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cbFemale.setChecked(false);
                } else {
                    cbFemale.setChecked(true);
                }
            }
        });

        cbFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cbMale.setChecked(false);
                } else {
                    cbMale.setChecked(true);
                }
            }
        });

       Button btBack = (Button) findViewById(R.id.bt_info_back);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initListener() {
        //点击更换头像
        headPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog();
            }
        });
        //点击更换昵称
        personName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击跳转到另一个页面更改昵称
                Intent intent = new Intent(PersonInfoActivity.this,SetNameActivity.class);
                startActivityForResult(intent,REQUEST_NAME);
            }
        });
        //点击更换签名
        tvSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonInfoActivity.this,SetSignActivity.class);
                startActivityForResult(intent,REQUEST_SIGN);

            }
        });
    }

    private void showSelectDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PersonInfoActivity.this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(PersonInfoActivity.this,R.layout.dialog_change_head_pic,null);

        TextView tvPhoto = (TextView) view.findViewById(R.id.tv_photo);
        TextView tvAlbum = (TextView) view.findViewById(R.id.tv_album);

        tvPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //拍照功能
                //创建File对象，用于存储拍照后的照片
                File outputImage = new File(getExternalCacheDir(),"output_image.jpg");
                try {
                    if (outputImage.exists()){
                        outputImage.delete();
                    }

                    outputImage.createNewFile();
                } catch (IOException e){
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24){
                    imageUri = FileProvider.getUriForFile(PersonInfoActivity.this,
                            "com.martin.myclub.fileprovider",outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }
                //启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);

                dialog.dismiss();
            }
        });

        tvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(PersonInfoActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(PersonInfoActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                } else {
                    openAlbum();
                }

                dialog.dismiss();
            }
        });

        dialog.setView(view);
        dialog.show();
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        Log.d("openAlbum", "data:" + intent.getData());
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(PersonInfoActivity.this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK){
                    try {
                        //将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        headPic.getBigCircleImageView().setImageBitmap(bitmap);
                        saveBitmapFile(bitmap);
                    } catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK){
                    if (Build.VERSION.SDK_INT >= 19){
                        //4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        //4.4以下系统用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            case REQUEST_NAME:
                //接受回传的intent，获取字符串，设置给TextView
                String name = data.getStringExtra("name");
                personName.setText(name);
                user.setName(name);
                break;
            case REQUEST_SIGN:
                //接受回传的intent，获取字符串，设置给TextView
                String sign = data.getStringExtra("sign");
                tvSign.setText(sign);
                user.setSign(sign);
                break;
        }
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        disPlayImage(imagePath);
        Log.d("handleImageBeforeKitKat", "ImagePath:" + imagePath);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("handleImageOnKitKat", "Uri:" + uri);       //
        Log.d("handleImageOnKitKat", "Uri.getAuthority:" + uri.getAuthority());       //
        Log.d("handleImageOnKitKat", "Uri.getScheme:" + uri.getScheme());       //
        if (DocumentsContract.isDocumentUri(PersonInfoActivity.this, uri)) {
            //如果是document类型的uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.provider.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];    //解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.provider.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.
                        withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        disPlayImage(imagePath);//根据图片路径显示图片
        Log.d("afterIf", "imagePath:" + imagePath);
    }

    private void disPlayImage(String imagePath) {
        Log.d("disPlayPath", "enter disPlayPath");
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            headPic.getBigCircleImageView().setImageBitmap(bitmap);
            saveBitmapFile(bitmap);

//            Log.d("disPlayPath","enter dis PlayPath . SET");
        } else {
            Toast.makeText(PersonInfoActivity.this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    public String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = PersonInfoActivity.this.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /**
     * 将bitmap转化为File
     * @param bitmap
     */
    private void saveBitmapFile(Bitmap bitmap){
        File file = new File("data/data/com.martin.myclub/avatar.jpg");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);

            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String path = file.getAbsolutePath();
        BmobFile bmobFile = new BmobFile("avatar",null,path);
        String url = bmobFile.getUrl();
        user.setAvatar(url);
        user.setName("小团");
        Log.e("TAG","路径"+ path + "Url" + url);
    }
}