package com.example.oslobysykkelfinal

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.io.Serializable


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    //private val client = OkHttpClient()

    /*
    *Datasets with API data from Oslobyskkel**/
    var dataset_1: ArrayList<data> = ArrayList<data>()
    var status1 = false
    var dataset_2: ArrayList<data_info> = ArrayList<data_info>()
    var status2 = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        getAPI()

        //Hardcoded async await
        while(!status1){ println("Wating for onresponse 1") }
        while(!status2){ println("Wating for onresponse 2") }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMapToolbarEnabled = false
        mMap.setMaxZoomPreference(15.0F)
        onCameraChange(mMap.cameraPosition, mMap)
        var listofsations = dataset_1[0].stations
        var listofstationinfo = dataset_2[0].stations

        //Assigning attributes and fillling in custom infowindow for marker for google maps
        for (station in listofsations){
            var pos = LatLng(station.lat.toDouble(), station.lon.toDouble())
            var name = station.name
            var address = station.address
            var capacity = station.capacity
            var numbikes = "empty"
            var numdocks = "empty"

            for (info in listofstationinfo){
                numbikes = info.num_bikes_available
                numdocks = info.num_docks_available
            }
            mMap.addMarker(MarkerOptions().position(pos).title(name).snippet(
                    "Address: " + address + "\n" +
                            "Capacity: " + capacity + "\n" +
                            "Bikes available: " + numbikes + "\n" +
                            "Docks available: " + numdocks
            ))
            mMap.setInfoWindowAdapter(CustomInfoWindowForGoogleMap(this))
            mMap.setOnInfoWindowClickListener(OnInfoWindowClickListener { marker -> marker.hideInfoWindow() })
        }

    }

    fun onCameraChange(position: CameraPosition, googleMap: GoogleMap) {
        val maxZoom = 17.0f
        if (position.zoom > maxZoom) googleMap.animateCamera(CameraUpdateFactory.zoomTo(maxZoom))
    }

    //Function call for OkHttpClient for API requests
    fun getAPI(){
        val stations_url = "https://gbfs.urbansharing.com/oslobysykkel.no/station_information.json"
        val stations_info_url = "https://gbfs.urbansharing.com/oslobysykkel.no/station_status.json"

        val request = Request.Builder().url(stations_url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                println("Failed to execute")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val body = response?.body()?.string()

                val gson = GsonBuilder().create()
                val station = gson.fromJson(body, data_set_stations::class.java)
                pass_dataset1(station.data)
                set_status1()
            }
        })

        val request2 = Request.Builder().url(stations_info_url).build()
        val client2 = OkHttpClient()
        client2.newCall(request2).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                println("Failed to execute")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val body = response?.body()?.string()
                val gson = GsonBuilder().create()
                val station_info = gson.fromJson(body, data_set_info::class.java)
                pass_dataset2(station_info.data)
                set_status2()
            }
        })
    }

    //Different helpers for getting and handling data from API
    fun set_status1(){ status1 = true }

    fun set_status2(){ status2 = true }

    fun pass_dataset1(data: data){ dataset_1.add(data) }

    fun pass_dataset2(data: data_info){ dataset_2.add(data) }
}

//Custom infowindow for marker
class CustomInfoWindowForGoogleMap(context: Context) : GoogleMap.InfoWindowAdapter {

    var mWindow = (context as Activity).layoutInflater.inflate(R.layout.custom_info_window, null)

    private fun rendowWindowText(marker: Marker, view: View){

        val name = view.findViewById<TextView>(R.id.title)
        val info = view.findViewById<TextView>(R.id.snippet)


        name.text = marker.title
        info.text = marker.snippet

    }

    override fun getInfoContents(marker: Marker): View {
        rendowWindowText(marker, mWindow)
        return mWindow
    }

    override fun getInfoWindow(marker: Marker): View? {
        rendowWindowText(marker, mWindow)
        return mWindow
    }
}

//Different classes for assigning datatype for dataset from API
class data_set_stations(val data: data)
class data(val stations: List<station>) : Serializable
class station(val station_id: String, val name: String, val address: String, val capacity: String, val lat: String, val lon: String)
class data_set_info(val data: data_info)
class data_info(val stations: List<station_info>) : Serializable
class station_info(val station_id: String, val num_bikes_available: String, val num_docks_available: String)

