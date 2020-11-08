package com.example.mapbox.ui.fragment.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mapbox.R
import com.example.mapbox.adapter.User_Adapter
import com.example.mapbox.databinding.FragmentMapBinding
import com.example.mapbox.model.callback.LocationListeningCallback
import com.example.mapbox.model.location.TrackLocation
import com.example.mapbox.model.request.Cell
import com.example.mapbox.model.request.CellInfo
import com.example.mapbox.model.response.CellLocation
import com.example.mapbox.ui.viewModel.home.MapViewModel
import com.example.mapbox.ui.viewModel.sim.CellLocationViewModel
import com.example.mapbox.ui.viewModel.sim.CellLocationViewModelFactory
import com.example.mapbox.ui.viewModel.sim.State
import com.google.firebase.firestore.GeoPoint
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_map.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MapFragment : Fragment() {


    private val callback = LocationListeningCallback(this)

    lateinit var mbinding: FragmentMapBinding
    var x =false

    var REQUEST_CODE_AUTOCOMPLETE = 2
    val geojsonSourceLayerId = "geojsonSourceLayerId"

    lateinit var mMap: MapboxMap
    lateinit var mStyle: Style

    var destinationMarker: LatLng? = null
    var currentRoute: DirectionsRoute? = null
    lateinit var originPosition: Point
    lateinit var destinationPosition: Point


    lateinit var locationEngine: LocationEngine

    private val viewModel: MapViewModel by lazy {
      ViewModelProvider(this)[MapViewModel::class.java]
    }

    private lateinit var viewModel2: CellLocationViewModel

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }


    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Mapbox.getInstance(
            requireActivity(),
            requireActivity().getString(R.string.tokenKey)
        )
        mbinding = FragmentMapBinding.inflate(inflater,container,false).apply {
            executePendingBindings()
        }
        return mbinding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        locationEngine =
            LocationEngineProvider.getBestLocationEngine(requireActivity())

       mapView.getMapAsync { mapboxMap ->
            mMap = mapboxMap
            viewModel.getAllUser(mMap)
        }



        initViewModel()
        initLocationLiveData()


        btn_show.setOnClickListener {
            requireActivity().drawer_layout.openDrawer(Gravity.START)
        }



        btn_getMyLocation.setOnClickListener {
            if (!isLocationEnabled(requireContext())) {
                onClickFindLocation()
                mMap.clear()
            }else{
                mMap.clear()
                startLocationRrequest(mStyle,1,null)
            }
        }


        start_route.setOnClickListener {
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


       btn_search.setOnClickListener {
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


        mapView.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(mapboxMap: MapboxMap) {

                mapboxMap.uiSettings.isZoomGesturesEnabled = true

                mapboxMap.setStyle(Style.MAPBOX_STREETS, object : Style.OnStyleLoaded {
                    override fun onStyleLoaded(style: Style) {
                        mStyle = style
                        // viewModel.addLayer_below_Layer(style)


                        if (!isLocationEnabled(requireContext())) {
                            onClickFindLocation()
                        }else{
                            startLocationRrequest(style,1,null)
                        }
                          setUpSource(style)




                        btn_change_style.setOnClickListener {
                            if (!x) {
                                mapboxMap.setStyle(Style.DARK)
                                contanier.setBackgroundResource(R.drawable.shape_gradint_night)
                                x=true
                            }else{
                                mapboxMap.setStyle(Style.MAPBOX_STREETS)
                                contanier.setBackgroundResource(R.drawable.shape_gradint)
                                x=false
                            }
                        }

                       /* viewModel.userLocation(mMap, mStyle, requireActivity()) {
                            val position2 = CameraPosition.Builder()
                                .target(LatLng(it.latitude, it.longitude))
                                .zoom(15.0)
                                .tilt(20.0)
                                .build()

                            try {
                                val log = Geocoder(activity)
                                val p = log.getFromLocation(it.latitude+1,  it.longitude+1, 1)
                                val options = MarkerOptions().apply {
                                    title = p[0].countryName
                                    position = LatLng( it.latitude,  it.longitude)
                                    snippet = p[0].postalCode
                                    IconFactory.getInstance(requireContext()).fromResource(R.drawable.ic_marker2)
                                }
                                first_place.text = options.title.toString()
                            }catch (e:Exception){
                                Log.e("eee add",e.message.toString())
                            }


                            mMap.animateCamera(
                                CameraUpdateFactory.newCameraPosition(position2),
                                10
                            )
                        }*/

                    }
                })

                mapboxMap.uiSettings.isAttributionEnabled = false
                mapboxMap.uiSettings.isLogoEnabled = false
                mapboxMap.uiSettings.isCompassEnabled=false



                mapboxMap.addOnMapLongClickListener { point ->
                    try {
                        if (destinationMarker != null) {
                            MarkerOptions().position(destinationMarker!!).marker.remove()
                        }
                        viewModel.addMarker(requireActivity(), point) { option ->
                            val x = mMap.addMarker(option).position
                            destinationMarker = x
                        Log.e("eee x = ",x.toString())
                        }
                        destinationPosition =
                            Point.fromLngLat(point.longitude, point.latitude)
                        viewModel.getRoute(originPosition, destinationPosition,mapView, mMap){
                            currentRoute = it
                        }
                        Log.e("eee destinationPos = ",destinationPosition.toString())
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
                if (source != null) {
                    source.setGeoJson(
                        FeatureCollection.fromFeatures(
                            arrayOf(
                                Feature.fromJson(
                                    selectedCarmenFeature.toJson()
                                )
                            )
                        )
                    )
                }

                viewModel.MoveCamera(mMap,mStyle,requireActivity(),(selectedCarmenFeature.geometry() as Point?)!!.latitude(), (selectedCarmenFeature.geometry() as Point?)!!.longitude())
            }
        }
    }




    private fun startLocationRrequest(style: Style,type: Int,location:LatLng?){
        if (type==1) {
            viewModel.userLocation(mMap, style, requireActivity()) {
                originPosition = Point.fromLngLat(it.longitude, it.latitude)

                val x = GeoPoint(it.latitude, it.longitude)
                //  viewModel.UpdateLocation(x)

                val position = CameraPosition.Builder()
                    .target(LatLng(it.latitude, it.longitude))
                    .zoom(12.0)
                    .tilt(20.0)
                    .build()

                mMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(position),
                    4000
                )

                viewModel.getGeoOption(it){options->
                    first_place.text =  options.title
                }

            }
        }else{

            originPosition = Point.fromLngLat(location!!.longitude, location.latitude)

            val x = GeoPoint(location.latitude, location.longitude)
            //  viewModel.UpdateLocation(x)

            val position = CameraPosition.Builder()
                .target(LatLng(location.latitude, location.longitude))
                .zoom(12.0)
                .tilt(20.0)
                .build()

            mMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(position),
                4000
            )

            viewModel.getGeoOption(location){options->
                first_place.text =  "Eye Hospital General"
            }

            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(location.latitude, location.longitude))
                    .title("Your location using SIM")
            )

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




    private fun initViewModel() {
        viewModel2 = ViewModelProvider(
            this,
            CellLocationViewModelFactory()
        )[CellLocationViewModel::class.java]
    }

    private fun initLocationLiveData() {
        viewModel2.locationLiveData.observe(
            requireActivity(),
            Observer(::onStateChanged)
        )
    }

    private fun onClickFindLocation() {
        viewModel2.fetchLocation(CellInfo(requireActivity().getString(R.string.openGlToken),"gsm",425,5, listOf(Cell(7020, 52421, 789)),1))
    }

    private fun onStateChanged(state: State) {
        when (state) {
            is State.Loading -> {
                Log.e("eee location loading","loading")
            }
            is State.Failed -> {
                Log.e("eee location","Failed")
            }
            is State.Success -> {
                Log.e("eee location","Success")
                showLocationInfo(state.response)
            }
        }
    }

    private fun showLocationInfo(cellLocation: CellLocation) {
        Log.e("eee location","${cellLocation.latitude.toString()} || ${cellLocation.longitude.toString()}")
        startLocationRrequest(mStyle,0,
            LatLng(cellLocation.latitude!!.toDouble(),cellLocation.longitude!!.toDouble())
        )
    }


    private fun isLocationEnabled(mContext: Context): Boolean {
        val lm = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

}
