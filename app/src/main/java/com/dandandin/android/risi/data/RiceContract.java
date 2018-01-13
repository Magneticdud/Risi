package com.dandandin.android.risi.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class RiceContract {

    //To prevent someone from accidentally instantiating the contract class, give it an empty constructor.
    private RiceContract(){}

    public static final String CONTENT_AUTHORITY = "com.dandandin.android.risi";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_RICE = "rices";

    public static final class RiceEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_RICE);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of risi.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RICE;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single rice.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RICE;

        //nome tabella
        public static final String TABLE_NAME = "rices";
        public final static String _ID = BaseColumns._ID;
        //nome commerciale
        public static final String COLUMN_RICE_NAME = "name";
        //prezzo di vendita in centesimi (era: pet weight)
        public static final String COLUMN_PRICE = "price";
        //tipo di riso (breed)
        public static final String COLUMN_BREED = "breed";
        //tipo di confezione
        public static final String COLUMN_PACKAGING = "packaging";
        //descrizione
        public static final String COLUMN_DESCRIPTION = "description";

        /**
         * Possible values for the style of the headphone.
         */
        public static final int PACK_VACUUM = 0;
        public static final int PACK_CARTON = 1;
        public static final int PACK_VACUUMCARTON = 2;
        public static final int PACK_BAG = 3;

        /**
        * Returns whether or not the given packaging is valid
         */
        public static boolean isValidPackaging(int pack) {
            if (pack == PACK_VACUUM || pack == PACK_CARTON || pack == PACK_VACUUMCARTON || pack == PACK_BAG) {
                return true;
            }
            return false;
        }

    }
}