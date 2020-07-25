package com.example.mapbox

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.mapbox.model.LocationListeningCallback
import com.example.mapbox.model.TrackLocation
import com.example.mapbox.mvvm.MapViewModel
import com.example.mapbox.util.User_Adapter
import com.google.firebase.firestore.GeoPoint
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.mapbox.android.core.location.*
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import kotlinx.android.synthetic.main.map_fragment.mapView
import kotlinx.android.synthetic.main.map_fragment.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class map_Fragment : Fragment(), User_Adapter.onClick {


    private val callback = LocationListeningCallback(this)

    var array2 = ArrayList<TrackLocation>()

    private val adapter by lazy {
        User_Adapter(requireActivity(), array2, this)
    }


    var REQUEST_CODE_AUTOCOMPLETE = 2
    val geojsonSourceLayerId = "geojsonSourceLayerId"

    lateinit var mMap: MapboxMap
    lateinit var mStyle: Style

    var navigation: NavigationMapRoute? = null

    var destinationMarker: LatLng? = null
    var currentRoute: DirectionsRoute? = null
    lateinit var originPosition: Point
    lateinit var destinationPosition: Point


    lateinit var locationEngine: LocationEngine
    var array = ArrayList<LatLng>()

    private val viewModel: MapViewModel by lazy {
        ViewModelProviders.of(this).get(MapViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        viewModel.getUser.observe(requireActivity(), Observer {
            it.forEach {
                if (it.uid != viewModel.mAuth.currentUser!!.uid) {
                    val Marker = MarkerOptions().apply {
                        title = it.name
                        position = LatLng(
                            it.latLng!!.latitude,
                            it.latLng!!.longitude
                        )
                        IconFactory.getInstance(requireContext())
                            .fromResource(R.drawable.ic_marker2)
                    }
                    mMap.addMarker(Marker)
                }
            }
            array2 = it as ArrayList<TrackLocation>
            root.user_list.adapter = adapter
        })
    }


    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }


    lateinit var root: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Mapbox.getInstance(
            requireActivity(),
            requireActivity().getString(R.string.tokenKey)
        )
        root = inflater.inflate(R.layout.map_fragment, container, false)
        root.apply {

            locationEngine =
                LocationEngineProvider.getBestLocationEngine(requireActivity())

            root.mapView.getMapAsync { mapboxMap ->
                mMap = mapboxMap
                viewModel.getAllUser(mMap)
            }

            root.mapView.onCreate(savedInstanceState)




            root.btn_show.setOnClickListener {
                root.user_list.visibility = View.VISIBLE
                root.btn_show.visibility = View.GONE
                root.btn_hide.visibility = View.VISIBLE
            }
            root.btn_hide.setOnClickListener {
                root.user_list.visibility = View.GONE
                root.btn_show.visibility = View.VISIBLE
                root.btn_hide.visibility = View.GONE
            }

            root.btn_getMyLocation.setOnClickListener {
                viewModel.userLocation(mMap, mStyle, requireActivity()) {
                    val position = CameraPosition.Builder()
                        .target(LatLng(it.latitude, it.longitude))
                        .zoom(15.0)
                        .tilt(20.0)
                        .build()

                    mMap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(position),
                        10
                    )
                }
            }


            root.btn_start.setOnClickListener {
                if (currentRoute != null) {
                    val start =
                        NavigationLauncherOptions.builder().directionsRoute(currentRoute)
                            .shouldSimulateRoute(true)
                            .build()
                    NavigationLauncher.startNavigation(requireActivity(), start)
                } else {
                    Toast.makeText(
                        requireActivity(),
                        "You must select the route",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            root.btn_search.setOnClickListener {
                val intent = PlaceAutocomplete.IntentBuilder()
                    .accessToken(requireActivity().getString(R.string.tokenKey))
                    .placeOptions(
                        PlaceOptions.builder()
                            .backgroundColor(Color.parseColor("#EEEEEE"))
                            .limit(10)
                            .build(PlaceOptions.CONTENTS_FILE_DESCRIPTOR)
                    )
                    .build(requireActivity())
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE)
            }
            root.mapView.getMapAsync(object : OnMapReadyCallback {
                override fun onMapReady(mapboxMap: MapboxMap) {

                    mapboxMap.uiSettings.isZoomGesturesEnabled = true

                    mapboxMap.setStyle(Style.MAPBOX_STREETS, object : Style.OnStyleLoaded {
                        override fun onStyleLoaded(style: Style) {
                            mStyle = style
                            // viewModel.addLayer_below_Layer(style)
                            startLocationRrequest(style)
                            setUpSource(style)
                        }

                    })




                    mapboxMap.addOnMapLongClickListener { point ->
                        try {
                            if (destinationMarker != null) {

                                MarkerOptions().position(destinationMarker!!).marker.remove()
                            }
                            viewModel.addMarker(requireActivity(), point) { option ->
                                val x = mMap.addMarker(option).position
                                destinationMarker = x
                            }
                            destinationPosition =
                                Point.fromLngLat(point.longitude, point.latitude)
                            getRoute(originPosition, destinationPosition)
                        } catch (e: Exception) {
                            Log.e("eee", e.message.toString())
                        }

                        true
                    }

                    mapboxMap.setOnInfoWindowClickListener {
                        Toast.makeText(requireActivity(), it.title.toString(), Toast.LENGTH_SHORT)
                            .show()
                        true
                    }
                }
            })


        }
        return root
    }


    fun setUpSource(loadedMapStyle: Style) {
        loadedMapStyle.addSource(GeoJsonSource(geojsonSourceLayerId));
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            val selectedCarmenFeature = PlaceAutocomplete.getPlace(data)
            val style = mMap.style
            if (style != null) {

                val source = style.getSourceAs<GeoJsonSource>(geojsonSourceLayerId)!!
               // if (source != null) {
                    source.setGeoJson(
                        FeatureCollection.fromFeatures(
                            arrayOf(
                                Feature.fromJson(
                                    selectedCarmenFeature.toJson()
                                )
                            )
                        )
                    )
                //}

                viewModel.MoveCamera(mMap,mStyle,requireActivity(),(selectedCarmenFeature.geometry() as Point?)!!.latitude(), (selectedCarmenFeature.geometry() as Point?)!!.longitude())
            }
        }
    }

    //start Routing mode
    private fun getRoute(origin: Point, destination: Point) {
        NavigationRoute.builder(requireActivity())
            .accessToken(Mapbox.getAccessToken()!!)
            .origin(origin)
            .destination(destination).build()
            .getRoute(object : Callback<DirectionsResponse> {
                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                    Log.e("eee navigation", t.message.toString())
                }

                override fun onResponse(
                    call: Call<DirectionsResponse>,
                    response: Response<DirectionsResponse>
                ) {
                    Log.e("eee response", response.body()!!.routes().first().toString())
                    if (response.body() == null || response.body()!!.routes().size > 1) {
                        Log.e("eee", "no route found")
                        return
                    }
                    currentRoute = response.body()!!.routes().get(0)


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


    private fun startLocationRrequest(style: Style) {
        Dexter.withContext(requireActivity())
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {

                    viewModel.userLocation(mMap, style, requireActivity()) {
                        originPosition = Point.fromLngLat(it.longitude, it.latitude)

                        val x = GeoPoint(it.latitude, it.longitude)
                        viewModel.UpdateLocation(x)

                        val position = CameraPosition.Builder()
                            .target(LatLng(it.latitude, it.longitude))
                            .zoom(15.0)
                            .tilt(20.0)
                            .build()

                        mMap.animateCamera(
                            CameraUpdateFactory.newCameraPosition(position),
                            10
                        )
                    }


                    val locationUpdate = LocationEngineRequest.Builder(3000)
                        .setPriority(LocationEngineRequest.PRIORITY_NO_POWER)
                        .setMaxWaitTime(1000L * 6)
                        .build()


                    locationEngine.requestLocationUpdates(
                        locationUpdate,
                        callback, Looper.getMainLooper()
                    )
                    locationEngine.getLastLocation(callback)

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    p1!!.continuePermissionRequest()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    requireActivity().finish()
                }

            }).check()
    }

    override fun onClickItem(position: Int, type: Int) {
        when (type) {
            1 -> {
                viewModel.MoveCamera(mMap, mStyle, requireActivity(), array2[position].latLng!!.latitude, array2[position].latLng!!.longitude)
            }
        }
    }


    override fun onPause() {
        super.onPause()
        locationEngine.removeLocationUpdates(callback)
        mapView.onPause()
    }


    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }


    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }


}
