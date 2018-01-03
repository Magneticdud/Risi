package com.dandandin.android.risi.data;

import android.provider.BaseColumns;

public final class RiceContract {

    //To prevent someone from accidentally instantiating the contract class, give it an empty constructor.
    private RiceContract(){}

    public static final class RiceEntry implements BaseColumns {

        //nome tabella
        public static final String TABLE_NAME = "rices";
        public final static String _ID = BaseColumns._ID;
        //nome commerciale
        public static final String COLUMN_RICE_NAME = "name";
        //prezzo di vendita (era: pet weight)
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

    }
}