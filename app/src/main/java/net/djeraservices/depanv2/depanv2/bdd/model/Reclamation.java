package net.djeraservices.depanv2.depanv2.bdd.model;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import net.djeraservices.depanv2.depanv2.BootApplication;
import net.djeraservices.depanv2.depanv2.MainView.Taches;
import net.djeraservices.depanv2.depanv2.bdd.LocaleBDD;

/**
 * Created by BW.KOFFI on 17/06/2017.
 */

public class Reclamation extends Modele {
    public static final String TABLE_NAME = "reclamation";
    public static final String COL_ID = "id";
    public static final String COL_PRIORITE = "priorite";
    public static final String COL_NUMERO = "numero";
    public static final String COL_PANNE = "panne";
    public static final String COL_LONGITUDE = "longitude";
    public static final String COL_LATITUDE = "latitude";
    public static final String COL_DATE_AFFECTATION = "dateaffectation";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" (" +
            COL_ID + " INTEGER PRIMARY KEY," +
            COL_PRIORITE + " INTEGER NOT NULL," +
            COL_NUMERO + " TEXT NOT NULL," +
            COL_PANNE + " TEXT NOT NULL," +
            COL_LONGITUDE + " TEXT NOT NULL," +
            COL_LATITUDE + " TEXT NOT NULL," +
            COL_DATE_AFFECTATION + " TEXT);";

    public boolean insert(Taches taches){
        long flag = 0;

        //on ouvre la base en écriture
        SQLiteDatabase bdd = BootApplication.getMaBaseSQLite().getWritableDatabase();

        //Création d'un contentValues (pour un mappage clé/valeur)
        ContentValues values = new ContentValues();

        if(taches.getDateaffectation() != null){
            values.put(COL_ID, taches.getId());
            values.put(COL_PRIORITE, taches.getPriorite());
            values.put(COL_NUMERO, taches.getNumero());
            values.put(COL_PANNE, taches.getPanne());
            values.put(COL_LONGITUDE, taches.getLongitude());
            values.put(COL_LATITUDE, taches.getLatitude());
            values.put(COL_DATE_AFFECTATION, taches.getDateaffectation());
        }

        flag =  bdd.insert(TABLE_NAME,null,values);
        bdd.close();;

        return flag > 0 ;
    }
}