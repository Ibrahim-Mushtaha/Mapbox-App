package com.example.mapbox

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.example.mapbox.mvvm.MapViewModel
import kotlinx.android.synthetic.main.sign_in_fragment.view.*

/**
 * A simple [Fragment] subclass.
 */
class SignIn_Fragment : Fragment() {


    private val viewModel: MapViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MapViewModel::class.java)
    }

    override fun onStart() {
        if (viewModel.mAuth!!.uid != null){
            Navigation.findNavController(root)
                .navigate(R.id.action_signIn_Fragment_to_map_Fragment)
        }
        super.onStart()
    }


    lateinit var root:View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root= inflater.inflate(R.layout.sign_in_fragment, container, false)

        root.btn_login.setOnClickListener {
            if (root.etxt_email_login.text.isNotEmpty()){
                SignIn(root.etxt_email_login.text.toString())
            }
        }


        root.btn_create_new.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_signIn_Fragment_to_signUp_Fragment)
        }


        return  root
    }


    private fun SignIn(email:String) {
        viewModel.mAuth.signInWithEmailAndPassword(email,"123456").addOnSuccessListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_signIn_Fragment_to_map_Fragment)
        }.addOnFailureListener {
            Log.e("eee","user not found")
        }
    }
}
