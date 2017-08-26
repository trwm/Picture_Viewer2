package name.weiskirchner.picture_viewer2;

import android.provider.BaseColumns;

/**
 * Created by michael on 25.05.17.
 */

public final class DatabaseContract {


    private DatabaseContract() {

    }

    /* Inner class that defines the table contents */
    public static class ImageTable implements BaseColumns {
        public static final String TABLE_NAME = "images";
        public static final String COLUMN_NAME_FILENAME = "filename";
        public static final String COLUMN_NAME_RECEIVEDATE = "receivedate";
        public static final String COLUMN_NAME_FILESIZE = "filesize";
        public static final String COLUMN_NAME_SENDER = "sender";
        public static final String COLUMN_NAME_SENDERNUMBER = "sendernumber";
        public static final String COLUMN_NAME_TEXT = "text";
        public static final String COLUMN_NAME_NEW = "new";
        public static final String COLUMN_NAME_FOREVERNEW = "forevernew";
        public static final String COLUMN_NAME_INVISIBLE = "invisible";
    }

}


//ID, filename, receivedate, sender, text, new, shown, invisible