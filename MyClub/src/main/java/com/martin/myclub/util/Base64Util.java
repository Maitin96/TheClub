package com.martin.myclub.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Base64Util {


	public static Options setBitmapOptions(){
		Options options = new Options();
		options.inSampleSize = 3;//图片大小，设置越大，图片越不清晰，占用空间越小
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds设回false
		options.inJustDecodeBounds = false;
        // 设置是否深拷贝，与inPurgeable结合使用
		options.inInputShareable = true;
        // 设置为True时，表示系统内存不足时可以被回 收，设置为False时，表示不能被回收。
		options.inPurgeable = true;
		//bitmap = BitmapFactory.decodeStream(fis);// 读取图片文件流，并解码成图片
		return options;
	}
}
