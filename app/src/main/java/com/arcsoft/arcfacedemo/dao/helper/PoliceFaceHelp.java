package com.arcsoft.arcfacedemo.dao.helper;

import com.arcsoft.arcfacedemo.dao.PoliceFaceDao;
import com.arcsoft.arcfacedemo.dao.bean.PoliceFace;
import com.arcsoft.arcfacedemo.net.bean.JsonOneFace;
import com.arcsoft.arcfacedemo.net.bean.JsonPoliceFace;
import com.arcsoft.arcfacedemo.net.bean.JsonPoliceFace2;
import com.arcsoft.arcfacedemo.util.utils.FileUtils;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class PoliceFaceHelp {
    private static PoliceFaceDao policeFaceDao = DaoUtils.getDaoSession().getPoliceFaceDao();

    //获取所有人脸
    public static List<PoliceFace> getPoliceFaceAllListFromDB() {
        return policeFaceDao.queryBuilder().list();
    }

    //根据干警卡号获取数据
    public static PoliceFace getPoliceFaceByNum(String num) {
        QueryBuilder queryBuilder = policeFaceDao.queryBuilder();
        queryBuilder.where(PoliceFaceDao.Properties.EMP_ID.eq(num));
        if (queryBuilder.list() != null && queryBuilder.list().size() > 0) {
            return (PoliceFace) queryBuilder.list().get(0);
        }
        return null;
    }

    public static void savePoliceInfoToDB(PoliceFace PoliceFace) {
        if (PoliceFace != null) {
            policeFaceDao.insertOrReplace(PoliceFace);
        }
    }
    public static void savePoliceInfoToDB(JsonOneFace jsonOneFace) {
        if (jsonOneFace != null) {
            PoliceFace policeFace = new PoliceFace();
            policeFace.setEMP_ID(jsonOneFace.getEmp_id());
            policeFace.setEMP_NAME(jsonOneFace.getEmp_name());
            policeFace.setEMP_FEATURE(jsonOneFace.getPhoto());
            policeFaceDao.insertOrReplace(policeFace);
        }
    }

    public static void savePoliceInfoAllListToDB(List<JsonPoliceFace.DataBean> list, OpenSaveListener openSaveListener) {
        FileUtils.getFileUtilsHelp().saveupdatehelp(" 存储数据到本地");
        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                PoliceFace policeFace = new PoliceFace();
                policeFace.setEMP_NAME(list.get(i).getEmp_name());
                policeFace.setEMP_FEATURE(list.get(i).getPhoto());
                policeFace.setEMP_ID(list.get(i).getEmp_id());
                policeFaceDao.insertOrReplace(policeFace);

                if (list.get(i).getEmp_name().equals("翟佟")){
                    PoliceFace policeFace2 = new PoliceFace();
                    policeFace2.setEMP_NAME("翟佟");
                    policeFace2.setEMP_FEATURE(list.get(i).getPhoto());
                    policeFace2.setEMP_ID("2859CD");
                    policeFaceDao.insertOrReplace(policeFace2);
                }

            }
            FileUtils.getFileUtilsHelp().saveupdatehelp(" 数据存储本地完成,共"+list.size()+"条数据");
            LogUtils.a("数据存储本地完成,共"+list.size()+"条数据");
            openSaveListener.opensave("存储数据成功");
        }else {
            FileUtils.getFileUtilsHelp().saveupdatehelp(" 数据空存储失败");
            openSaveListener.opensave("存储数据失败");
        }
    }
    public static void savePoliceInfoAllListToDB2(List<JsonPoliceFace2.SourceBean.DataBean> list, OpenSaveListener openSaveListener) {
        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                PoliceFace policeFace = new PoliceFace();
                policeFace.setEMP_NAME(list.get(i).getEmp_name());
                policeFace.setEMP_FEATURE(list.get(i).getEmp_feature());
                policeFace.setEMP_ID(list.get(i).getEmp_id());
                policeFaceDao.insertOrReplace(policeFace);
            }
            LogUtils.a("数据存储本地完成,共"+list.size()+"条数据");
            openSaveListener.opensave("存储数据成功");
        }else {
            openSaveListener.opensave("存储数据失败");
        }
    }

    public static void deletePoliceInfoAllInDB(PoliceFace PoliceFace) {
        policeFaceDao.delete(PoliceFace);
    }

    public static void deleteAllPoliceInfoAllList() {

        policeFaceDao.deleteAll();
    }
    //存储监听
    private OpenSaveListener openSaveListener;
    public interface OpenSaveListener {
        void opensave(String message);
    }



}
