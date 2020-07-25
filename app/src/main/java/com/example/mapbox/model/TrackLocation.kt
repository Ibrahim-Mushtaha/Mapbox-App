package com.example.mapbox.model

import com.google.firebase.firestore.GeoPoint
import com.mapbox.mapboxsdk.geometry.LatLng

data class TrackLocation(var uid :String,var name :String,var latLng:GeoPoint?) {
    constructor():this("","",GeoPoint(32.1,34.1))
}