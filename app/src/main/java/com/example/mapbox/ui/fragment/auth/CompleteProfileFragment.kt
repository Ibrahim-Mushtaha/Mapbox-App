package com.example.mapbox.ui.fragment.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mapbox.R
import com.example.mapbox.databinding.FragmentCompleteProfileBinding
import com.example.mapbox.databinding.FragmentPhoneNumberBinding
import kotlinx.android.synthetic.main.fragment_complete_profile.*

class CompleteProfileFragment : Fragment() {

    lateinit var mbinding:FragmentCompleteProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mbinding = FragmentCompleteProfileBinding.inflate(inflater,container,false).apply {
            executePendingBindings()
        }
        return mbinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_login.setOnClickListener {
            EnableLocationFragment().show(childFragmentManager,"")
        }


    }

}