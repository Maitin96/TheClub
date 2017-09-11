package com.martin.myclub.view;

import com.martin.myclub.bean.ClubApply;

/**
 * Created by Administrator on 2017/8/15.
 */

public class ClubApplyInterface {
    public interface setPage {
        public void setPageByName(String name,int pageItem);
        public void saveClubMsg(ClubApply clubApply,int flag);
    }
}
