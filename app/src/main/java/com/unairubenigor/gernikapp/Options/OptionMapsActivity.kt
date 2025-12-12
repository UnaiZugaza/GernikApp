package com.unairubenigor.gernikapp.Options

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.unairubenigor.gernikapp.Actividades_Geltokiak.AndraMariActivity
import com.unairubenigor.gernikapp.Actividades_Geltokiak.ArbolaActivity
import com.unairubenigor.gernikapp.Actividades_Geltokiak.AstraActivity
import com.unairubenigor.gernikapp.Actividades_Geltokiak.GernikakoMuralaOngiEtorriActivity
import com.unairubenigor.gernikapp.Actividades_Geltokiak.PasilekuActivity
import com.unairubenigor.gernikapp.R

class OptionMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option_maps)

        // --- 1. HEADER ---
        val headerView = findViewById<View>(R.id.header_layout)
        val headerTitle = headerView.findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "MAPA"

        // --- 2. FOOTER ---
        val footerView = findViewById<View>(R.id.footer_layout)
        val btnBack = footerView.findViewById<ImageButton>(R.id.buttonBack)
        val btnContinue = footerView.findViewById<ImageButton>(R.id.buttonContinue)

        btnBack.visibility = View.VISIBLE
        btnBack.setOnClickListener { finish() }

        btnContinue.visibility = View.INVISIBLE

        // --- 3. INICIALIZAR MAPA ---
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // --- A. Configuración Visual ---
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true

        // --- B. Coordenadas ---
        val mural      = LatLng(43.315528, -2.680083)
        val museoPaz   = LatLng(43.315056, -2.678394)
        val iglesia    = LatLng(43.314503, -2.679192)
        val pasileku   = LatLng(43.313869, -2.678731)
        val arbol      = LatLng(43.313361, -2.680011)
        val astra      = LatLng(43.312436, -2.676803)

        // --- C. Añadir Marcadores con Subtítulo (Snippet) ---
        // El snippet aparece debajo del título al tocar el pin
        val textoAyuda = "Sakatu hemen ikusteko"

        mMap.addMarker(MarkerOptions().position(mural).title("Gernikako murala").snippet(textoAyuda))
        mMap.addMarker(MarkerOptions().position(museoPaz).title("Bakearen museoa").snippet("Laster erabilgarri"))
        mMap.addMarker(MarkerOptions().position(iglesia).title("Andra Mari Eliza").snippet(textoAyuda))
        mMap.addMarker(MarkerOptions().position(pasileku).title("Pasileku Plaza").snippet(textoAyuda))
        mMap.addMarker(MarkerOptions().position(arbol).title("Gernikako Arbola").snippet(textoAyuda))
        mMap.addMarker(MarkerOptions().position(astra).title("Astra").snippet(textoAyuda))

        // --- D. Enfocar Cámara ---
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mural, 16f))

        // --- E. DETECTAR CLIC EN LA BURBUJA DE INFORMACIÓN (InfoWindow) ---
        // Esto solo se activa cuando tocas la cajita blanca que sale sobre el marcador
        mMap.setOnInfoWindowClickListener { marker ->

            when (marker.title) {
                "Gernikako murala" -> {
                    startActivity(Intent(this, GernikakoMuralaOngiEtorriActivity::class.java))
                }

                "Astra" -> {
                    startActivity(Intent(this, AstraActivity::class.java))
                }

                "Gernikako Arbola" -> {
                    startActivity(Intent(this, ArbolaActivity::class.java))
                }

                "Andra Mari Eliza" -> {
                    startActivity(Intent(this, AndraMariActivity::class.java))
                }

                "Pasileku Plaza" -> {
                    startActivity(Intent(this, PasilekuActivity::class.java))
                }

                "Bakearen museoa" -> {
                    // Alerta informativa
                    AlertDialog.Builder(this)
                        .setTitle("Bakearen museoa")
                        .setMessage("Atala hau prestatzen ari gara. Laster erabilgarri!")
                        .setPositiveButton("Ados", null)
                        .show()
                }
            }
        }
    }
}