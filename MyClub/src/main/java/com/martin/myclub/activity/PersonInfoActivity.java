package com.martin.myclub.activity;


import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Intent;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.martin.myclub.R;
import com.martin.myclub.bean.PersonInfo;
import com.martin.myclub.view.IdentityImageView;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


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

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private PersonInfo personInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preson_info);

//        personInfo = new PersonInfo();
        initViews();

//        personInfo.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//                if (e == null){
//                    Log.d("PersonInfoActivity","个人信息保存成功");
//                } else {
//                    Log.e("bmob","失败："+e.getMessage()+","+e.getErrorCode());
//                }
//            }
//        });
    }

    private void initViews(){
        headPic = (IdentityImageView) findViewById(R.id.info_hp);
        headPic.getBigCircleImageView().setImageResource(R.drawable.head_pic);

        headPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog();
            }
        });

        personName = (TextView) findViewById(R.id.person_name);
        tvSign = (TextView) findViewById(R.id.tv_sign);

        cbMale = (CheckBox) findViewById(R.id.cb_male);
        cbFemale = (CheckBox) findViewById(R.id.cb_female);

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
//                        saveBitmapFile(bitmap);
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
//            saveBitmapFile(bitmap);

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

    private void saveBitmapFile(Bitmap bitmap){
        File file = new File(getExternalCacheDir(),"pic.jpg");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            BmobFile bmobFile = new BmobFile(file);
            personInfo.setPic(bmobFile);

            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
