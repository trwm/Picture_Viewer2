package name.weiskirchner.picture_viewer2;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by michael on 09.07.17.
 */

public class WADatabaseController {

    SQLiteDatabase whatsAppDB;
    String whatsAppDB_path;

    public WADatabaseController(String waDBPath) {
        this.whatsAppDB_path=waDBPath;
        Log.d("WADatabaseController", "New WADatabaseController");

        try {

            /*whatsAppDB = SQLiteDatabase.openDatabase(waDBPath, null, SQLiteDatabase.ENABLE_WRITE_AHEAD_LOGGING);
            Cursor cursor = whatsAppDB.rawQuery("SELECT * from messages", null);
            while (cursor.moveToNext()) {
                Log.d("WADatabaseController", "ID: " + cursor.getLong(cursor.getColumnIndexOrThrow("_id")) + " Size: " + cursor.getLong(cursor.getColumnIndexOrThrow("media_size")) + " Date: " + cursor.getLong(cursor.getColumnIndexOrThrow("received_timestamp")));
            }
            cursor.close();
            whatsAppDB.close();*/
        }catch (Exception e) {
            Log.d("WADatabaseController", "Exception:" + e.getMessage());
        }

    }


    public ArrayList<WAImage> getAllWAMessagesImages() {
        ArrayList<WAImage> waImages = new ArrayList<>();

        try {
            whatsAppDB = SQLiteDatabase.openDatabase(whatsAppDB_path, null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.ENABLE_WRITE_AHEAD_LOGGING);
            Cursor cursor = whatsAppDB.rawQuery("SELECT key_remote_jid, media_size, received_timestamp FROM messages WHERE key_from_me=0 AND media_wa_type=1", null);

            while(cursor.moveToNext()) {
                long receivedate = cursor.getLong(cursor.getColumnIndexOrThrow("received_timestamp"));
                long filesize = cursor.getLong(cursor.getColumnIndexOrThrow("media_size"));
                String sender = cursor.getString(cursor.getColumnIndexOrThrow("key_remote_jid"));

                Pattern p = Pattern.compile("(.*)@.*");
                Matcher m = p.matcher(sender);
                String sendernumber;
                if (m.find()) {
                    sendernumber = m.group(1);
                }else{
                    sendernumber = sender;
                }
                waImages.add(new WAImage(receivedate, filesize, sendernumber));

                Log.d("getAllWAMessagesImages", "Date: " + receivedate + " Size: " + filesize + " Sender " + sendernumber);
            }
            cursor.close();
            whatsAppDB.close();
        }catch(Exception e) {
                Log.d("getAllWAMessagesImages", "Exception:" + e.getMessage());
        }
        return waImages;
    }

    public PVimage getImageDetails(PVimage pVimage) {

        try {
            Log.d("getImageDetails", "Before DB open");
            whatsAppDB = SQLiteDatabase.openDatabase(whatsAppDB_path, null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.ENABLE_WRITE_AHEAD_LOGGING);
            if(whatsAppDB.isDatabaseIntegrityOk()) {
                Log.d("getImageDetails", "Database integritiy OK");
                String query = "SELECT key_remote_jid, media_size, received_timestamp FROM messages WHERE key_from_me=0 AND media_wa_type=1 AND media_size=" + pVimage.getFilesize();
                Log.d("getImageDetails", "SQL: " + query);
                Cursor cursor = whatsAppDB.rawQuery(query, null);
                Log.d("getImageDetails", "Cursor size: " + cursor.getCount());
                if (cursor.getCount() > 1) {
                    //more two entries with the same filesize => check date
                    String query_date = "SELECT key_remote_jid, media_size, received_timestamp FROM messages WHERE key_from_me=0 AND media_wa_type=1 AND media_size=" + pVimage.getFilesize() + " AND received_timestamp>=" + (pVimage.getReceivedate() - 180000) + " AND received_timestamp<=" + (pVimage.getReceivedate() + 180000);
                    Log.d("getImageDetails", "SQL: " + query_date);
                    cursor = whatsAppDB.rawQuery(query_date, null);
                    Log.d("getImageDetails", "Cursor size: " + cursor.getCount());
                }

                while (cursor.moveToNext()) {
                    long receivedate = cursor.getLong(cursor.getColumnIndexOrThrow("received_timestamp"));
                    long filesize = cursor.getLong(cursor.getColumnIndexOrThrow("media_size"));
                    String sender = cursor.getString(cursor.getColumnIndexOrThrow("key_remote_jid"));

                    Pattern p = Pattern.compile("(.*)@.*");
                    Matcher m = p.matcher(sender);
                    String sendernumber;
                    Log.d("getImageDetails", "SQL: " + query);
                    if (m.find()) {
                        sendernumber = m.group(1);
                    } else {
                        sendernumber = sender;
                    }
                    pVimage.setSendernumber(sendernumber);

                    Log.d("getImageDetails", "Path: " + pVimage.getFilename() + " Date: " + receivedate + " Size: " + filesize + " Sender " + sendernumber);

                }

                cursor.close();
            }else{
                Log.d("getImageDetails", "Database integritiy not OK");
            }
            Log.d("getImageDetails", "Closing WA Database");
            whatsAppDB.close();
        }catch(Exception e) {
            Log.d("getImageDetails", "Exception:" + e.getMessage());
        }

        return pVimage;
    }

}
