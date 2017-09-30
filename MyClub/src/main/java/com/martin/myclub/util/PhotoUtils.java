package com.martin.myclub.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.martin.myclub.activity.PersonInfoActivity;

import java.io.File;

/**
 * Created by Edward on 2017/9/11.
 */

public class PhotoUtils {
    public static final int PHOTO = 1;
    public static final int ALBUM = 2;
    Activity mActivity;
    Uri imageUri;

    public PhotoUtils(Activity activity){
        this.mActivity = activity;
    }

    /**
     * 打开相册
     * @param type 打开类型
     */
    private String path;
    private File dirFile;
    private File photoFile;
    public void openByType(int type){
        if(type == PHOTO){
            path = Environment.getExternalStorageDirectory().getPath();
            dirFile = new File(path + "/clubWriteDynamic/");  //一次性目录
            String photoName =  System.currentTimeMillis() + ".jpg";
            photoFile = new File(dirFile.getPath() + "/" + photoName);
            try{
                if(dirFile.exists())
                    deleteDirWithFile(dirFile);
                dirFile.mkdirs();
            }catch (Exception e){
                e.printStackTrace();
            }
            if (Build.VERSION.SDK_INT >= 24){
                imageUri = FileProvider.getUriForFile( mActivity,
                        "com.martin.myclub.fileprovider",photoFile);
            } else {
                imageUri = Uri.fromFile(photoFile);
            }
            //启动相机程序
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
            mActivity.startActivityForResult(intent,PHOTO);

        }else if(type == ALBUM){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            mActivity.startActivityForResult(intent,ALBUM);
        }

    }

    public File getPhotoFile(int type){
        if(type == PHOTO){
            return photoFile;
        }
        return null;
    }

    /**
     * 删除文件夹
     *
     * @param dir
     */
    public void deleteDirWithFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWithFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }
}
