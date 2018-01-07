package com.ing.roger.teguio;

import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by Roger on 03/05/2017.
 */

public class Linea implements Comparable<Linea> {
    String nombre,imagen,color;
    int id;
    ArrayList<Posicion> ruta;


    public Linea(int id,String nombre) {
        this.nombre = nombre;
        this.id = id;

    }

    public Linea( int id,String nombre, String imagen,String lcolor) {
        this.nombre = nombre;
        this.imagen = imagen;
        this.id = id;
        this.color=lcolor;

    }
    public String getImagen()
    {
        return imagen;
    }
    public void setImagen(String im){
        this.imagen=im;
    }
    public String getColor(){
        return color;
    }
    public void setColor(String c){
        this.color=c;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Posicion> getRuta() {
        return ruta;
    }

    public void setRuta(ArrayList<Posicion> ruta) {
        this.ruta = ruta;
    }

    @Override
    public int compareTo(@NonNull Linea another) {
        if(another.getId()<getId()){
            return 1;
        }
        if(another.getId()>getId()){
            return -1;
        }
        return 0;
    }
}
