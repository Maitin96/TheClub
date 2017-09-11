package com.martin.myclub.view;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.martin.myclub.R;
import com.martin.myclub.bean.ClubApply;
import com.martin.myclub.util.Base64Util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Administrator on 2017/8/15.
 */

public class Fragment_apply1 extends Fragment implements View.OnClickListener {

    private static final int PHOTO = 1 << 1;
    private View rootView;

    private ClubApplyInterface.setPage mCallback;
    private Button btn_next;
    private ClubApply mClubApply = new ClubApply();
    private EditText et_club_name;
    private EditText et_club_introduction;  //简介
    private ImageView iv_club_logo;  //Logo

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_apply1, container, false);
        initViews();
        return rootView;
    }

    private void initViews() {
        et_club_name = (EditText) rootView.findViewById(R.id.et_club_name);
        et_club_introduction = (EditText) rootView.findViewById(R.id.et_club_introduction);
        iv_club_logo = (ImageView) rootView.findViewById(R.id.iv_club_logo);
        iv_club_logo.setOnClickListener(this);
        btn_next = (Button) rootView.findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context != null) {
            mCallback = (ClubApplyInterface.setPage) context;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                goNext();
                break;
            case R.id.iv_club_logo:
                openAlbum();
                break;
        }
    }

    private void goNext() {
        String name = et_club_name.getText().toString();
        String introduction = et_club_introduction.getText().toString();
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(introduction) && bmobFile != null) {
            mClubApply.setName(name);
            mClubApply.setIntroduction(introduction);

            mCallback.saveClubMsg(mClubApply, 1);
        } else {
            Toast.makeText(getActivity(), "您还有没填写完整哦", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 打开图库
     */
    private void openAlbum() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            open();
        }
    }

    /**
     * 打开图库
     */
    private String path;
    private File dirFile;

    private void open() {
        path = Environment.getExternalStorageDirectory().getPath();
        dirFile = new File(path + "/myImage/");
        Log.e("开始创建目录", dirFile.exists() + "");

        if (!dirFile.exists() && !dirFile.isDirectory()) {
            dirFile.mkdirs();
            Log.e("开始创建目录", "wancheng");
        }

        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PHOTO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    open();
                } else {
                    Toast.makeText(getContext(), "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHOTO:
                try {
                    if (resultCode == Activity.RESULT_OK) {
                        if (Build.VERSION.SDK_INT >= 19) {
                            //4.4及以上系统使用这个方法处理图片
                            handleImageOnKitKat(data);
                        } else {
                            //4.4以下系统用这个方法处理图片
                            handleImageBeforeKitKat(data);
                        }
                    }
                } catch (Exception e) {

                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(getContext(), uri)) {
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

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        disPlayImage(imagePath);
        Log.d("handleImageBeforeKitKat", "ImagePath:" + imagePath);
    }

    public String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContext().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /**
     * 显示图片到ImageView
     *
     * @param imagePath
     */
    private Bitmap bitmap;

    private void disPlayImage(String imagePath) {
        if (!TextUtils.isEmpty(imagePath)) {
            try {
                if (bitmap != null) {
                    bitmap = null;  //不关闭会内存溢出
                }
                BitmapFactory.Options opts = Base64Util.setBitmapOptions();
                bitmap = BitmapFactory.decodeFile(imagePath, opts);
                Log.e("imagePath", imagePath);
                saveBitmapAsFile(bitmap);
            } catch (Exception e) {
                Toast.makeText(getContext(), "上传图片出了点小问题", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 将bitmap转化为File
     *
     * @param b
     */
    private File picFile;
    private String logoName = "";

    private void saveBitmapAsFile(Bitmap b) {
        String clubName = et_club_name.getText().toString();
        if (!TextUtils.isEmpty(clubName)) {
            logoName = clubName + ".jpg";
            picFile = new File(dirFile.getPath() + "/" + logoName);
            if (picFile.exists()) {
                picFile.delete();
            }
        } else {
            Toast.makeText(getContext(), "请先填写社团名称", Toast.LENGTH_SHORT).show();
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(picFile));
            b.compress(Bitmap.CompressFormat.PNG, 80, bos);

            bos.flush();
            bos.close();
            //上传社团logo
            uploadLogoFile(picFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传并显示社团logo
     *
     * @param picFile
     */
    private BmobFile bmobFile;

    private void uploadLogoFile(File picFile) {
        if (picFile.exists()) {
            try{
                bmobFile = new BmobFile(picFile);
                mClubApply.setLogo(bmobFile);
                FileInputStream fis = new FileInputStream(bmobFile.getLocalFile());
                iv_club_logo.setImageBitmap(BitmapFactory.decodeStream(fis));
            }catch (Exception e){
                e.printStackTrace();
            }

        } else {
            Log.e("上传社团logo:", "图片文件不存在！");
        }
    }
}
