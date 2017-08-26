package name.weiskirchner.picture_viewer2;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by michael on 17.06.17.
 */

public class PVimageController {

    private List<PVimage> PVimages = new ArrayList<>();
    private List<PVimage> PVimagesFromDB = new ArrayList<>();
    private DatabaseController database;
    private WADatabaseController waDatabase;
    private String imageDirPath;
    private Context context;

    public PVimageController(DatabaseController database, WADatabaseController waDatabase, Context context) {
        this.database = database;
        this.waDatabase = waDatabase;
        this.context = context;

    }

    public void initializeImageList (String imageDirPath) {
        this.imageDirPath = imageDirPath;
        //updateImageList(); //will be done by task 3 after start
    }

    public boolean updateImageDatabase() {
        boolean newitem = false;
        //List<PVimage> newitem_list = new ArrayList<>();
        PVimage newitem_image;
        boolean found = false;
        File imageDir;
        PVimagesFromDB = database.getAllImages();
        //Log.d("updateImageDatabase", "Images: " + PVimages);
        RootController mRootController =  new RootController();
        mRootController.execute();

        //Compare directory with database and add new items to database
        imageDir = new File(imageDirPath);
        if (imageDir.isDirectory()) {
            for (File f : imageDir.listFiles()) {
                if (f.isFile()) {
                    //Toast.makeText(getApplicationContext(), f.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    found = false;
                    for(PVimage image : PVimagesFromDB) {
                        //Log.d("updateImageDatabase", "Image:" + image.getFilename());
                        //Log.d("updateImageList", "File:" + f.getAbsolutePath());
                        if(image.getFilename().equals(f.getAbsolutePath())) {
                            found = true;
                            //Log.d("updateImageList", "Found a match!");
                        }
                    }
                    if(!found) {
                        newitem_image = new PVimage(0, f.getAbsolutePath(), f.lastModified(), f.length(), null, null, null, 1, 0, 0);
                        newitem_image = waDatabase.getImageDetails(newitem_image);
                        String name = getContactDisplayNameByNumber(newitem_image.getSendernumber());
                        if(name != "?") {
                            newitem_image.setSender(name);
                        }else{
                            newitem_image.setSender("Unbekannt");
                        }
                        database.addImageFile(newitem_image);
                        Log.d("updateImageDatabase", "New Image for DB:" + newitem_image.getFilename());
                        PVimages.add(newitem_image);
                        //PVimages.add(new PVimage(1, f.getAbsolutePath(), new Date(f.lastModified()), "testsender", "testtext", true, true));
                        //newitem_list.add(newitem_image);
                        newitem=true;
                    }
                }
            }
        }else{
            return newitem;
        }

        return newitem;
    }

    public void updateForevernew(int pVimageID, int dBimageID, boolean forevernew){
        if(forevernew) {
            PVimages.get(pVimageID).setForevernew(1);
        }else{
            PVimages.get(pVimageID).setForevernew(0);
        }
        database.updateForevernew(dBimageID, forevernew);
    }

    public void updateSendernumber(int pVimageID, int dBimageID, String sendernumber){
        if(sendernumber!="" && sendernumber!=null) {
            PVimages.get(pVimageID).setSendernumber(sendernumber);
            database.updateSendernumber(dBimageID, sendernumber);
        }

    }

    public void updateInvisible(int pVimageID, int dBimageID, boolean invisible){
        if(invisible) {
            PVimages.get(pVimageID).setInvisible(1);
        }else{
            PVimages.get(pVimageID).setInvisible(0);
        }
        database.updateInvisible(dBimageID, invisible);
    }

    public void updateImageList() {
        int pvIndex = 0;
        List<Integer> removeList = new ArrayList<>();
        PVimages = database.getAllImages2BeDisplayed();

        for(PVimage pvImage : PVimages) {
            if(pvImage.getSendernumber()==null) {
                pvImage = waDatabase.getImageDetails(pvImage);
                if(pvImage.getSendernumber()!=null) {
                    updateSendernumber(pvIndex, pvImage.getId(), pvImage.getSendernumber());
                }
                Log.d("updateImageList", "Sendernumber upate:" + pvImage.getFilename());
            }
            String name = getContactDisplayNameByNumber(pvImage.getSendernumber());
            if(name != "?") {
                pvImage.setSender(name);
            }else{
                pvImage.setSender("Unbekannt");
            }
            File file = new File(pvImage.getFilename());
            if(!file.exists()) {
                updateInvisible(pvIndex, pvImage.getId(),true);
                removeList.add(pvIndex);
                Log.d("updateImageList", "Removed image:" + pvImage.getFilename());

            }
            pvIndex++;
        }

        //clean up
        for(int removeItem : removeList) {
            PVimages.remove(removeItem);
        }
    }

    public void updateWhatsAppData(List<PVimage> newitem_list) {
        ArrayList<WAImage> waImages = new ArrayList<>();
        RootController mRootController =  new RootController();
        int pvIndex = 0;

        mRootController.execute();





        //waImages = waDatabase.getAllWAMessagesImages();

        /*for(PVimage pvImage : PVimages) {
            if(pvImage.getSendernumber() == null) {
                for(WAImage waImage : waImages) {
                    if(pvImage.getFilesize() == waImage.getFilesize() && (pvImage.getReceivedate() <= (waImage.getReceivedate()+10000) && pvImage.getReceivedate() >= (waImage.getReceivedate()-10000))) {
                        pvImage.setSendernumber(waImage.getSendernumber());
                        String name = getContactDisplayNameByNumber(waImage.getSendernumber());
                        if(name != "?") {
                            pvImage.setSender(name);
                        }
                        Log.d("updateWhatsAppData", "WANumber: " + waImage.getSendernumber() + " Name: " + name);
                        PVimages.set(pvIndex, pvImage);
                        //add number to DB is missing here
                    }
                }

            }
            pvIndex++;
        }*/

    }

    public PVimage getPVimage(int position) {
        return PVimages.get(position-1);
    }

    public int getSize() {
        //Log.d("ImageListSize", "Size:" + PVimages.size());
        return PVimages.size();
    }

    public String getContactDisplayNameByNumber(String number) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = "?";

        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME)).split(" ")[0];
                //String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return name;
    }

}
