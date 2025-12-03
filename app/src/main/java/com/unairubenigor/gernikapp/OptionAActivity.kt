package com.unairubenigor.gernikapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class OptionAActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option_a)

        // Buscamos el fragmento del mapa que ahora está dentro del LinearLayout
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // 1. Configuración Visual (Satélite + Controles)
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true

        // 2. Coordenadas de Gernika
        val mural      = LatLng(43.315528, -2.680083)
        val museoPaz   = LatLng(43.315056, -2.678394)
        val iglesia    = LatLng(43.314503, -2.679192)
        val pasileku   = LatLng(43.313869, -2.678731)
        val arbol      = LatLng(43.313361, -2.680011)
        val astra      = LatLng(43.312436, -2.676803)

        // 3. Añadir Marcadores
        mMap.addMarker(MarkerOptions().position(mural).title("Gernikako murala"))
        mMap.addMarker(MarkerOptions().position(museoPaz).title("Bakearen museoa"))
        mMap.addMarker(MarkerOptions().position(iglesia).title("Andra Mari Eliza"))
        mMap.addMarker(MarkerOptions().position(pasileku).title("Pasileku Plaza"))
        mMap.addMarker(MarkerOptions().position(arbol).title("Gernikako Arbola"))
        mMap.addMarker(MarkerOptions().position(astra).title("Astra"))

        // 4. Enfocar Gernika al iniciar
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mural, 16f))
    }
}