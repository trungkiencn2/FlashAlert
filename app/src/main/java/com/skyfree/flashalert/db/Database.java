package com.skyfree.flashalert.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import static android.media.MediaFormat.KEY_DURATION;

/**
 * Created by KienBeu on 5/17/2018.
 */

public class Database extends SQLiteOpenHelper {

    private ArrayList<Pack> mlistPackage;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "FLASH_ALERT";
    private static final String TABLE_FLASH = "TABLE_FLASH";
    private static final String KEY_PK = "KEY_PK";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MUSIC_TABLE = "CREATE TABLE " + TABLE_FLASH + "(" + KEY_PK + " TEXT" + ")";
        db.execSQL(CREATE_MUSIC_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addPack(Pack pack){
        if(!checkPackExist(pack)){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_PK, pack.getPack());
            db.insert(TABLE_FLASH, null, values);

        }
    }

    public boolean checkPackExist(Pack pack){
        ArrayList<Pack> mListPackTest = getListPack();
        for(int i = 0; i<mListPackTest.size(); i++){
            if(pack.getPack().equals(mListPackTest.get(i).getPack())){
                return true;
            }
        }
        return false;
    }

    public ArrayList<Pack> getListPack(){
        mlistPackage = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select " + KEY_PK + " from " + TABLE_FLASH;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            mlistPackage.add(new Pack(cursor.getString(0)));
        }
        db.close();
        cursor.close();
        return mlistPackage;
    }

    public void deletePack(Pack pack){
        if(checkPackExist(pack)){
            SQLiteDatabase db = this.getWritableDatabase();
            String sql = "delete from " + TABLE_FLASH + " where " + KEY_PK + " = '" + pack.getPack() + "'";
            db.execSQL(sql);
            db.close();
        }
    }

    public void deleteAllPack(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "delete from " + TABLE_FLASH;
        db.execSQL(sql);
        db.close();
    }
}
