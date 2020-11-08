package com.example.mapbox.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mapbox.R
import com.example.mapbox.databinding.ActivitySplashBinding
import com.example.mapbox.ui.viewModel.splash.SplashState
import com.example.mapbox.ui.viewModel.splash.SplashViewModel
import com.example.mapbox.util.Constant
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    lateinit var mBinding: ActivitySplashBinding

    private val viewModel by lazy {
        ViewModelProvider(this)[SplashViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        Constant.setUpStatusBar(this,0)
        viewModel.liveData.observe(this, Observer {
            when (it) {
                is SplashState.MainActivity -> {
                   goToMainActivity()
                }
            }
        })

        val a: Animation = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        a.reset()

        imgSplashLogo.clearAnimation()
        imgSplashLogo.startAnimation(a)

    }


    private fun goToMainActivity() {
        startActivity(Intent(this, if (Constant.getSharePref(this).getBoolean(Constant.OPEN,false)){
            MainActivity::class.java
        }else{
            AuthActivity::class.java
        }))
        finish()
    }

}