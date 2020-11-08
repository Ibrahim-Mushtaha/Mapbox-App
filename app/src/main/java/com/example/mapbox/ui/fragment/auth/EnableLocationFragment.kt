package com.example.mapbox.ui.fragment.auth

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mapbox.R
import com.example.mapbox.databinding.FragmentCompleteProfileBinding
import com.example.mapbox.databinding.FragmentEnableLocationBinding
import com.example.mapbox.databinding.FragmentPhoneNumberBinding
import com.example.mapbox.ui.activity.MainActivity
import com.example.mapbox.util.Constant
import com.example.mapbox.util.Constant.OPEN
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.GeoPoint
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style
import kotlinx.android.synthetic.main.fragment_enable_location.*

class EnableLocationFragment : BottomSheetDialogFragment() {

    lateinit var mbinding: FragmentEnableLocationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mbinding = FragmentEnableLocationBinding.inflate(inflater,container,false).apply {
            executePendingBindings()
        }
        isCancelable=false
        return mbinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_enable.setOnClickListener {
            getPermission()
        }

    }


    private fun getPermission() {
        Dexter.withContext(requireActivity())
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    Constant.getSharePref(requireContext()).edit().putBoolean(OPEN,true).apply()
                    startActivity(
                        Intent(
                            requireActivity(),
                            MainActivity::class.java
                        )
                    ).also {
                        requireActivity().finish()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    p1!!.continuePermissionRequest()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Constant.getSharePref(requireContext()).edit().putBoolean(OPEN,false).apply()
                    requireActivity().finish()
                }

            }).check()
    }


}