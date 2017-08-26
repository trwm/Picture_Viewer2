package name.weiskirchner.picture_viewer2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.Image;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import name.weiskirchner.picture_viewer2.DatabaseContract.ImageTable;

/**
 * Created by michael on 25.05.17.
 */

public class DatabaseController extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PictureViewer.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ImageTable.TABLE_NAME + " (" +
                    ImageTable._ID + " INTEGER PRIMARY KEY," +
                    ImageTable.COLUMN_NAME_FILENAME + " TEXT," +
                    ImageTable.COLUMN_NAME_RECEIVEDATE + " INT," +
                    ImageTable.COLUMN_NAME_FILESIZE + " INTEGER," +
                    ImageTable.COLUMN_NAME_SENDER + " TEXT," +
                    ImageTable.COLUMN_NAME_SENDERNUMBER + " TEXT," +
                    ImageTable.COLUMN_NAME_TEXT + " TEXT," +
                    ImageTable.COLUMN_NAME_NEW + " INTEGER," +
                    ImageTable.COLUMN_NAME_FOREVERNEW + " INTEGER," +
                    ImageTable.COLUMN_NAME_INVISIBLE + " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ImageTable.TABLE_NAME;

    public DatabaseController(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void deleteDatabase() {
        getWritableDatabase().execSQL(SQL_DELETE_ENTRIES);
        getWritableDatabase().execSQL(SQL_CREATE_ENTRIES);
    }

    public void addImageFile(PVimage pvImage) {
        //String sql_add_ImageFile = ImageTable.COLUMN_NAME_FILENAME"";
        ContentValues content = new ContentValues();
        content.put(ImageTable.COLUMN_NAME_FILENAME, pvImage.getFilename());
        content.put(ImageTable.COLUMN_NAME_RECEIVEDATE, pvImage.getReceivedate());
        content.put(ImageTable.COLUMN_NAME_FILESIZE, pvImage.getFilesize());
        //content.put(ImageTable.COLUMN_NAME_SENDER, pvImage.getSender());
        content.put(ImageTable.COLUMN_NAME_SENDERNUMBER, pvImage.getSendernumber());
        content.put(ImageTable.COLUMN_NAME_NEW, 1);
        content.put(ImageTable.COLUMN_NAME_FOREVERNEW, 0);
        content.put(ImageTable.COLUMN_NAME_INVISIBLE, 0);
        getWritableDatabase().insert(ImageTable.TABLE_NAME, null, content);
        Log.d("addImageFile", "content: " + content.toString());
    }

    public void getImageFile(int imageID) {

    }

    public void updateForevernew(int imageID, boolean forevernew) {
        String sqlStatement;
        if(forevernew) {
            sqlStatement = "UPDATE " + ImageTable.TABLE_NAME + " SET " + ImageTable.COLUMN_NAME_FOREVERNEW + " = 1 WHERE " + ImageTable._ID + "=" + imageID;
        }else {
            sqlStatement = "UPDATE " + ImageTable.TABLE_NAME + " SET " + ImageTable.COLUMN_NAME_FOREVERNEW + " = 0 WHERE " + ImageTable._ID + "=" + imageID;
        }
        Log.d("updateForevernew","SQL: " + sqlStatement);
        try{
            getWritableDatabase().execSQL(sqlStatement);
        }catch (Exception e) {
            Log.d("updateInvisible", "Exception" + e.getMessage());
        }
    }

    public void updateSendernumber(int imageID, String sendernumber) {
        String sqlStatement;
        sqlStatement = "UPDATE " + ImageTable.TABLE_NAME + " SET " + ImageTable.COLUMN_NAME_SENDERNUMBER + " = " + sendernumber + " WHERE " + ImageTable._ID + "=" + imageID;
        Log.d("updateSendernumber","SQL: " + sqlStatement);
        try{
            getWritableDatabase().execSQL(sqlStatement);
        }catch (Exception e) {
            Log.d("updateSendernumber", "Exception" + e.getMessage());
        }

    }

    public void updateInvisible(int imageID, boolean invisible) {
        String sqlStatement;
        if(invisible) {
            sqlStatement = "UPDATE " + ImageTable.TABLE_NAME + " SET " + ImageTable.COLUMN_NAME_INVISIBLE + " = 1 WHERE " + ImageTable._ID + "=" + imageID;
        }else {
            sqlStatement = "UPDATE " + ImageTable.TABLE_NAME + " SET " + ImageTable.COLUMN_NAME_INVISIBLE + " = 0 WHERE " + ImageTable._ID + "=" + imageID;
        }
        Log.d("updateInvisible","SQL: " + sqlStatement);
        try{
            getWritableDatabase().execSQL(sqlStatement);
        }catch (Exception e) {
            Log.d("updateSInvisible", "Exception" + e.getMessage());
        }
    }

    public List<PVimage> getAllImages() {
        List<PVimage> imageList = new ArrayList<>();
        String sqlStatement;

        sqlStatement = "SELECT * from " + ImageTable.TABLE_NAME;
        Cursor cursor = getReadableDatabase().rawQuery(sqlStatement, null);
        while(cursor.moveToNext()) {
            int imageID = cursor.getInt(cursor.getColumnIndexOrThrow(ImageTable._ID));
            String filename = cursor.getString(cursor.getColumnIndexOrThrow(ImageTable.COLUMN_NAME_FILENAME));
            long receivedate = cursor.getLong(cursor.getColumnIndexOrThrow(ImageTable.COLUMN_NAME_RECEIVEDATE));
            long filesize = cursor.getLong(cursor.getColumnIndexOrThrow(ImageTable.COLUMN_NAME_FILESIZE));
            String sender = cursor.getString(cursor.getColumnIndexOrThrow(ImageTable.COLUMN_NAME_SENDER));
            String sendernumber = cursor.getString(cursor.getColumnIndexOrThrow(ImageTable.COLUMN_NAME_SENDERNUMBER));
            String text = cursor.getString(cursor.getColumnIndexOrThrow(ImageTable.COLUMN_NAME_TEXT));
            int newimage = cursor.getInt(cursor.getColumnIndexOrThrow(ImageTable.COLUMN_NAME_NEW));
            int forevernew = cursor.getInt(cursor.getColumnIndexOrThrow(ImageTable.COLUMN_NAME_FOREVERNEW));
            int invisible = cursor.getInt(cursor.getColumnIndexOrThrow(ImageTable.COLUMN_NAME_INVISIBLE));

            //Log.d("getAllImages", "Listentry: " + imageID + filename + " " + receivedate + " " + filesize + sender + sendernumber + text + newimage + forevernew + invisible);
            imageList.add(new PVimage(imageID, filename, receivedate, filesize, sender, sendernumber, text, newimage, forevernew, invisible));
        }
        cursor.close();


        return imageList;
    }


    public List<PVimage> getAllImages2BeDisplayed() {
        List<PVimage> imageList = new ArrayList<>();
        String sqlStatement;
        sqlStatement = "SELECT count(*) from " + ImageTable.TABLE_NAME + " WHERE " + ImageTable.COLUMN_NAME_RECEIVEDATE + " >= " + (System.currentTimeMillis() - (60*24*3600*1000));
        //sqlStatement = "SELECT count(*) from " + ImageTable.TABLE_NAME + " WHERE " + ImageTable.COLUMN_NAME_RECEIVEDATE + " >= " + (System.currentTimeMillis());
        Log.d("getAllImages2BeDispl", "SQL1: " + sqlStatement);
        //sqlStatement = "SELECT receivedate from " + ImageTable.TABLE_NAME;
        //Log.d("getAllImages2BeDispl", "SQL: " + sqlStatement);

        Cursor cursor = getReadableDatabase().rawQuery(sqlStatement, null);
        cursor.moveToFirst();
        Integer imagecount = cursor.getInt(cursor.getColumnIndexOrThrow("count(*)"));
        Log.d("getAllImages2BeDispl", "Imagecount: " + imagecount);
        if(imagecount<50) {
            sqlStatement = "SELECT * from " + ImageTable.TABLE_NAME + " WHERE " + ImageTable.COLUMN_NAME_FOREVERNEW + " = 1 OR (" + ImageTable.COLUMN_NAME_INVISIBLE + " = 0 AND " + ImageTable._ID + " > (SELECT max(" + ImageTable._ID + ") - 50 FROM " + ImageTable.TABLE_NAME + ")) ORDER BY " + ImageTable.COLUMN_NAME_RECEIVEDATE + " ASC";
        }else{
            sqlStatement = "SELECT * from " + ImageTable.TABLE_NAME + " WHERE " + ImageTable.COLUMN_NAME_FOREVERNEW + " = 1 OR (" + ImageTable.COLUMN_NAME_INVISIBLE + " = 0 AND " + ImageTable.COLUMN_NAME_RECEIVEDATE + " >= " + (System.currentTimeMillis() + (60*24*3600*1000)) + ") ORDER BY " + ImageTable.COLUMN_NAME_RECEIVEDATE + " ASC";
        }
        Log.d("getAllImages2BeDispl", "SQL2: " + sqlStatement);
        cursor = getReadableDatabase().rawQuery(sqlStatement, null);
        while(cursor.moveToNext()) {
            int imageID = cursor.getInt(cursor.getColumnIndexOrThrow(ImageTable._ID));
            String filename = cursor.getString(cursor.getColumnIndexOrThrow(ImageTable.COLUMN_NAME_FILENAME));
            long receivedate = cursor.getLong(cursor.getColumnIndexOrThrow(ImageTable.COLUMN_NAME_RECEIVEDATE));
            long filesize = cursor.getLong(cursor.getColumnIndexOrThrow(ImageTable.COLUMN_NAME_FILESIZE));
            String sender = cursor.getString(cursor.getColumnIndexOrThrow(ImageTable.COLUMN_NAME_SENDER));
            String sendernumber = cursor.getString(cursor.getColumnIndexOrThrow(ImageTable.COLUMN_NAME_SENDERNUMBER));
            String text = cursor.getString(cursor.getColumnIndexOrThrow(ImageTable.COLUMN_NAME_TEXT));
            int newimage = cursor.getInt(cursor.getColumnIndexOrThrow(ImageTable.COLUMN_NAME_NEW));
            int forevernew = cursor.getInt(cursor.getColumnIndexOrThrow(ImageTable.COLUMN_NAME_FOREVERNEW));
            int invisible = cursor.getInt(cursor.getColumnIndexOrThrow(ImageTable.COLUMN_NAME_INVISIBLE));

            Log.d("getAllImages2BeDispl", "Listentry: " + imageID + filename + receivedate + filesize + sender + sendernumber + text + newimage + forevernew + invisible);
            imageList.add(new PVimage(imageID, filename, receivedate, filesize, sender, sendernumber, text, newimage, forevernew, invisible));
        }
        cursor.close();

        /*while (cursor.moveToNext()) {
            Log.d("getAllImages2BeDispl", "count(*): " + cursor.getInt(cursor.getColumnIndexOrThrow("count(*)")));
        }*/


        return imageList;
    }



    public void setShownTag(int value) {

    }

    public void updateWhatsAppData() {

    }





    //Database stuff is missing here

                        /*SQLiteDatabase db = SQLiteDatabase.openDatabase("/sdcard/msgstore.db", null, 0);
                //Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
                Cursor c = db.rawQuery("SELECT * FROM messages", null);

                if (c.moveToFirst()) {
                    while ( !c.isAfterLast() ) {
                        Toast.makeText(getApplicationContext(), "Table Name=> "+c.getString(0), Toast.LENGTH_LONG).show();
                        c.moveToNext();
                    }
                }*/

    

}
