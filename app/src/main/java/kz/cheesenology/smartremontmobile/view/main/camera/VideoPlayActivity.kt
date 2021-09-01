package kz.cheesenology.smartremontmobile.view.main.camera

import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_video_play.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.util.AppConstant
import java.io.File

class VideoPlayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        actionBar?.hide()

        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoPlayer)
        videoPlayer.setMediaController(mediaController)

        videoPlayer.setVideoURI(
                Uri.fromFile(
                        File(AppConstant.FULL_MEDIA_PHOTO_PATH + intent.getStringExtra("video_path"))))
        videoPlayer.requestFocus()
        videoPlayer.start()
    }
}
