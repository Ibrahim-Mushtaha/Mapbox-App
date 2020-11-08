package com.example.mapbox.ui.fragment.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.mapbox.databinding.FragmentPhoneNumberBinding
import com.example.mapbox.ui.viewModel.home.MapViewModel
import com.example.mapbox.util.Constant
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.fragment_phone_number.*
import java.util.concurrent.TimeUnit


/**
 * A simple [Fragment] subclass.
 */
class PhoneNumber_Fragment : Fragment() {

    lateinit var mbinding:FragmentPhoneNumberBinding
    lateinit var codebysystem:String
    private val viewModel: MapViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MapViewModel::class.java)
    }
    lateinit var verificationId:String




    override fun onStart() {
        if ( FirebaseAuth.getInstance().uid != null){
           /* Navigation.findNavController(root)
                .navigate(R.id.action_signIn_Fragment_to_map_Fragment)*/
        }
        super.onStart()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mbinding = FragmentPhoneNumberBinding.inflate(inflater,container,false).apply {
            executePendingBindings()
        }
        return mbinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        btn_next.setOnClickListener {

            if (etxt_phone_number.text.isEmpty()) {
                etxt_phone_number.error = "you must write a number"
                etxt_phone_number.requestFocus()
                return@setOnClickListener
            }
            //Constant.showDialog(requireActivity())
            Constant.showDialog(requireActivity())
            startPhoneNumberVerification(etxt_phone_number.text.toString())

        }
    }


    private fun startPhoneNumberVerification(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+"+countryPiker.selectedCountryCodeAsInt.toString()+phoneNumber, // Phone number to verify
            20,
            TimeUnit.SECONDS,
            requireActivity(),
            callbacks())
    }


    private fun callbacks ()=object :PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(p0)
            Toast.makeText(activity,p0.smsCode, Toast.LENGTH_SHORT).show()
            //Toast.makeText(activity,p0.smsCode,Toast.LENGTH_SHORT).show()
            /*  myApp.progressDialog.dismiss()*/

        }

        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(p0, p1)
            Toast.makeText(activity,"send", Toast.LENGTH_SHORT).show()
            Toast.makeText(activity,p0, Toast.LENGTH_SHORT).show()
            codebysystem=p0
            sendSmsCode(p0)
            Log.e("eee code 2",p0)
            Constant.dialog.dismiss()
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            Log.e("eee code",p0.message)
            Constant.dialog.dismiss()
            Toast.makeText(activity,p0.message, Toast.LENGTH_SHORT).show()
        }
    }


    private fun sendSmsCode(code:String){
        Thread(Runnable {
            val bundle = Bundle()
            bundle.putString("code",code)
            bundle.putString("codebysystem",codebysystem)

            Log.e("eee code",code)
            findNavController().navigate(com.example.mapbox.R.id.action_phoneNumber_Fragment_to_Verfiy_Fragment,bundle)
        }).start()
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(requireActivity(), object : OnCompleteListener<AuthResult?>{
                override fun onComplete(p0: Task<AuthResult?>) {

                }

            })
    }

    private fun SignIn(email:String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, codebysystem)

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,"123456").addOnSuccessListener {
      /*      Navigation.findNavController(root)
                .navigate(R.id.action_signIn_Fragment_to_map_Fragment)*/
        }.addOnFailureListener {
            Log.e("eee","user not found")
        }
    }
}
