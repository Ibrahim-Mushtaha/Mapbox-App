package com.example.mapbox.ui.fragment.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.mapbox.R
import com.example.mapbox.adapter.ImageSliderAdapter
import com.example.mapbox.databinding.FragmentOnBoardingBinding
import kotlinx.android.synthetic.main.fragment_on_boarding.*

class OnBoardingFragment : Fragment() {


    lateinit var mbinding:FragmentOnBoardingBinding

    private val image_adapter by lazy {
    ImageSliderAdapter(ArrayList())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mbinding = FragmentOnBoardingBinding.inflate(inflater,container,false).apply {
            executePendingBindings()
        }
        return mbinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        image_adapter.data.clear()
        image_adapter.data.add(R.drawable.ic_image1)
        image_adapter.data.add(R.drawable.ic_image2)
        image_adapter.notifyDataSetChanged()

        viewPager2.apply {
            adapter = image_adapter
            indicator.setViewPager2(viewPager2)
        }

        btn_create_phone.setOnClickListener {
            findNavController().navigate(R.id.action_onBoardingFragment_to_phoneNumber_Fragment)
        }

    }

}