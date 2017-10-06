package com.martin.myclub.activity.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.martin.myclub.R;
import com.martin.myclub.activity.ClubManagerActivity;
import com.martin.myclub.activity.HistoryActivity;
import com.martin.myclub.activity.LoginActivity;
import com.martin.myclub.activity.MainActivity;
import com.martin.myclub.activity.MyFavorActivity;
import com.martin.myclub.activity.MyPostActivity;
import com.martin.myclub.activity.PersonInfoActivity;
import com.martin.myclub.activity.SettingActivity;
import com.martin.myclub.activity.SuggestActivity;
import com.martin.myclub.bean.MyUser;
import com.martin.myclub.view.IdentityImageView;
import com.martin.myclub.view.RewritePopwindow;

import org.w3c.dom.Text;

import java.net.Inet4Address;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by Martin on 2017/7/10.
 * 个人信息页面
 */
public class LayoutPerson extends Fragment implements View.OnClickListener {

    private View rootView;
    private IdentityImageView identityImageView;
    private FloatingActionButton floatingActionButton;
    private ImageView imageView;
    private ImageView ivNight;

    private RewritePopwindow mPopwindow;

    private boolean isNight = false;

    public static final int REQUEST_INFO = 6;
    private TextView userID;
    private TextView userSign;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_person, container, false);
        initViews();
        return rootView;
    }

//    /**
//     * 调用碎片的生命周期，获取从Activity中获取的数据并且设置给响应的组件
//     * @param activity
//     */
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        if (((PersonInfoActivity)activity).returnAvatar() != null){
//            identityImageView.getBigCircleImageView().setImageURI(Uri.parse(((PersonInfoActivity)activity).returnAvatar()));
//        }
//        if (((PersonInfoActivity)activity).returnName() != null){
//            userID.setText(((PersonInfoActivity)activity).returnName());
//        }
//        if (((PersonInfoActivity)activity).returnSign() != null){
//            userSign.setText(((PersonInfoActivity)activity).returnSign());
//        }
//    }

    private void initViews() {
        identityImageView = (IdentityImageView) rootView.findViewById(R.id.iiv);
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        imageView = (ImageView) rootView.findViewById(R.id.iv_bg);
        ivNight = (ImageView) rootView.findViewById(R.id.iv_night);
        userID = (TextView) rootView.findViewById(R.id.user_ID);
        userSign = (TextView) rootView.findViewById(R.id.user_info_sign);

        BmobQuery<MyUser> bmobQuery = new BmobQuery<>();
        String objectId = MyUser.getCurrentUser().getObjectId();
        bmobQuery.getObject(objectId, new QueryListener<MyUser>() {
            @Override
            public void done(MyUser myUser, BmobException e) {
                if (e == null){
                    //设置tab4的头像显示
//                    String avatar = myUser.getAvatar();
//
// identityImageView.getBigCircleImageView().setImageURI(Uri.parse(avatar));
                    //设置tab4昵称
                    String name = myUser.getName();
                    userID.setText(name);
                    //设置tab4签名
                    String sign = myUser.getSign();
                    userSign.setText(sign);
                }
            }
        });

        //夜间模式
        ivNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNight) {
                    ivNight.setImageResource(R.drawable.switch_off);
                } else {
                    ivNight.setImageResource(R.drawable.switch_on);
                }
                isNight = !isNight;
            }
        });

        LinearLayout myPost = (LinearLayout) rootView.findViewById(R.id.my_post);
        LinearLayout myFavor = (LinearLayout) rootView.findViewById(R.id.my_favor);
        LinearLayout history = (LinearLayout) rootView.findViewById(R.id.history);

        LinearLayout clubManage = (LinearLayout) rootView.findViewById(R.id.club_manage);
        LinearLayout share = (LinearLayout) rootView.findViewById(R.id.share);
        LinearLayout suggest = (LinearLayout) rootView.findViewById(R.id.suggest);

        Button btSingOut = (Button) rootView.findViewById(R.id.bt_out);
        btSingOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                BmobUser.logOut();
                startActivity(intent);
                getActivity().finish();
            }
        });

        myPost.setOnClickListener(this);
        myFavor.setOnClickListener(this);
        history.setOnClickListener(this);

        clubManage.setOnClickListener(this);
        share.setOnClickListener(this);
        suggest.setOnClickListener(this);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置页面
                Intent intent = new Intent(getContext(), SettingActivity.class);
                startActivity(intent);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog();
            }
        });

        //填充大图片
        identityImageView.getBigCircleImageView().setImageResource(R.drawable.head_pic);

        //改变图片比例大小，
        identityImageView.setRadiusScale(0.1f);

        //填充标识小图片
//        identityImageView.getSmallCircleImageView().setImageResource(R.drawable.vip);


        //增加边框
        identityImageView.setBorderWidth(20);
        identityImageView.setBorderColor(R.color.white);

        //增加进度条，以及改变的角度
        identityImageView.setIsprogress(true);
        identityImageView.setProgressColor(R.color.white);
        identityImageView.setProgress(120);

        identityImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PersonInfoActivity.class);
//                startActivityForResult(intent,REQUEST_INFO);
                startActivity(intent);
            }
        });
    }

    private void showSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final AlertDialog dialog = builder.create();
        View view = View.inflate(getContext(), R.layout.dialog_change_background, null);

        TextView changeBackground = (TextView) view.findViewById(R.id.change_bg);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);

        changeBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断运行时权限
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        startActivityForResult(intent, 3);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(getContext(), "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 3:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        //4.4以下系统用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
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

    private void disPlayImage(String imagePath) {
        Log.d("disPlayPath", "enter disPlayPath");
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);

//            Log.d("disPlayPath","enter dis PlayPath . SET");
        } else {
            Toast.makeText(getContext(), "failed to get image", Toast.LENGTH_SHORT).show();
        }
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_post:
                Intent intentPost = new Intent(getContext(), MyPostActivity.class);
                startActivity(intentPost);
                break;
            case R.id.my_favor:
                Intent intentFavor = new Intent(getContext(), MyFavorActivity.class);
                startActivity(intentFavor);
                break;
            case R.id.history:
                Intent intentHistory = new Intent(getContext(), HistoryActivity.class);
                startActivity(intentHistory);
                break;
            case R.id.club_manage:
                Intent intentClub = new Intent(getContext(), ClubManagerActivity.class);
                startActivity(intentClub);
                break;
            case R.id.share:
                showPopupWindow(v);
                break;
            case R.id.suggest:
                Intent intentSuggest = new Intent(getContext(), SuggestActivity.class);
                startActivity(intentSuggest);
                break;
        }
    }


    private void showPopupWindow(View view) {
        mPopwindow = new RewritePopwindow(getActivity(), itemsOnClick);
        mPopwindow.showAtLocation(view,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            mPopwindow.dismiss();
            mPopwindow.backgroundAlpha(getActivity(), 1f);
            switch (v.getId()) {
                case R.id.weixinghaoyou:
                    Toast.makeText(getActivity(), "微信好友", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.pengyouquan:
                    Toast.makeText(getActivity(), "朋友圈", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.qqhaoyou:
                    Toast.makeText(getActivity(), "QQ好友", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.qqkongjian:
                    Toast.makeText(getActivity(), "QQ空间", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
}
