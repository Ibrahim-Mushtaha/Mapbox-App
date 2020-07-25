package com.example.mapbox.model

import android.util.Log
import androidx.lifecycle.ViewModelProviders
import com.example.mapbox.map_Fragment
import com.example.mapbox.mvvm.MapViewModel
import com.google.firebase.firestore.GeoPoint
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.mapboxsdk.annotations.Marker
import java.lang.ref.WeakReference

class LocationListeningCallback internal constructor(activity: map_Fragment) :
    LocationEngineCallback<LocationEngineResult> {

    private val viewModel: MapViewModel by lazy {
        ViewModelProviders.of(activity).get(MapViewModel::class.java)
    }

    private val activityWeakReference: WeakReference<map_Fragment>

    init {
        this.activityWeakReference = WeakReference(activity)
    }


    override fun onSuccess(result: LocationEngineResult?) {
        val it=result!!.lastLocation

        val has = HashMap<String, Any>()
        val x = GeoPoint(it!!.latitude,it.longitude)
        has["latLng"] = x
        val i = viewModel.firestore.collection("location")
            .document(viewModel.mAuth!!.uid!!).update(has)


        Log.e("eee change", result!!.lastLocation.toString())
    }

    override fun onFailure(exception: java.lang.Exception) {
        Log.e("eee change Error",exception.message.toString())
    }

}

