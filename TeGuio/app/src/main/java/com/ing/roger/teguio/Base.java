package com.ing.roger.teguio;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import org.osmdroid.util.GeoPoint;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Roger on 03/05/2017.
 */

public class Base extends SQLiteOpenHelper {
    private static String DB_PATH = "/data/data/com.ing.roger.teguio/databases/";
    private static String DB_NAME = "base";
    private SQLiteDatabase myDataBase;
    private final Context myContext;

    public Base(Context contexto, String nombre, SQLiteDatabase.CursorFactory factory,
                int version) {
        super(contexto, nombre, factory, version);
        this.myContext = contexto;
        try {
            createDataBase();
            openDataBase();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void openDataBase() throws SQLException {

        // Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if (dbExist) {
            // Si existe, no haemos nada!
        } else {
            // Llamando a este metodo se crea la base de datos vacia en la ruta
            // por defecto del sistema de nuestra aplicación por lo que
            // podremos sobreescribirla con nuestra base de datos.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copiando database");
            }
        }
    }
    private boolean checkDataBase() {

        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e) {
            // Base de datos no creada todavia
        }

        if (checkDB != null) {

            checkDB.close();
        }

        return checkDB != null ? true : false;

    }
    private void copyDataBase() throws IOException {

        OutputStream databaseOutputStream = new FileOutputStream("" + DB_PATH + DB_NAME);
        InputStream databaseInputStream;

        byte[] buffer = new byte[1024];
        int length;

        databaseInputStream = myContext.getAssets().open("base.db");
        while ((length = databaseInputStream.read(buffer)) > 0) {
            databaseOutputStream.write(buffer);
        }

        databaseInputStream.close();
        databaseOutputStream.flush();
        databaseOutputStream.close();
    }


    //metodos
    public String prueba() {
        Cursor c = myDataBase.rawQuery("select rutLatitud,rutLongitud from rutas where rutId=17652", null);
        if (c.moveToFirst()) {
            return c.getString(0);
        }
        return "No se encontaron datos";
    }

    public ArrayList<Posicion> getRutaLinea(int MIN1MAX136) {
        if(MIN1MAX136<=0|MIN1MAX136>136){
            MIN1MAX136=1;
        }
        ArrayList<Posicion> ArrayList = new ArrayList<Posicion>();
        Cursor c;
        c=myDataBase.rawQuery("select rutLatitud,rutLongitud from rutas where linId="+String.valueOf(MIN1MAX136),null);
        if(c.moveToFirst()){
            do{
                double lat,lon;
                lat=Double.parseDouble(c.getString(0));
                lon=Double.parseDouble(c.getString(1));
                ArrayList.add(new Posicion(lat,lon));
            }while(c.moveToNext());
        }
        return ArrayList;
    }
    public Linea getLinea(int MIN1MAX136){
        String nombre="",imagen="",colorl="";
        Cursor c = myDataBase.rawQuery("select linnombre,linImagen,linColor from lineasBuses where linId=" + String.valueOf(MIN1MAX136), null);
            if (c.moveToFirst()) {
                nombre = c.getString(0);
                imagen = c.getString(1);
                colorl=c.getString(2);

            }

        Linea linea=new Linea(MIN1MAX136,nombre,imagen,colorl);
        linea.setRuta(getRutaLinea(MIN1MAX136));
        return linea;
    }
    public String getNombreLinea(int MIN1MAX136){
        if(MIN1MAX136==1){
            MIN1MAX136=2;
        }
        Cursor c = myDataBase.rawQuery("select linnombre from lineasBuses where linId=" + String.valueOf(MIN1MAX136), null);
        if (c.moveToFirst()) {
            return c.getString(0);
        }
        return "";
    }

    public Linea getLinea(String nombre){
        String imagen="",color="";
        int id=0;
        Cursor c = myDataBase.rawQuery("select linImagen,linColor,linId from lineasBuses where linnombre='" +nombre+"'", null);
        if (c.moveToFirst()) {
            imagen = c.getString(0);
            color=c.getString(1);
            id=Integer.parseInt(c.getString(2));
        }
        Linea linea=new Linea(id,nombre,imagen,color);
        linea.setRuta(getRutaLinea(id));
        return linea;
    }

    ArrayList<String> Listar(){
        ArrayList<String> lista=new ArrayList<String>();
        Cursor c;
        c=myDataBase.rawQuery("select linnombre from lineasBuses",null);
        if(c.moveToFirst()){
            int cont=0;
            do{
               if(cont<10){
                   lista.add(c.getString(0));
                   cont++;
               }

            }while(c.moveToNext());
        }
        return lista;
    }

    ArrayList<Posicion> listar(int id){
        ArrayList<Posicion> lista=new ArrayList<Posicion>();
        Cursor c=myDataBase.rawQuery("select rutLatitud,rutLongitud from rutas where linId="+id, null);
        if(c.moveToFirst()){
            do{
                lista.add(new Posicion(id,doble(c.getString(0)),doble(c.getString(1))));
            }while(c.moveToNext());
        }
        return lista;
    }

    ArrayList<Posicion> lineasCercanas(GeoPoint ll){
        double ola=ll.getLatitude();//.latitude;
        double olo=ll.getLongitude();//.longitude;
        ArrayList<Posicion> lista=new ArrayList<Posicion>();
        ArrayList<Posicion> posiciones;
        for(int i=2;i<=20;i++){
            posiciones=listar(i);
            double dla=posiciones.get(0).lat;
            double dlo=posiciones.get(0).lon;
            double d=Math.sqrt(Math.pow(Math.abs(ola-dla),2)+Math.pow(Math.abs(olo-dlo),2));
            lista.add(posiciones.get(0));
            for(int k=1;k<posiciones.size();k++){
                double dlat=posiciones.get(k).lat;
                double dlon=posiciones.get(k).lon;
                double dist=Math.sqrt(Math.pow(Math.abs(ola-dlat),2)+Math.pow(Math.abs(olo-dlon),2));
                if(d>dist){
                    d=dist;
                    lista.set(lista.size()-1,posiciones.get(k));
                }
            }
        }
        return lista;
    }
    int entero(String cad){
        return Integer.parseInt(cad);
    }
    double doble(String cad){
        return Double.parseDouble(cad);
    }
}
