package com.example.mapbox.model.callback

import android.util.Log
import androidx.lifecycle.ViewModelProviders
import com.example.mapbox.ui.fragment.home.MapFragment
import com.example.mapbox.ui.viewModel.home.MapViewModel
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import java.lang.ref.WeakReference

class LocationListeningCallback internal constructor(activity: MapFragment) :
    LocationEngineCallback<LocationEngineResult> {

    private val viewModel: MapViewModel by lazy {
        ViewModelProviders.of(activity).get(MapViewModel::class.java)
    }

    private val activityWeakReference: WeakReference<MapFragment>

    init {
        this.activityWeakReference = WeakReference(activity)
    }


    override fun onSuccess(result: LocationEngineResult?) {
        val it=result!!.lastLocation

/*        val has = HashMap<String, Any>()
        val x = GeoPoint(it!!.latitude,it.longitude)
        has["latLng"] = x
        val i = viewModel.firestore.collection("location")
            .document(viewModel.mAuth!!.uid!!).update(has)*/


        Log.e("eee change", result!!.lastLocation.toString())
    }

    override fun onFailure(exception: java.lang.Exception) {
        Log.e("eee change Error",exception.message.toString())
    }

}

