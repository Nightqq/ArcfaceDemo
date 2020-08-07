package com.arcsoft.arcfacedemo.dao.helper;

import com.arcsoft.arcfacedemo.dao.CeWenInformDao;
import com.arcsoft.arcfacedemo.dao.bean.CeWenInform;

import java.util.List;

public class CeWenHelp {
    private static CeWenInformDao ceWenInformDao = DaoUtils.getDaoSession().getCeWenInformDao();


    //获取所有人脸
    public static CeWenInform getCeWenInform() {
        List<CeWenInform> list = ceWenInformDao.queryBuilder().list();
        if (list!=null&&list.size()>0){
            return list.get(0);
        }else {
            CeWenInform ceWenInform = new CeWenInform();
            saveCeWenInform(ceWenInform);
            return ceWenInform;
        }
    }
    public static void saveCeWenInform(CeWenInform ceWenInform) {
        if (ceWenInform != null) {
            deleteAllceWenInform();
            ceWenInformDao.insertOrReplace(ceWenInform);
        }
    }
    public static void deleteAllceWenInform() {
        ceWenInformDao.deleteAll();
    }
}
