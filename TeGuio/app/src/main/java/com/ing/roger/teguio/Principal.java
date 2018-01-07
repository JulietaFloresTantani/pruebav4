package com.ing.roger.teguio;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.PathOverlay;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.ing.roger.teguio.R.styleable.NavigationView;

public class Principal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Base base;
    List<String> mLineas = new ArrayList<String>();
    public static final GeoPoint STC = new GeoPoint(-17.778204,-63.173159);
    private MapController mc;
    MapView mapView;
    private LocationManager locationManager;
    Context esto=this;
    private PathOverlay po;
    TextView nk;
    ImageView ft;
    DrawerLayout drawer;
    private ProgressDialog dialog;

    //Menu_navigation
    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    Toolbar toolbar;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
         mapView = (MapView) findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        // mapView.setUseDataConnection(true);


        mc = (MapController) mapView.getController();
        mc.setZoom(13);
        mc.setCenter(STC);
        base = new Base(this, "database", null, 1);
        RellenarLineas();


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navHeaderView= navigationView.getHeaderView(0);
        nk=(TextView)navHeaderView.findViewById(R.id.t_linea);
        ft=(ImageView)navHeaderView.findViewById(R.id.Image_linea);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                ShowAlertDialogWithListview();
               // drawer.openDrawer(GravityCompat.START);
            }
        });



        //Menu_navigation
        mTitle = mDrawerTitle = getTitle();
        mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        setupToolbar();

        DataModel[] drawerItem = new DataModel[3];

        drawerItem[0] = new DataModel(R.drawable.img_1, "Connect");
        drawerItem[1] = new DataModel(R.drawable.img_2, "Fixtures");
        drawerItem[2] = new DataModel(R.drawable.img_3, "Table");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.list_view_item_row, drawerItem);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        setupDrawerToggle();

    }


    //Menu_navigation
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

    }

    private void selectItem(int position) {

       Fragment fragment = null;
        //android.app.Fragment fragment=null;
        switch (position) {
            case 0:
                fragment = new ConnectFragment();
                break;
            case 1:
                fragment = new FixturesFragment();
                break;
            case 2:
                fragment = new TableFragment();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(mNavigationDrawerItemTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    void setupToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    void setupDrawerToggle(){
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle.syncState();
    }


    //___________________

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            dialog = new ProgressDialog(Principal.this);
            dialog.setTitle("Dibujando...");
            dialog.setMessage("Porfavor espere...");
            dialog.setIndeterminate(true);
            dialog.show();
            runOnUiThread(new Runnable(){
                public void run(){
                   // for (int i = 1; i < 4; i++) {
                    for (int i = 1; i < mLineas.size() +1; i++) {
                        Linea linea = base.getLinea(i);
                        // showm(linea.getNombre());
                        // mapView.getOverlays().clear();
                        dibujar(linea);
                    }
                    dialog.dismiss();
                }
            });


           // new view_all().execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } /*else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void ShowAlertDialogWithListview()
    {

        //Create sequence of items
        final CharSequence[] Llineas = mLineas.toArray(new String[mLineas.size()]);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Lineas");
        dialogBuilder.setItems(Llineas, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
               // String selectedText = Llineas[item].toString();
                Linea linea = base.getLinea(item+1);
                mapView.getOverlays().clear();
                dibujar(linea);
                show("colo "+linea.getColor());
                Toast.makeText(Principal.this, String.valueOf(item), Toast.LENGTH_LONG).show();
            }
        });
        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();
    }
    public void initPathOverlay(Color c){
        /*po = new PathOverlay(0, this);
        Paint p = new Paint();
        p.setColor(c);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(5);
        po.setPaint(p);*/
    }

    public void RellenarLineas() {
        mLineas=base.Listar();
    }
    void dibujar(Linea linea) {
       // initPathOverlay(Color.RED);
        ArrayList<Posicion> ruta = linea.getRuta();
        String color= linea.getColor();
        GeoPoint o, d = null;
        for (int i = 0; i < ruta.size() - 1; i++) {
            Posicion a, b = ruta.get(i + 1);
            a = ruta.get(i);
            o = new GeoPoint(a.getLat(), a.getLon());
            d = new GeoPoint(b.getLat(), b.getLon());
            if (i == 0) {
                addMarcadorParada(o,linea.getNombre(),linea.getImagen(), false);
            }
            if (i == ruta.size() - 2) {
                addMarcadorParada(d, linea.getNombre(),linea.getImagen(),false);
            }

           dibujarLinea(a.getLat(), a.getLon(), b.getLat(), b.getLon(),color);
        }
    }
    void addMarcadorParada(GeoPoint ll,String ttl,String ig, boolean limpiar) {
        if (limpiar) {
           // mapView.getOverlays().clear();
        }
        addMarker(ll,ttl,ig);
       // moverCamara(ll);
    }

    public void addMarker(GeoPoint center, final String title, final String img){
        Marker marker = new Marker(mapView);
        marker.setPosition(center);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(getResources().getDrawable(R.drawable.ic_directions_bus_black_24dp));
        marker.setTitle(title);
        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener(){

            @Override
            public boolean onMarkerClick(Marker m, MapView arg1) {
                Log.i("Script", "onMarkerClick()");
                m.showInfoWindow();
                show(title);
                cargar_info_linea(title,img);
                return true;
            }

        });

       // mapView.getOverlays().clear();
        mapView.getOverlays().add(marker);
        //mapView.invalidate();
    }
    public void cargar_info_linea(String nombre,String image){
        nk.setText(nombre);
        try {
            R.drawable ourRID = new R.drawable();
            Field photoNameField = ourRID.getClass().getField(image);
            ft.setImageResource(photoNameField.getInt(ourRID));
        }catch (Exception e){
            show("no se encontro imagen");
        }


        drawer.openDrawer(GravityCompat.START);
    }
   /* public void drawRoute(GeoPoint start, GeoPoint end){
        RoadManager roadManager = new OSRMRoadManager();
        ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
        points.add(start);
        points.add(end);
        Road road = roadManager.getRoad(points);
        final Polyline roadOverlay = RoadManager.buildRoadOverlay(road, Principal.this);

        runOnUiThread(new Runnable(){
            public void run(){
                mapView.getOverlays().add(roadOverlay);
            }
        });
    }*/
    public void dibujarLinea(double lato, double lono, double latd, double lond,String lcolor) {
        try {
            GeoPoint ori = new GeoPoint(lato, lono);
            GeoPoint dest = new GeoPoint(latd, lond);

           // Polyline line = mapView.addPolyline(new PolylineOptions().add(ori, dest).width(5).color(Color.GREEN));
            RoadManager roadManager = new OSRMRoadManager();
            ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
            points.add(ori);
            points.add(dest);
            Road road = roadManager.getRoad(points);
            final Polyline roadOverlay = RoadManager.buildRoadOverlay(road, Principal.this);
           if(!lcolor.equals("")){
                int color = Color.parseColor(lcolor);
                roadOverlay.setColor(color);
               // show("si "+lcolor);
            }else {
                roadOverlay.setColor(Color.RED);
                //show("no "+lcolor);
            }
           // roadOverlay.setColor(Color.RED);

            runOnUiThread(new Runnable(){
                public void run(){
                    mapView.getOverlays().add(roadOverlay);
                }
            });
        } catch (Exception e) {
            show("Seleccione un codigo de destino");
            return;
        }
    }
    void show(String cad) {
        Toast.makeText(getApplicationContext(), cad, Toast.LENGTH_SHORT).show();
    }

    public class view_all extends AsyncTask<ArrayList<String>, Void, ArrayList<String>> {

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(Principal.this);
            dialog.setTitle("Dibujando...");
            dialog.setMessage("Porfavor espere...");
            dialog.setIndeterminate(true);
            dialog.show();
        }

        @Override
        protected ArrayList<String> doInBackground(ArrayList<String>... params) {
            for (int i = 1; i < 4; i++) {
              //  for (int i = 1; i < mLineas.size() +1; i++) {
                Linea linea = base.getLinea(i);
               // showm(linea.getNombre());
               // mapView.getOverlays().clear();
                dibujar(linea);
            }
            return null;
        }
        protected void onPostExecute(ArrayList<String> result) {
            dialog.dismiss();

        }

    }
}
