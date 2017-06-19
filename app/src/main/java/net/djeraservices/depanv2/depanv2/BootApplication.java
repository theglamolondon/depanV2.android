package net.djeraservices.depanv2.depanv2;

import android.app.Application;
import android.content.Intent;

import com.facebook.stetho.Stetho;

import net.djeraservices.depanv2.depanv2.bdd.LocaleBDD;
import net.djeraservices.depanv2.depanv2.location.LocationService;

/**
 * Created by BW.KOFFI on 17/06/2017.
 */

public class BootApplication extends Application {

    private static LocaleBDD maBaseSQLite;

    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);
        maBaseSQLite = new LocaleBDD(this);

        //Démarrage du service de géo
        Intent intent = new Intent(BootApplication.this, LocationService.class);
        startService(intent);
    }

    public static LocaleBDD getMaBaseSQLite(){
        return maBaseSQLite;
    }
}
