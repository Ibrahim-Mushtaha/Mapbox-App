package com.example.mapbox.ui.viewModel.home

import android.app.Activity
import android.app.Application
import android.graphics.Color
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mapbox.R
import com.example.mapbox.model.location.TrackLocation
import com.example.mapbox.util.Constant.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import kotlinx.android.synthetic.main.fragment_map.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URI

class MapViewModel(application: Application):AndroidViewModel(application) {

    val context= application
    var navigation: NavigationMapRoute? = null
    val mAuth by lazy {
        FirebaseAuth.getInstance()
    }


    val dataUserLiveData = MutableLiveData<List<TrackLocation>>()



    fun getAllUser(mMap:MapboxMap){
        FirebaseFirestore.getInstance().collection("location").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
        val array = ArrayList<TrackLocation>()
            mMap.clear()
            querySnapshot!!.documents.forEach {
                val item  = it.toObject(TrackLocation::class.java)
                    array.add(item!!)
            }
            dataUserLiveData.postValue(array)
        }
    }



     fun userLocation(map: MapboxMap, loadedStyle: Style,activity: Activity, onComplete: (userLocation: LatLng) -> Unit){
        val locationComponent = map.locationComponent

         val customLocationComponentOptions =
             LocationComponentOptions.builder(activity)
                 .elevation(5f)
                 .accuracyAlpha(.6f)
                 .accuracyColor(Color.TRANSPARENT)
                 .foregroundDrawable(R.drawable.ic_my_marker)
                 .build()

         val locationComponentActivationOptions =
             LocationComponentActivationOptions.builder(activity, loadedStyle)
                 .locationComponentOptions(customLocationComponentOptions)
                 .build()

         locationComponent.activateLocationComponent(locationComponentActivationOptions)
         locationComponent.isLocationComponentEnabled=true

         locationComponent.cameraMode= CameraMode.TRACKING
         locationComponent.renderMode = RenderMode.COMPASS
         val knownlocation =locationComponent.lastKnownLocation

         val userLocation = LatLng(knownlocation!!.latitude,knownlocation.longitude)
         onComplete(userLocation)
     }


     fun MoveCamera(mMap: MapboxMap,mStyle:Style,activity: Activity,latitude:Double,longitude:Double){

            val position = CameraPosition.Builder()
                .target(LatLng(latitude,longitude))
                .zoom(11.0)
                .tilt(20.0)
                .build()

            mMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(position),
                4000
            )

    }


    fun UpdateLocation(latLng: GeoPoint) {
        val has = HashMap<String, Any>()
        has["latLng"] = latLng
        FirebaseFirestore.getInstance().collection("location")
            .document( FirebaseAuth.getInstance().uid!!).update(has)
    }





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
             Log.e("$TAG getGeoOption",e.message.toString())
         }
    }



    fun getGeoOption(location: LatLng,onComplete: (option: MarkerOptions) -> Unit){
        try {
            val log = Geocoder(context)
            val p = log.getFromLocation(location.latitude+0.1,  location.longitude+0.1, 1)
            val options = MarkerOptions().apply {
                title = "kamal AL Naser Street"
                snippet = p[0].postalCode
            }
            onComplete(options)

        }catch (e:Exception){
            Log.e("$TAG getGeoOption",e.message.toString())
        }
    }




    //start Routing mode
     fun getRoute(origin: Point, destination: Point,mapView: MapView,mMap: MapboxMap,onComplete: (currentlocation: DirectionsRoute) -> Unit) {

        NavigationRoute.builder(context)
            .accessToken(Mapbox.getAccessToken()!!)
            .origin(origin)
            .destination(destination).build()
            .getRoute(object : Callback<DirectionsResponse> {
                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                    Log.e("$TAG navigation", t.message.toString())
                }

                override fun onResponse(
                    call: Call<DirectionsResponse>,
                    response: Response<DirectionsResponse>
                ) {
                    if (response.body() == null || response.body()!!.routes().size > 1) {
                        Log.e("$TAG", "no route found")
                        return
                    }
                  val currentRoute = response.body()!!.routes().get(0)

                    onComplete(currentRoute)
                    if (navigation != null) {
                        navigation!!.removeRoute()
                    } else {
                        navigation =
                            NavigationMapRoute(
                                null, mapView, mMap,
                                R.style.NavigationMapRoute
                            )
                    }

                    navigation!!.addRoute(response.body()!!.routes().first())
                }

            })
    }

}