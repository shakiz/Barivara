package com.shakil.barivara.presentation.tutorial

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.databinding.ActivityVideoTutorialBinding
import com.shakil.barivara.presentation.onboard.HomeActivity
import com.shakil.barivara.utils.ButtonActionConstants

class VideoTutorialActivity : BaseActivity<ActivityVideoTutorialBinding>() {
    private lateinit var activityVideoTutorialBinding: ActivityVideoTutorialBinding

    override val layoutResourceId: Int
        get() = R.layout.activity_video_tutorial

    override fun setVariables(dataBinding: ActivityVideoTutorialBinding) {
        activityVideoTutorialBinding = dataBinding
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            buttonAction(
                ButtonActionConstants.actionTutorialClose
            )
            startActivity(
                Intent(
                    this@VideoTutorialActivity,
                    HomeActivity::class.java
                )
            )
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        init()
        initListener()
    }

    private fun init() {
        lifecycle.addObserver(activityVideoTutorialBinding.youtubePlayerView)
    }

    private fun initListener() {
        activityVideoTutorialBinding.youtubePlayerView.addYouTubePlayerListener(object :
            AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                val videoId = "JpT6m7d8908"
                youTubePlayer.loadVideo(videoId, 0f)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        activityVideoTutorialBinding.youtubePlayerView.release()
    }
}