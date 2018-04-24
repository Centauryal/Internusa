package com.supersoft.internusa.helper.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.supersoft.internusa.model.ChatGroupDetail;
import com.supersoft.internusa.model.ChatGroupModel;
import com.supersoft.internusa.model.CountryCode;
import com.supersoft.internusa.model.HistorytrxModel;
import com.supersoft.internusa.model.KomenModel;
import com.supersoft.internusa.model.Loghistory;
import com.supersoft.internusa.model.ProfilDB;
import com.supersoft.internusa.model.Promocode;
import com.supersoft.internusa.model.Row;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Centaury on 20/04/2018.
 */
public class DBHelper extends SQLiteOpenHelper {

    private String tbl_profil = "profil";
    private String tbl_lastid_info = "mitraid_info";
    private String TABLE_TIMELINE = "timeline_info";
    private String TABLE_TIMELINE_GALLERY = "timeline_info_gallery";
    private String TABLE_KOMENTAR = "komentar";


    private static DBHelper instance = null;

    private static final String DATABASE_NAME = "internusa";
    private static final int DATABASE_VERSION = 3;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("PRAGMA foreign_keys=ON");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tbl_profil + " (id_profil integer primary key, name text, email text, prefik text, hp text, pin text, activated integer, islogin integer,  img text, mem_id int)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + HistorytrxModel.TBL_NAME + " (" + HistorytrxModel.P_ID + " integer primary key, " + HistorytrxModel.P_NOHP + " text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tbl_lastid_info + " (ID integer primary key, MI_ID TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + CountryCode.TABLENAME + " (" + CountryCode.P_CODE + " TEXT primary key, " + CountryCode.P_NAME + " TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Promocode.TABLENAME + " (" + Promocode.F_ID + " TEXT primary key, " + Promocode.F_TITLE + " TEXT, " + Promocode.F_DESC + " TEXT, " + Promocode.F_DATETIME + " TEXT, " + Promocode.F_BGIMAGE + " TEXT, " + Promocode.F_ACTION + " TEXT, " + Promocode.F_ACTIVITY + " TEXT)");
        db.execSQL("Create table if not exists " + TABLE_TIMELINE + " (id integer primary key, idbase integer, avatar TEXT,totalkomentar integer,totallikes integer, judul text, isi text, pengirim text, tglkirim text, telp text, alamat text, status text, ownlike integer, last_update DATETIME DEFAULT CURRENT_TIMESTAMP )");
        db.execSQL("Create table if not exists " + TABLE_TIMELINE_GALLERY + " (id integer primary key, idbase integer, image TEXT)");
        db.execSQL("Create table if not exists " + KomenModel.TABLENAME + " (" + KomenModel.F_ID + " integer primary key, " + KomenModel.F_IDBASE + " integer, " + KomenModel.F_INFOID + " integer, " + KomenModel.F_PENGIRIM + " TEXT, " + KomenModel.F_KIRIMDARI + " TEXT, " + KomenModel.F_ISI + " TEXT, " + KomenModel.F_TGLKIRIM + " TEXT)");
        db.execSQL("Create table if not exists " + ChatGroupModel.TABLENAME + " (" + ChatGroupModel.P_ID + " integer primary key, " + ChatGroupModel.P_IDBASE + " integer, " + ChatGroupModel.P_CREATEDATE + " TEXT, " + ChatGroupModel.P_CONTENT + " TEXT, " + ChatGroupModel.P_CREATORID + " TEXT, " + ChatGroupModel.P_CREATORNAME + " TEXT, " + ChatGroupModel.P_CREATORAVATAR + " TEXT, " + ChatGroupModel.P_UNREAD + " integer, " + ChatGroupModel.P_IMPORTANT + " integer, " + ChatGroupModel.P_STATUS + " TEXT, " + ChatGroupModel.P_COLOR + " integer, " + ChatGroupModel.P_LASTNAMESENDER + " TEXT, " + ChatGroupModel.P_TITLETOPIK + " TEXT, " + ChatGroupModel.P_HASJOINED + " integer default 0, " + ChatGroupModel.P_LASTUPDATE + " DATETIME DEFAULT CURRENT_TIMESTAMP)");

        db.execSQL("Create table if not exists " + ChatGroupDetail.TABLENAME + " (" + ChatGroupDetail.P_ID + " integer primary key, " + ChatGroupDetail.P_IDBASE + " integer, " + ChatGroupDetail.P_CREATEDATE + " TEXT, " + ChatGroupDetail.P_CONTENT + " TEXT, " + ChatGroupDetail.P_CREATORID + " TEXT, " + ChatGroupDetail.P_CREATORNAME + " TEXT, " + ChatGroupDetail.P_TYPE + " integer, " + ChatGroupDetail.P_IDCHAT + " integer, " + ChatGroupDetail.P_REPLAYID + " integer, " + ChatGroupDetail.P_ISUNREAD + " integer , " + ChatGroupDetail.P_URIPATH + " text, " + ChatGroupDetail.P_DOWNLOAD_PATH + " text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Loghistory.TABLENAME + " (" + Loghistory.P_ID + " integer primary key, " + Loghistory.P_TITLE + " text," + Loghistory.P_DESCRIPTION + " text, " + Loghistory.P_TYPE + " text, " + Loghistory.P_ACTIVITY + " text, " + Loghistory.P_IDBASE + " integer default 0," + Loghistory.P_CREATEDATE + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2 && newVersion >= 2) {
            //db.execSQL("update " + Account.TABLENAME + " set "
            //        + Account.OPTIONS + " = " + Account.OPTIONS + " | 8");
        }
        onCreate(db);
    }

    public boolean isProfilExists() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from " + tbl_profil + " where id_profil = 1", null);
        boolean exists = c.getCount() > 0;
        c.close();
        return exists;
    }


    public ProfilDB getProfilDb() {
        //id_profil integer primary key, name text, email text, hp text, pin text, activated integer, islogin integer,  img text
        ProfilDB pf = new ProfilDB();
        if (isProfilExists()) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery("select * from " + tbl_profil + " where id_profil = 1", null);
            c.moveToFirst();
            pf.agenid = c.getString(2);
            pf.fullname = c.getString(1);
            pf.prefik = c.getString(3);
            pf.hp = c.getString(4);
            pf.pin = c.getString(5);
            pf.avatar = c.getString(8);
            if (!c.isClosed()) {
                c.close();
            }
        }
        return pf;
    }

    public void updateLastInfoId(String total) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("MI_ID", total);

        String where = "ID=?";
        String[] whereArgs = {"1"};
        int rows = db.update(tbl_lastid_info, values, where, whereArgs);
        if (rows == 0) {
            db.insert(tbl_lastid_info, null, values);
        }
    }

    public int getLastInfoId() {
        String[] columns = {"MI_ID"};
        String[] selectionArgs = {"1"};
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tbl_lastid_info,
                columns,
                "ID=?",
                selectionArgs,
                null, null, null);
        final int count;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        } else {
            count = 0;
        }
        cursor.close();
        return count;
    }

    public String getFieldProfil(String field, boolean isInteger) {
        if (isProfilExists()) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery("select " + field + " from " + tbl_profil + " where id_profil = 1", null);
            c.moveToFirst();
            String fieldName = (isInteger) ? String.valueOf(c.getInt(0)) : c.getString(0);
            if (!c.isClosed()) {
                c.close();
            }
            return fieldName;
        } else {
            return "unknown";
        }
    }


    public boolean addProfil(String name, String email, String prefik, String hp, String pin, String activated, String islogin, String img) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        Boolean success = false;
        try {
            ContentValues cv = new ContentValues();
            cv.put("id_profil", 1);
            if (!name.equals("")) cv.put("name", name);
            if (!email.equals("")) cv.put("email", email);
            if (!prefik.equals("")) cv.put("prefik", prefik);
            if (!hp.equals("")) cv.put("hp", hp);
            if (!pin.equals("")) cv.put("pin", pin);
            if (!activated.equals("")) cv.put("activated", Integer.valueOf(activated));
            if (!islogin.equals("")) cv.put("islogin", Integer.valueOf(islogin));
            if (!img.equals("")) cv.put("img", img);

            if (isProfilExists()) {
                db.update(tbl_profil, cv, "id_profil = 1", null);
            } else {
                db.insert(tbl_profil, null, cv);
            }
            db.setTransactionSuccessful();
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return success;
    }


    public boolean updateAvatar(String img) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        Boolean success = false;
        try {
            ContentValues cv = new ContentValues();
            cv.put("id_profil", 1);
            if (!img.equals("")) cv.put("img", img);

            if (isProfilExists()) {
                db.update(tbl_profil, cv, "id_profil = 1", null);
            } else {
                db.insert(tbl_profil, null, cv);
            }
            db.setTransactionSuccessful();
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return success;
    }

    public boolean setIsLogin(String islogin) {
        boolean ok = addProfil("", "", "", "", "", "", islogin, "");
        return ok;
    }



    /*
    public List<OrderItem> getAllProducts(){
        List<OrderItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from cart", null);
        if(c.getCount() > 0){
            c.moveToFirst();
            while(!c.isAfterLast()){
                OrderItem item = new OrderItem();
                item.qty = c.getInt(3);
                Product prod = new Product();
                prod.id = String.valueOf(c.getInt(0));
                prod.name = c.getString(1);
                prod.price = c.getInt(2);
                item.product = prod;
                items.add(item);
                c.moveToNext();
            }
            c.close();
        }
        return items;
    }
    */

    public ArrayList<HistorytrxModel> getHistoryTrx(String tujuan) {
        ArrayList<HistorytrxModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("select * from " + HistorytrxModel.TBL_NAME + " WHERE " + HistorytrxModel.P_NOHP + " like '%" + tujuan + "%'", null);
        while (cursor.moveToNext()) {
            list.add(HistorytrxModel.fromCursor(cursor));
        }
        cursor.close();
        return list;
    }

    public long addHistory(HistorytrxModel history) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        long rows = 0;
        try {

            String where = HistorytrxModel.P_NOHP + "=?";
            String[] whereArgs = {history.TUJUAN};

            rows = db.update(HistorytrxModel.TBL_NAME, history.getContentValues(), where, whereArgs);
            if (rows == 0)
                rows = db.insert(HistorytrxModel.TBL_NAME, null, history.getContentValues());
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return rows;
    }

    public void clearData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + HistorytrxModel.TBL_NAME);
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase db = super.getWritableDatabase();
        //db.execSQL("PRAGMA foreign_keys=ON;");
        return db;
    }

    public void createCountrycode(CountryCode message) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(CountryCode.TABLENAME, null, message.getContentValues());
    }

    public ArrayList<CountryCode> getCountryCode() {
        ArrayList<CountryCode> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("select * from " + CountryCode.TABLENAME, null);
        while (cursor.moveToNext()) {
            list.add(CountryCode.fromCursor(cursor));
        }
        cursor.close();
        return list;
    }


    public Promocode findPromocodeByID(String promoid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = {promoid};
        Cursor cursor = db.query(Promocode.TABLENAME, null,
                Promocode.F_ID + "=?", selectionArgs, null, null, null);
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        Promocode conversation = Promocode.fromCursor(cursor);
        cursor.close();
        return conversation;
    }


    public ArrayList<String> getGalleryTimeline(String idbase) {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = {idbase};
        Cursor cursor = db.query(TABLE_TIMELINE_GALLERY, null, "idbase=?", selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            list.add(cursor.getString(cursor.getColumnIndex("image")));
        }
        cursor.close();
        return list;
    }

    public ArrayList<Row> getTimeline(int idbase, int page, int offset) {
        ArrayList<Row> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        Log.e("timeline", "select * from " + TABLE_TIMELINE + " ORDER BY last_update DESC LIMIT " + offset);
        String where = (idbase > 0) ? " WHERE idbase = " + idbase : "";
        cursor = db.rawQuery("select * from " + TABLE_TIMELINE + where + " ORDER BY last_update DESC LIMIT " + offset, null);
        while (cursor.moveToNext()) {
            Row row = new Row();
            //(id integer primary key, idbase integer, avatar TEXT,totalkomentar integer,totallikes integer, judul text, isi text, pengirim text, tglkirim text, telp text, alamat text, status text, ownlike integer )
            row.setAvatar(cursor.getString(cursor.getColumnIndex("avatar")));
            row.setNama(cursor.getString(cursor.getColumnIndex("pengirim")));
            row.pengirim = cursor.getString(cursor.getColumnIndex("pengirim"));
            row.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            row.tglkirim = cursor.getString(cursor.getColumnIndex("tglkirim"));
            row.isi = cursor.getString(cursor.getColumnIndex("isi"));
            row.totallikes = "" + cursor.getInt(cursor.getColumnIndex("totallikes"));
            row.totalkomentar = "" + cursor.getInt(cursor.getColumnIndex("totalkomentar"));
            row.ownlike = cursor.getInt(cursor.getColumnIndex("ownlike")) == 1;
            row.telp = cursor.getString(cursor.getColumnIndex("telp"));
            row.last_update = cursor.getString(cursor.getColumnIndex("telp"));
            row.setId(cursor.getString(cursor.getColumnIndex("idbase")));
            //Log.e("getIdabase", "--" + cursor.getString(cursor.getColumnIndex("idbase")));
            row.setImage(getGalleryTimeline(cursor.getString(cursor.getColumnIndex("idbase"))));
            list.add(row);
        }
        cursor.close();
        return list;
    }

    public void updateLastUpdateTimeline(String idbase) {
        SQLiteDatabase db = this.getWritableDatabase();

        String SQL = "UPDATE " + TABLE_TIMELINE + " SET  last_update='" + ChatGroupModel.getDateTime() + "' WHERE idbase=" + idbase;
        db.execSQL(SQL);
        Log.e("updateLastUpdate", SQL);

    }

    public Integer LastTLid() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select MAX(idbase) as max_id from " + TABLE_TIMELINE, null);
        c.moveToFirst();
        int fieldName = (c.getString(0) == null) ? 0 : Integer.parseInt(c.getString(0));
        if (!c.isClosed()) {
            c.close();
        }
        return fieldName;
    }


    public Integer LastKomenid(int infoid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select MAX(idbase) as max_id from " + KomenModel.TABLENAME + " WHERE infoid=" + infoid, null);
        c.moveToFirst();
        int fieldName = (c.getString(0) == null) ? 0 : Integer.parseInt(c.getString(0));
        if (!c.isClosed()) {
            c.close();
        }
        return fieldName;
    }


    public ArrayList<KomenModel> getKomen(int infoid) {
        ArrayList<KomenModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("select * from " + KomenModel.TABLENAME + " WHERE infoid=" + infoid, null);
        while (cursor.moveToNext()) {
            list.add(KomenModel.fromCursor(cursor));
        }
        cursor.close();
        return list;
    }

    public Integer getMaxKomenByInfoid(int infoid) {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("select MAX(" + KomenModel.F_IDBASE + ") as idbase from " + KomenModel.TABLENAME + " WHERE infoid=" + infoid, null);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        } else {
            count = 0;
        }


        cursor.close();
        return count;
    }

    public long insertKomentar(KomenModel komen, boolean owncreate) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] oargv = {String.valueOf(komen.idbase), String.valueOf(komen.infoid)};
        String[] owargv = {String.valueOf(komen.id), String.valueOf(komen.infoid)};
        String where = (owncreate) ? KomenModel.F_ID + "=? AND " + KomenModel.F_INFOID + "=?" : KomenModel.F_IDBASE + "=? AND " + KomenModel.F_INFOID + "=?";

        long rows = db.update(KomenModel.TABLENAME, komen.getContentValues(), where, (owncreate) ? owargv : oargv);
        if (rows == 0)
            rows = db.insert(KomenModel.TABLENAME, null, komen.getContentValues());

        Constant.debug("insertKomen", "Insert or update " + rows + "-" + new Gson().toJson(komen));
        return rows;
    }


    public void updateOwnlikes(String idbase, int total) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("totallikes", total);
        String where = "idbase=?";
        String[] whereArgs = {idbase};
        int rows = db.update(TABLE_TIMELINE, values, where, whereArgs);
    }

    public void updateTotalLikes(String idbase, boolean ownlike) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ownlike", ownlike ? 1 : 0);
        String where = "idbase=?";
        String[] whereArgs = {idbase};
        int rows = db.update(TABLE_TIMELINE, values, where, whereArgs);
    }


    public void updateTotalKomenIncreament(String idbase) {
        SQLiteDatabase db = this.getWritableDatabase();

        String SQL = "UPDATE " + TABLE_TIMELINE + " SET totalkomentar = totalkomentar + 1 where idbase=" + idbase;
        db.execSQL(SQL);
        Log.e("updateIncreament", SQL);
    }

    public void updateTotalKomenIncreament(String idbase, int value) {
        SQLiteDatabase db = this.getWritableDatabase();

        String SQL = "UPDATE " + TABLE_TIMELINE + " SET totalkomentar = " + value + " where idbase=" + idbase;
        db.execSQL(SQL);
        Log.e("updateIncreament", SQL);
    }

    public Integer getTotalKomen(String idbase) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select totalkomentar from " + TABLE_TIMELINE + " where idbase = " + idbase, null);
        c.moveToFirst();
        int total = c.getInt(0);
        if (!c.isClosed()) {
            c.close();
        }
        return total;
    }

    public void insertTimeline(Row item) {
        //(id integer primary key, idbase integer, avatar TEXT,totalkomentar integer,totallikes integer, judul text, isi text, pengirim text, tglkirim text, telp text, alamat text, status text, ownlike integer )
        Log.e("insertTimeline", "Starting..");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("avatar", item.getAvatar());
        values.put("totalkomentar", item.totalkomentar);
        values.put("totallikes", item.totallikes);
        values.put("ownlike", item.ownlike ? 1 : 0);
        values.put("judul", item.isi);
        values.put("isi", item.isi);
        values.put("pengirim", item.pengirim);
        values.put("tglkirim", item.tglkirim);
        values.put("telp", item.telp);
        values.put("status", item.getStatus());

        String where = "idbase=?";
        String[] whereArgs = {item.getId()};
        int rows = db.update(TABLE_TIMELINE, values, where, whereArgs);
        Log.e("insertTimeline", "update row " + rows);
        if (rows == 0) {
            values.put("idbase", item.getId());
            db.insert(TABLE_TIMELINE, null, values);
            Log.e("timeline", "INSERTING ...idbase " + item.getId());
            if (item.getImage().size() > 0) {
                for (int i = 0; i < item.getImage().size(); i++) {
                    ContentValues content = new ContentValues();
                    content.put("image", item.getImage().get(i));
                    content.put("idbase", item.getId());
                    db.insert(TABLE_TIMELINE_GALLERY, null, content);
                }
            }
        }
    }

    public void deleteChatGroup(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + ChatGroupModel.TABLENAME + " where " + ChatGroupModel.P_IDBASE + "=" + id);
        db.execSQL("delete from " + ChatGroupDetail.TABLENAME + " where " + ChatGroupDetail.P_IDBASE + "=" + id);
    }


    public int countUnreadChat(String id) {
        String[] columns = {ChatGroupModel.P_UNREAD};
        String[] selectionArgs = {id, "0"};
        SQLiteDatabase db = this.getReadableDatabase();
        String SQL = "SELECT " + ChatGroupModel.P_UNREAD + " FROM " + ChatGroupModel.TABLENAME + " WHERE " + ChatGroupModel.P_IDBASE + " = ? AND " + ChatGroupModel.P_UNREAD + ">?";
        Cursor cursor = db.rawQuery(SQL, selectionArgs);
        final int count;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        } else {
            count = 0;
        }
        Log.e("count", "" + count + " SQL : " + SQL);
        cursor.close();
        return count;
    }

    public int countUnreadChatAllTopik() {
        String[] columns = {ChatGroupModel.P_UNREAD};
        String[] selectionArgs = {"0"};
        SQLiteDatabase db = this.getReadableDatabase();
        String SQL = "SELECT SUM(" + ChatGroupModel.P_UNREAD + ") FROM " + ChatGroupModel.TABLENAME + " WHERE " + ChatGroupModel.P_UNREAD + ">?";
        Cursor cursor = db.rawQuery(SQL, selectionArgs);
        final int count;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        } else {
            count = 0;
        }
        Log.e("countAllTopik", "" + count + " SQL : " + SQL);
        cursor.close();
        return count;
    }

    public ArrayList<ChatGroupModel> getChatgorup() {
        ArrayList<ChatGroupModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        String SQL = "select * from " + ChatGroupModel.TABLENAME + " ORDER BY " + ChatGroupModel.P_LASTUPDATE + " DESC, " + ChatGroupModel.P_IMPORTANT + " DESC";
        cursor = db.rawQuery(SQL, null);
        Constant.debug("chatgroup", SQL);
        while (cursor.moveToNext()) {

            list.add(ChatGroupModel.fromCursor(cursor));
        }
        cursor.close();
        return list;
    }

    public ArrayList<ChatGroupModel> getSingleChatgorupByIdbase(String idBase) {
        ArrayList<ChatGroupModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        Constant.debug("query", "select * from " + ChatGroupModel.TABLENAME + " WHERE " + ChatGroupModel.P_IDBASE + "=" + idBase + " ORDER BY " + ChatGroupModel.P_LASTUPDATE + " DESC");
        cursor = db.rawQuery("select * from " + ChatGroupModel.TABLENAME + " WHERE " + ChatGroupModel.P_IDBASE + "=" + idBase + " ORDER BY " + ChatGroupModel.P_LASTUPDATE + " ASC", null);
        while (cursor.moveToNext()) {

            list.add(ChatGroupModel.fromCursor(cursor));
        }
        cursor.close();
        return list;
    }

    public ArrayList<ChatGroupDetail> getChatgorupDetail(String groupid) {
        ArrayList<ChatGroupDetail> consolidatedList = new ArrayList<>();
        LinkedHashMap<String, Set<ChatGroupDetail>> groupedHashMap = new LinkedHashMap<>();
        Set<ChatGroupDetail> list = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("select * from " + ChatGroupDetail.TABLENAME + " WHERE " + ChatGroupDetail.P_IDBASE + "='" + groupid + "' ORDER BY " + ChatGroupDetail.P_CREATEDATE + " ASC", null);
        int inLoop = 0;
        while (cursor.moveToNext()) {

            String hashMapKey = Constant.parseDate(Constant.convertStringtoDate(cursor.getString(cursor.getColumnIndex(ChatGroupDetail.P_CREATEDATE))));
            if (groupedHashMap.containsKey(hashMapKey)) {
                groupedHashMap.get(hashMapKey).add(ChatGroupDetail.fromCursor(cursor));
            } else {
                list = new LinkedHashSet<>();
                list.add(ChatGroupDetail.fromCursor(cursor));
                groupedHashMap.put(hashMapKey, list);
            }

            inLoop++;
        }
        cursor.close();

        for (String date : groupedHashMap.keySet()) {
            ChatGroupDetail dateSeparate = new ChatGroupDetail();
            dateSeparate.LASTMESSAGE = Constant.DATE_SEPARATOR_BODY;
            dateSeparate.CREATEDATE = Constant.parseDate(Constant.convertStringtoDate(date));
            dateSeparate.TYPE = 7;
            consolidatedList.add(dateSeparate);
            for (ChatGroupDetail chatModel : groupedHashMap.get(date)) {
                consolidatedList.add(chatModel);
            }
        }

        return consolidatedList;
    }

    public long insertChatGroupById(ChatGroupModel chatGroup) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = ChatGroupModel.P_ID + "=?";
        String[] whereArgs = {String.valueOf(chatGroup.ID)};
        long rows = 0;
        rows = db.update(ChatGroupModel.TABLENAME, chatGroup.getContentValues(), where, whereArgs);
        if (rows == 0)
            rows = db.insert(ChatGroupModel.TABLENAME, null, chatGroup.getContentValues());

        Constant.debug("insertChatg", "Insert or update " + rows + "-");
        return rows;
    }

    public long insertChatGroupByIdBase(ChatGroupModel chatGroup) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = ChatGroupModel.P_IDBASE + "=?";
        String[] whereArgs = {chatGroup.IDBASE};
        long rows = 0;
        try {
            rows = db.update(ChatGroupModel.TABLENAME, chatGroup.getContentValues(), where, whereArgs);
            if (rows == 0)
                rows = db.insert(ChatGroupModel.TABLENAME, null, chatGroup.getContentValues());

            Constant.debug("insertChatidbase", "Insert or update " + rows + "-");
            return rows;
        } catch (SQLException e) {
            Constant.debug("insertChatidbase", e.getMessage());
        }
        return rows;
    }

    public long insertChatGroup(ChatGroupModel chatGroup) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = ChatGroupModel.P_ID + "=?";
        String[] whereArgs = {String.valueOf(chatGroup.ID)};
        long rows = 0;
        rows = db.update(ChatGroupModel.TABLENAME, chatGroup.getContentValues(), where, whereArgs);
        if (rows == 0)
            rows = db.insert(ChatGroupModel.TABLENAME, null, chatGroup.getContentValues());

        Constant.debug("insertChatg", "Insert or update " + rows + "-");
        return rows;
    }

    public void updateUnreadChat(boolean reset, String id, String pengirim, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        String field = (reset) ? "0" : ChatGroupModel.P_UNREAD + "+1";
        String SQL = "UPDATE " + ChatGroupModel.TABLENAME + " SET " + ChatGroupModel.P_UNREAD + " = " + field + ", " + ChatGroupModel.P_LASTNAMESENDER + "='" + pengirim + "', " + ChatGroupModel.P_CONTENT + "='" + message + "', " + ChatGroupModel.P_LASTUPDATE + "='" + ChatGroupModel.getDateTime() + "' WHERE " + ChatGroupModel.P_IDBASE + "=" + id;
        db.execSQL(SQL);
        Log.e("updaUnread", SQL);

    }

    public void resetUnreadChat(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String SQL = "UPDATE " + ChatGroupModel.TABLENAME + " SET unread = 0 WHERE " + ChatGroupModel.P_IDBASE + "=" + id;
        db.execSQL(SQL);
        Log.e("updaUnread", SQL);

    }

    public long updateLastMessageChatGroup(ChatGroupModel chatGroup) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = ChatGroupModel.P_IDBASE + "=?";
        String[] whereArgs = {String.valueOf(chatGroup.IDBASE)};
        ContentValues value = new ContentValues();
        value.put(ChatGroupModel.P_LASTNAMESENDER, chatGroup.NAMAPENGIRIMTERAKHIR);
        value.put(ChatGroupModel.P_CONTENT, chatGroup.LASTMESSAGE);
        value.put(ChatGroupModel.P_UNREAD, chatGroup.ISUNREAD);
        long rows = 0;
        rows = db.update(ChatGroupModel.TABLENAME, value, where, whereArgs);
        Constant.debug("updLMS", "updateLastMessageChatGroup" + rows + "-");
        return rows;
    }

    public long updateJoinChatGroup(ChatGroupModel chatGroup) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = ChatGroupModel.P_IDBASE + "=?";
        String[] whereArgs = {String.valueOf(chatGroup.IDBASE)};
        ContentValues value = new ContentValues();
        value.put(ChatGroupModel.P_HASJOINED, (chatGroup.JOINGROUP) ? 1 : 0);
        long rows = 0;
        rows = db.update(ChatGroupModel.TABLENAME, value, where, whereArgs);
        Constant.debug("updLMS", "updateLastMessageChatGroup" + rows + "-");
        return rows;
    }

    public long insertChatGroupDetail(String TAG, boolean ownself, ChatGroupDetail chatDetail) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = ChatGroupDetail.P_IDCHAT + "=?";
        String[] whereArgs = {String.valueOf(chatDetail.IDCHAT)};
        long rows = 0;
        //rows = db.update(ChatGroupDetail.TABLENAME,chatDetail.getContentValues(),where,whereArgs);
        //if(rows == 0)
        rows = db.insert(ChatGroupDetail.TABLENAME, null, chatDetail.getContentValues());
        updateUnreadChat(ownself, chatDetail.GROUPID, chatDetail.CREATORNAME, chatDetail.LASTMESSAGE);
        Constant.debug(TAG, "Insert or update " + rows + "-");
        return rows;
    }

    public long insertChatGroupDetailByIDCHAT(String TAG, boolean ownself, ChatGroupDetail chatDetail) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = ChatGroupDetail.P_IDCHAT + "=?";
        String[] whereArgs = {String.valueOf(chatDetail.IDCHAT)};
        long rows = 0;
        rows = db.update(ChatGroupDetail.TABLENAME, chatDetail.getContentValues(), where, whereArgs);
        if (rows == 0)
            rows = db.insert(ChatGroupDetail.TABLENAME, null, chatDetail.getContentValues());

        return rows;
    }

    public long insertLoghistory(Loghistory chatGroup) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = Loghistory.P_IDBASE + "=?";
        String[] whereArgs = {String.valueOf(chatGroup.IDBASE)};
        long rows = 0;
        rows = db.update(Loghistory.TABLENAME, chatGroup.getContentValues(), where, whereArgs);
        if (rows == 0)
            rows = db.insert(Loghistory.TABLENAME, null, chatGroup.getContentValues());

        Constant.debug("Loghistory", "Insert or update " + rows + "-");
        return rows;
    }

    public ArrayList<Loghistory> getLoghistory() {
        ArrayList<Loghistory> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("select * from " + Loghistory.TABLENAME + " WHERE 1 ORDER BY ID DESC LIMIT 1000", null);
        while (cursor.moveToNext()) {
            list.add(Loghistory.fromCursor(cursor));
        }
        cursor.close();
        return list;
    }
}
