package com.ing.roger.teguio;

import android.support.annotation.NonNull;

/**
 * Created by Roger on 03/05/2017.
 */

public class Posicion implements Comparable<Posicion> {
    double lat,lon;
    int NroLinea;

    public Posicion(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }
    public Posicion(int linea,double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
        this.NroLinea=linea;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
    @Override
    public int compareTo(@NonNull Posicion o) {
        return 0;
    }
}
