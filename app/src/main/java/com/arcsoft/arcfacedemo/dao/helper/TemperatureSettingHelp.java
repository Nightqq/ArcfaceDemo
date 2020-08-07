package com.arcsoft.arcfacedemo.dao.helper;

import com.arcsoft.arcfacedemo.dao.TemperatureSettingDao;
import com.arcsoft.arcfacedemo.dao.bean.TemperatureSetting;

import java.util.List;

public class TemperatureSettingHelp {
    private static TemperatureSettingDao temperatureSettingDao = DaoUtils.getDaoSession().getTemperatureSettingDao();

    public static TemperatureSetting getTerminalInformation() {
        List<TemperatureSetting> list = temperatureSettingDao.queryBuilder().list();
        if (list==null||list.size()<1){
            TemperatureSetting temperatureSetting = new TemperatureSetting();
            temperatureSetting.setWen3035("0.3");
            temperatureSetting.setWen3540("0.5");
            temperatureSetting.setWen40("0.8");
            temperatureSetting.setWenxia("35.2");
            temperatureSetting.setWenshang("37.3");
            savePoliceInfoToDB(temperatureSetting);
            return temperatureSetting;
        }else {
            return list.get(0);
        }
    }
    public static void savePoliceInfoToDB(TemperatureSetting temperatureSetting) {
        if (temperatureSetting != null) {
            deleteAllTemperatureSetting();
            temperatureSettingDao.insertOrReplace(temperatureSetting);
        }
    }
    public static void deleteAllTemperatureSetting() {
        temperatureSettingDao.deleteAll();
    }
}
