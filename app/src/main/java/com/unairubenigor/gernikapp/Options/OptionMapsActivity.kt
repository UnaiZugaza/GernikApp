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
import com.unairubenigor.gernikapp.Actividades_Geltokiak.AndraMari1Activity
import com.unairubenigor.gernikapp.Actividades_Geltokiak.AstraActivity
import com.unairubenigor.gernikapp.Actividades_Geltokiak.GernikakoArbolaOngiEtorriActivity
import com.unairubenigor.gernikapp.Actividades_Geltokiak.GernikakoMuralaOngiEtorriActivity
import com.unairubenigor.gernikapp.Actividades_Geltokiak.PasilekuActivity
import com.unairubenigor.gernikapp.R

class OptionMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var nombreUsuario: String? = ""
    private var codigoClase: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option_maps)

        // Recoger datos de la sesión
        nombreUsuario = intent.getStringExtra("USER_NAME")
        codigoClase = intent.getStringExtra("CLASS_CODE")

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
        mMap.setOnInfoWindowClickListener { marker ->

            val intent = when (marker.title) {
                "Gernikako murala" -> Intent(this, GernikakoMuralaOngiEtorriActivity::class.java)
                "Astra" -> Intent(this, AstraActivity::class.java)
                "Gernikako Arbola" -> Intent(this, GernikakoArbolaOngiEtorriActivity::class.java)
                "Andra Mari Eliza" -> Intent(this, AndraMari1Activity::class.java)
                "Pasileku Plaza" -> Intent(this, PasilekuActivity::class.java)
                else -> null
            }

            if (intent != null) {
                // Pasar datos de usuario y clase a todas las actividades
                intent.putExtra("USER_NAME", nombreUsuario)
                intent.putExtra("CLASS_CODE", codigoClase)
                startActivity(intent)
            } else if (marker.title == getString(R.string.bakearen_museoa)) {
                AlertDialog.Builder(this)
                    .setTitle(R.string.bakearen_museoa)
                    .setMessage(getString(R.string.atala_hau_prestatzen_ari_gara_laster_erabilgarri))
                    .setPositiveButton(getString(R.string.ados), null)
                    .show()
            }
        }
    }
}
