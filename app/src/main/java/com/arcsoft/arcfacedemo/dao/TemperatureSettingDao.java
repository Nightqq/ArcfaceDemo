package com.arcsoft.arcfacedemo.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.arcsoft.arcfacedemo.dao.bean.TemperatureSetting;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TEMPERATURE_SETTING".
*/
public class TemperatureSettingDao extends AbstractDao<TemperatureSetting, Long> {

    public static final String TABLENAME = "TEMPERATURE_SETTING";

    /**
     * Properties of entity TemperatureSetting.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Forehead_X = new Property(1, String.class, "forehead_X", false, "FOREHEAD__X");
        public final static Property Forehead_xx = new Property(2, String.class, "forehead_xx", false, "FOREHEAD_XX");
        public final static Property Forehead_Y = new Property(3, String.class, "forehead_Y", false, "FOREHEAD__Y");
        public final static Property Forehead_Yx = new Property(4, String.class, "forehead_Yx", false, "FOREHEAD__YX");
        public final static Property Wenxia = new Property(5, String.class, "wenxia", false, "WENXIA");
        public final static Property Wenshang = new Property(6, String.class, "wenshang", false, "WENSHANG");
        public final static Property Cewenrizhi = new Property(7, boolean.class, "cewenrizhi", false, "CEWENRIZHI");
    }


    public TemperatureSettingDao(DaoConfig config) {
        super(config);
    }
    
    public TemperatureSettingDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TEMPERATURE_SETTING\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"FOREHEAD__X\" TEXT," + // 1: forehead_X
                "\"FOREHEAD_XX\" TEXT," + // 2: forehead_xx
                "\"FOREHEAD__Y\" TEXT," + // 3: forehead_Y
                "\"FOREHEAD__YX\" TEXT," + // 4: forehead_Yx
                "\"WENXIA\" TEXT," + // 5: wenxia
                "\"WENSHANG\" TEXT," + // 6: wenshang
                "\"CEWENRIZHI\" INTEGER NOT NULL );"); // 7: cewenrizhi
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TEMPERATURE_SETTING\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TemperatureSetting entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String forehead_X = entity.getForehead_X();
        if (forehead_X != null) {
            stmt.bindString(2, forehead_X);
        }
 
        String forehead_xx = entity.getForehead_xx();
        if (forehead_xx != null) {
            stmt.bindString(3, forehead_xx);
        }
 
        String forehead_Y = entity.getForehead_Y();
        if (forehead_Y != null) {
            stmt.bindString(4, forehead_Y);
        }
 
        String forehead_Yx = entity.getForehead_Yx();
        if (forehead_Yx != null) {
            stmt.bindString(5, forehead_Yx);
        }
 
        String wenxia = entity.getWenxia();
        if (wenxia != null) {
            stmt.bindString(6, wenxia);
        }
 
        String wenshang = entity.getWenshang();
        if (wenshang != null) {
            stmt.bindString(7, wenshang);
        }
        stmt.bindLong(8, entity.getCewenrizhi() ? 1L: 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TemperatureSetting entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String forehead_X = entity.getForehead_X();
        if (forehead_X != null) {
            stmt.bindString(2, forehead_X);
        }
 
        String forehead_xx = entity.getForehead_xx();
        if (forehead_xx != null) {
            stmt.bindString(3, forehead_xx);
        }
 
        String forehead_Y = entity.getForehead_Y();
        if (forehead_Y != null) {
            stmt.bindString(4, forehead_Y);
        }
 
        String forehead_Yx = entity.getForehead_Yx();
        if (forehead_Yx != null) {
            stmt.bindString(5, forehead_Yx);
        }
 
        String wenxia = entity.getWenxia();
        if (wenxia != null) {
            stmt.bindString(6, wenxia);
        }
 
        String wenshang = entity.getWenshang();
        if (wenshang != null) {
            stmt.bindString(7, wenshang);
        }
        stmt.bindLong(8, entity.getCewenrizhi() ? 1L: 0L);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public TemperatureSetting readEntity(Cursor cursor, int offset) {
        TemperatureSetting entity = new TemperatureSetting( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // forehead_X
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // forehead_xx
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // forehead_Y
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // forehead_Yx
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // wenxia
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // wenshang
            cursor.getShort(offset + 7) != 0 // cewenrizhi
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, TemperatureSetting entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setForehead_X(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setForehead_xx(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setForehead_Y(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setForehead_Yx(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setWenxia(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setWenshang(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setCewenrizhi(cursor.getShort(offset + 7) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(TemperatureSetting entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(TemperatureSetting entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(TemperatureSetting entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
