package com.arcsoft.arcfacedemo.dao.helper;

import com.arcsoft.arcfacedemo.dao.PoliceFaceDao;
import com.arcsoft.arcfacedemo.dao.bean.PoliceFace;
import com.arcsoft.arcfacedemo.net.bean.JsonPoliceFace;
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

    public static void savePoliceInfoAllListToDB(List<PoliceFace> list,OpenSaveListener openSaveListener) {
       if (list!=null&&list.size()>0){
           for (int i = 0; i < list.size(); i++) {
               policeFaceDao.insertOrReplace(list.get(i));
               if(i%50==0){
                   int progress=(i*100)/ list.size();
                   openSaveListener.opensave(""+progress);
                   LogUtils.a(progress);
               }
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
