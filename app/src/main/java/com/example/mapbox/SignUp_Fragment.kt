package com.example.mapbox

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.example.mapbox.model.TrackLocation
import com.example.mapbox.mvvm.MapViewModel
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.sign_up_fragment.view.*

/**
 * A simple [Fragment] subclass.
 */
class SignUp_Fragment : Fragment() {

    private val viewModel: MapViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MapViewModel::class.java)
    }



    lateinit var root:View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root =inflater.inflate(R.layout.sign_up_fragment, container, false)


        root.btn_create.setOnClickListener {
            if (root.etxt_email.text.isNotEmpty() && root.etxt_name.text.isNotEmpty()) {
                signup(root.etxt_email.text.toString(), root.etxt_name.text.toString())
            }
        }


        root.btn_have_anAccount.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_signUp_Fragment_to_signIn_Fragment2)
        }

        return root
    }

    private fun signup(email:String, name: String) {
        viewModel.mAuth.createUserWithEmailAndPassword(email, "123456").addOnSuccessListener {

            insertLocation(TrackLocation(it.user.uid,name,GeoPoint(32.1,34.1)),it.user.uid)
            Navigation.findNavController(root)
                .navigate(R.id.action_signUp_Fragment_to_map_Fragment)
        }.addOnFailureListener {
            Log.e("eee login error", it.message.toString())
        }
    }


    fun insertLocation(user: TrackLocation,uid:String) {
        val i = viewModel.firestore.collection("location").document(uid).set(user)

    }


}
