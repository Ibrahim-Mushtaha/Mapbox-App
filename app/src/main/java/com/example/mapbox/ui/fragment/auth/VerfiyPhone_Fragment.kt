package com.example.mapbox.ui.fragment.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.mapbox.R
import com.example.mapbox.databinding.FragmentVerfiyPhoneBinding
import com.example.mapbox.model.location.TrackLocation
import com.example.mapbox.ui.viewModel.home.MapViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.fragment_verfiy_phone.*


class VerfiyPhone_Fragment : Fragment() {

    private val viewModel: MapViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MapViewModel::class.java)
    }

    lateinit var mbinding:FragmentVerfiyPhoneBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mbinding = FragmentVerfiyPhoneBinding.inflate(inflater,container,false).apply {
            executePendingBindings()
        }
        return mbinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        PinView.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.length == 5){
                    PinView.text!!.clear()
                    Toast.makeText(requireContext(),"done",Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_Verfiy_Fragment_to_completeProfileFragment)
                }
            }

        });
    }


    private fun signup(email:String, name: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, "123456").addOnSuccessListener {

            insertLocation(
                TrackLocation(
                    it.user!!.uid,
                    name,
                    GeoPoint(32.1, 34.1)
                ),it!!.user!!.uid)
           /* Navigation.findNavController(root)
                .navigate(R.id.action_signUp_Fragment_to_map_Fragment)*/
        }.addOnFailureListener {
            Log.e("eee login error", it.message.toString())
        }
    }


    fun insertLocation(user: TrackLocation, uid:String) {
        val i = FirebaseFirestore.getInstance().collection("location").document(uid).set(user)
    }


}
