package com.example.mapbox.mvvm

import android.Manifest
import android.app.Activity
import android.app.Application
import android.graphics.Color
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mapbox.R
import com.example.mapbox.model.TrackLocation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import java.net.URI

class MapViewModel(application: Application):AndroidViewModel(application) {

    val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val mAuth by lazy {
        FirebaseAuth.getInstance()
    }


    private val _Users = MutableLiveData<List<TrackLocation>>()

    val getUser: LiveData<List<TrackLocation>>
        get() = _Users




    fun getAllUser(mMap:MapboxMap){
        firestore.collection("location").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
        val array = ArrayList<TrackLocation>()


            mMap.clear()
            querySnapshot!!.documents.forEach {
                val item  = it.toObject(TrackLocation::class.java)
                    array.add(item!!)

            }
            _Users.value = array
        }
    }



     fun userLocation(map: MapboxMap, loadedStyle: Style,activity: Activity, onComplete: (userLocation: LatLng) -> Unit){
        val locationComponent = map.locationComponent
        locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(activity,loadedStyle).build())

        locationComponent.isLocationComponentEnabled=true
        locationComponent.cameraMode= CameraMode.TRACKING
        locationComponent.renderMode = RenderMode.COMPASS
        val knownlocation =locationComponent.lastKnownLocation
        val userLocation = LatLng(knownlocation!!.latitude,knownlocation.longitude)
        onComplete(userLocation)
    }


     fun MoveCamera(mMap: MapboxMap,mStyle:Style,activity: Activity,latitude:Double,longitude:Double){
        userLocation(mMap,mStyle,activity){
            val position = CameraPosition.Builder()
                .target(LatLng(latitude,longitude))
                .zoom(15.0)
                .tilt(20.0)
                .build()

            mMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(position),
                4000
            )
        }
    }


    fun UpdateLocation(latLng: GeoPoint) {
        val has = HashMap<String, Any>()
        has["latLng"] = latLng
         firestore.collection("location")
            .document(mAuth!!.uid!!).update(has)
    }


    // to add layer below another layer
     fun addLayer_below_Layer(style: Style) {
        try {
            // pass the location or json LatLng
            val urbanAreasSource = GeoJsonSource(
                "urban-areas",
                URI("https://d2ad6b4ur7yvpq.cloudfront.net/naturalearth-3.3.0/ne_50m_urban_areas.geojson")
            )

            style.addSource(urbanAreasSource)
            val urbanArea = FillLayer("urban-areas-fill", "urban-areas")
            // add theproperties to the location
            urbanArea.setProperties(
                PropertyFactory.fillColor(Color.parseColor("#ff0088")),
                PropertyFactory.fillOpacity(0.4f)
            )

            style.addLayerBelow(urbanArea, "water")
        } catch (e: Exception) {
            Log.e("eee", e.message.toString())
        }
    }


 /*   fun DragMarker(location: LatLng, onComplete: (option: MarkerOptions) -> Unit) {
        try {
            if (array.isEmpty() || array.size <= 2) {
                addMarker(location) {
                    array.add(LatLng(it.position.latitude, it.position.longitude))
                    destinationPosition =
                        Point.fromLngLat(it.position.longitude, it.position.latitude)
                    onComplete(it)
                }
            } else {
                addMarker(location) {
                    onComplete(it)
                    Log.e("eee destinationPosition", destinationPosition.toString())
                }
            }
            if (array.size == 2) {
                getRoute(originPosition, destinationPosition)
            }
        } catch (e: Exception) {
            Log.e("eee drag", e.message.toString())
        }

    }*/

     fun addMarker(activity: Activity,location: LatLng, onComplete: (option: MarkerOptions) -> Unit) {
         try {
             val log = Geocoder(activity)
             val p = log.getFromLocation(location.latitude, location.longitude, 1)
             val options = MarkerOptions().apply {
                 title = p[0].countryName
                 position = LatLng(location.latitude, location.longitude)
                 snippet = p[0].postalCode
                 IconFactory.getInstance(activity).fromResource(R.drawable.ic_marker2)
             }
             onComplete(options)
         }catch (e:Exception){
             Log.e("eee add",e.message.toString())
         }
    }
}