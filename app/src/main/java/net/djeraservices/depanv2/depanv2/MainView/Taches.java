package net.djeraservices.depanv2.depanv2.MainView;

/**
 * Created by BW.KOFFI on 21/02/2017.
 */

public class Taches {
    private int id = 0;
    private int priorite;
    private String numero;
    private String panne;
    private String longitude;
    private String latitude;

    private String dateaffectation;

    public final int PRIORITE_TRES_URGENT = 1;
    public final int PRIORITE_URGENT = 2;
    public final int PRIORITE_NORMALE = 3;

    public Taches(int id, int priorite, String numero, String panne, String longitude, String latitude, String dateaffectation) {
        this.id = id;
        this.priorite = priorite;
        this.numero = numero;
        this.panne = panne;
        this.longitude = longitude;
        this.latitude = latitude;
        this.dateaffectation = dateaffectation;
    }

    public Taches(){
        id = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPriorite() {
        return priorite;
    }

    public void setPriorite(int priorite) {
        this.priorite = priorite;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getPanne() {
        return panne;
    }

    public void setPanne(String panne) {
        this.panne = panne;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getDateaffectation() {
        return dateaffectation;
    }

    public void setDateaffectation(String dateaffectation) {
        this.dateaffectation = dateaffectation;
    }
}
