package com.martin.myclub.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.martin.myclub.R;
import com.martin.myclub.adapter.AdapterClubApply;
import com.martin.myclub.bean.ClubApply;
import com.martin.myclub.bean.MyUser;
import com.martin.myclub.view.ClubApplyInterface;
import com.martin.myclub.view.Fragment_apply1;
import com.martin.myclub.view.Fragment_apply2;
import com.martin.myclub.view.Fragment_apply3;
import com.martin.myclub.view.Fragment_apply4;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 社团申请页面
 * Created by Edward on 2017/8/15.
 */

public class ClubApplyActivity extends FragmentActivity implements ClubApplyInterface.setPage {

    private MyUser currentUser;
    private ViewPager view_pager;
    private String clubObjId = "";
    private ClubApply mClubApply = new ClubApply();
    private ArrayList<Fragment> list = new ArrayList<>();
    private ImageView iv_return;
    private int LAST_PAGE = 0;   //最后一页不监听返回


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_club);
        currentUser = BmobUser.getCurrentUser(MyUser.class);
        initView();
    }

    private void initView() {
        iv_return = (ImageView) findViewById(R.id.iv_return);
        iv_return.setOnClickListener(new View.OnClickListener() {     //取消创建社团
            @Override
            public void onClick(View v) {
                if(LAST_PAGE != 3){
                    showReturnDialog();
                }else{
                    finish();
                }
            }
        });
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        AdapterClubApply adapter = new AdapterClubApply(getSupportFragmentManager(), setFragment());
        view_pager.setAdapter(adapter);
    }

    private List<Fragment> setFragment() {
        list.add(new Fragment_apply1());
        list.add(new Fragment_apply2());
        list.add(new Fragment_apply3());
        list.add(new Fragment_apply4());
        return list;
    }

    @Override
    public void setPageByName(String name, int pageItem) {
        if (name.equals("finish")) {
            Intent intent = new Intent(ClubApplyActivity.this, ClubActivity.class);
            intent.putExtra("clubObjId",clubObjId);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void saveClubMsg(ClubApply clubApply, int flag) {
        switch (flag) {
            case 1:
                setParamsFromF1(clubApply);
                break;
            case 2:
                setParamsFromF2(clubApply);
                break;
            case 3:
                setParamsFromF3(clubApply);
                break;
        }
    }

    private BmobFile logo;
    private void setParamsFromF1(ClubApply clubApply) {
        showWaitingDialog(true);
        this.mClubApply.setName(clubApply.getName());
        this.mClubApply.setIntroduction(clubApply.getIntroduction());
        logo = clubApply.getLogo();
        logo.upload(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    uploadF1();
                }
            }
        });
    }

    private void uploadF1() {
        this.mClubApply.setLogo(logo);
        //先在Bmob创建出来 弹出等待dialog防止多次创建社团
        this.mClubApply.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                showWaitingDialog(false);
                if (e == null) {
                    clubObjId = s;
                    changePage(1);
                } else {
                    Toast.makeText(getApplicationContext(), "连接服务器失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setParamsFromF2(ClubApply clubApply) {
        this.mClubApply.setPhone(clubApply.getPhone());
        this.mClubApply.setEmail(clubApply.getEmail());
        this.mClubApply.setQQ(clubApply.getQQ());
        this.mClubApply.setRemarks(clubApply.getRemarks());

        changePage(2);
    }

    private void setParamsFromF3(ClubApply clubApply) {
        this.mClubApply.setUser(currentUser);

        this.mClubApply.setRealName(clubApply.getRealName());
        this.mClubApply.setPosition(clubApply.getPosition());
        this.mClubApply.setApplicatPhone(clubApply.getApplicatPhone());
        this.mClubApply.setApplicatQQ(clubApply.getApplicatQQ());

        confirm_Upload();
    }

    /**
     * view pager 换页
     */
    private void changePage(int pageItem) {
        view_pager.setCurrentItem(pageItem);
    }

    /**
     * 请稍等dialog
     *
     * @param show
     */
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
     * 上传社团信息
     */
    private void confirm_Upload() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("创建社团")
                .setContentText("您确定要创建该社团吗？")
                .setConfirmText("是的!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sDialog) {
                        if (!TextUtils.isEmpty(clubObjId)) {
                            /**设置管理员 */
                            BmobRelation relation = new BmobRelation();
                            relation.add(currentUser);
                            mClubApply.setAdmin(relation);
                            mClubApply.update(clubObjId, new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    showWaitingDialog(false);
                                    if (e == null) {
                                        sDialog
                                                .setTitleText("完成")
                                                .setContentText("您成功创建了" + mClubApply.getName() +"!")
                                                .setConfirmText("OK")
                                                .setConfirmClickListener(null)
                                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                                        setClub();
                                        changePage(3);

                                    } else {
                                        sDialog
                                                .setTitleText("失败")
                                                .setContentText("很遗憾,请再试一次吧~")
                                                .setConfirmText("我知道了")
                                                .setConfirmClickListener(null)
                                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                    }
                                }
                            });
                        }

                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                })
                .show();

        LAST_PAGE = 3;
        //取消监听事件
    }

    /**
     * 添加进已加入的社团
     */
    private void setClub() {
        MyUser myUser = new MyUser();
        myUser.setObjectId(currentUser.getObjectId());
        BmobRelation r = new BmobRelation();
        r.add(mClubApply);
        myUser.setClub(r);
        myUser.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    Log.e("ClubApplyActivity:", "当前用户添加社团成功");
                }else{
                    setClub();
                    Log.e("ClubApplyActivity:", "当前用户添加社团失败" + e.toString());
                }
            }
        });
    }

    /**
     * 撤销创建社团
     */
    private void showReturnDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("退出")
                .setContentText("您确定要取消创建社团吗？")
                .setConfirmText("是的")
                .setCancelText("我再想想")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sweetAlertDialog) {
                        mClubApply.delete(clubObjId, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e == null){
                                    Log.e("删除未创建完成的社团！", "success!" );
                                }else{
                                    Log.e("删除未创建完成的社团失败！", e.toString() );
                                }
                                sweetAlertDialog.dismiss();
                                finish();
                            }
                        });
                    }
                })
                .setCancelClickListener(null)
                .show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && LAST_PAGE != 3){
            showReturnDialog();
            return false;
        }else{
            return true;
        }
    }
}
