package com.arcsoft.arcfacedemo.dao.helper;

import com.arcsoft.arcfacedemo.dao.TerminalInformationDao;
import com.arcsoft.arcfacedemo.dao.bean.TerminalInformation;

import java.util.List;

public class TerminalInformationHelp {
    private static TerminalInformationDao terminalInformationDao = DaoUtils.getDaoSession().getTerminalInformationDao();
    public static TerminalInformation getTerminalInformation() {
        List<TerminalInformation> list = terminalInformationDao.queryBuilder().list();
        if (list==null||list.size()<1){
            TerminalInformation terminalInformation = new TerminalInformation();
            savePoliceInfoToDB(terminalInformation);
            return terminalInformation;
        }else {
            return list.get(0);
        }
    }
    public static void savePoliceInfoToDB(TerminalInformation terminalInformation) {
        if (terminalInformation != null) {
            deleteAllterminalInformation();
            terminalInformationDao.insertOrReplace(terminalInformation);
        }
    }

    public static void deleteAllterminalInformation() {
        terminalInformationDao.deleteAll();
    }
}
