package com.arcsoft.arcfacedemo.dao.helper;

import com.arcsoft.arcfacedemo.dao.PrisonerFaceDao;
import com.arcsoft.arcfacedemo.dao.bean.PrisonerFace;
import com.arcsoft.arcfacedemo.util.utils.LogUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

public class PrisonerFaceHelp {
    private static PrisonerFaceDao prisonerFaceDao = DaoUtils.getDaoSession().getPrisonerFaceDao();

    //获取所有人脸
    public static List<PrisonerFace> getPrisonerFaceListFromDB() {
        return prisonerFaceDao.queryBuilder().list();
    }
    //获取所有人名
    public static List<String> getPrisonerNameListFromDB() {
        List<String> name = new ArrayList<>();
        List<PrisonerFace> prisonerFaceListFromDB = getPrisonerFaceListFromDB();
        for (PrisonerFace prisonerFace : prisonerFaceListFromDB) {
            name.add(prisonerFace.getEmp_name());
        }
        return name;
    }
    //获取指定顺序的人脸
    public static PrisonerFace getPrisonerFaceFromName(int num) {
        return prisonerFaceDao.queryBuilder().list().get(num);
    }
    public static void savePrisonerFaceToDB(PrisonerFace prisonerFace) {
        if (prisonerFace != null) {
            prisonerFaceDao.insertOrReplace(prisonerFace);
        }
    }

    public static void savePrisonerresult(int num,int call_roll_result) {
        PrisonerFace prisonerFaceFromName = getPrisonerFaceFromName(num);
        if (prisonerFaceFromName.getCall_roll_result()==0){
            prisonerFaceFromName.setCall_roll_result(call_roll_result);
            savePrisonerFaceToDB(prisonerFaceFromName);
        }
    }

    public static void savePrisonerFaceAllListToDB(List<PrisonerFace> list) {
        if (list!=null&&list.size()>0){
            deleteAllPrisonerFaceAllList();
            for (PrisonerFace prisonerFace : list) {
                savePrisonerFaceToDB(prisonerFace);
            }
        }else {
            LogUtils.a("罪犯存储数据为空");
        }
    }
    public static void deleteAllPrisonerFaceAllList() {
        prisonerFaceDao.deleteAll();
    }

}
