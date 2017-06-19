package net.djeraservices.depanv2.depanv2.bdd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.djeraservices.depanv2.depanv2.bdd.model.Reclamation;

/**
 * Created by BW.KOFFI on 17/06/2017.
 */

public class LocaleBDD extends SQLiteOpenHelper {

    public static final String BDD_NAME = "local.db";
    public static final int BDD_VERSION = 1;

    public LocaleBDD(Context context) {
        super(context, BDD_NAME, null, BDD_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Reclamation.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion > oldVersion){
            db.execSQL("DROP TABLE "+Reclamation.TABLE_NAME +";");
        }
    }
}
