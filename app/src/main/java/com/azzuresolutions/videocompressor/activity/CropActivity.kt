package com.azzuresolutions.videocompressor.activity

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.azzuresolutions.videocompressor.R
import com.azzuresolutions.videocompressor.databinding.ActivityCropBinding
import net.vrgsoft.videcrop.VideoCropActivity
import java.util.concurrent.ScheduledExecutorService

class CropActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCropBinding
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var timer: ScheduledExecutorService
    var duration: String? = null
    private val CROP_REQUEST = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCropBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_crop)
        val inputPath = "/storage/emulated/0/1111.mp4"
        val outputPath = "/storage/emulated/0/YDXJ08599.mp4"
//        val uri: String = getRealPathFromURI(GalleryFileActivity.videoList1[0].uri)!!
        startActivityForResult(
            VideoCropActivity.createIntent(
                this,
                inputPath,
                outputPath
            ), CROP_REQUEST)
//        loadVideo()
//        buttonClick()
    }

    private fun loadVideo() {

//        binding.videoView.requestFocus()
//        createMediaPlayer(uri)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CROP_REQUEST && resultCode == RESULT_OK) {
        }
    }

//    open fun getRealPathFromURI(contentUri: Uri?): String? {
//        val proj = arrayOf(MediaStore.Audio.Media.DATA)
//        val cursor: Cursor = managedQuery(contentUri, proj, null, null, null)
//        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
//        cursor.moveToFirst()
//        return cursor.getString(column_index)
//    }

    private fun buttonClick() {

//        binding.imgPlay.setOnClickListener {
//            binding.imgPlay.setImageDrawable(getDrawable(R.drawable.ic_pause))
//            if (mediaPlayer.isPlaying) {
//                mediaPlayer.pause()
//                timer.shutdown()
////                binding.videoView.pause()
//                binding.imgPlay.setImageDrawable(getDrawable(R.drawable.ic_play))
//            } else {
//                mediaPlayer.start()
////                binding.videoView.start()
//                binding.imgPlay.setImageDrawable(getDrawable(R.drawable.ic_pause))
//                timer = Executors.newScheduledThreadPool(1)
//                timer.scheduleAtFixedRate({
//                    if (!binding.playProgressBar.isPressed) {
//                        binding.playProgressBar.progress = mediaPlayer.currentPosition
//                    }
//                }, 10, 10, TimeUnit.MILLISECONDS)
//            }
//        }

//        binding.playProgressBar.setOnSeekBarChangeListener(object :
//            SeekBar.OnSeekBarChangeListener {
//            @SuppressLint("SetTextI18n")
//            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
//                val millis = mediaPlayer.currentPosition
//                val total_secs =
//                    TimeUnit.SECONDS.convert(millis.toLong(), TimeUnit.MILLISECONDS)
//                val mins = TimeUnit.MINUTES.convert(total_secs, TimeUnit.SECONDS)
//                val secs = total_secs - mins * 60
//                val sec: String = if (secs <= 9) {
//                    "0$secs"
//                } else {
//                    "$secs"
//                }
//                val min: String = if (mins <= 9) {
//                    "0$mins"
//                } else {
//                    "$mins"
//                }
//                binding.tvVideoDuration.text = "$min:$sec / $duration"
//            }
//
//            override fun onStartTrackingTouch(seekBar: SeekBar) {
//                mediaPlayer.seekTo(binding.playProgressBar.progress)
////                binding.videoView.seekTo(binding.playProgressBar.progress)
//            }
//
//            override fun onStopTrackingTouch(seekBar: SeekBar) {
//                mediaPlayer.seekTo(binding.playProgressBar.progress)
////                binding.videoView.seekTo(binding.playProgressBar.progress)
//            }
//        })
    }

//    fun createMediaPlayer(uri: Uri?) {
//        mediaPlayer = MediaPlayer()
//        mediaPlayer.setAudioAttributes(
//            AudioAttributes.Builder()
//                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                .setUsage(AudioAttributes.USAGE_MEDIA)
//                .build()
//        )
//        try {
//            mediaPlayer.setDataSource(applicationContext, uri!!)
//            mediaPlayer.prepare()
//            val millis: Int = mediaPlayer.duration
//            val total_secs: Long = TimeUnit.SECONDS.convert(millis.toLong(), TimeUnit.MILLISECONDS)
//            val mins: Long = TimeUnit.MINUTES.convert(total_secs, TimeUnit.SECONDS)
//            val secs = total_secs - mins * 60
//            val sec: String = if (secs <= 9) {
//                "0$secs"
//            } else {
//                "$secs"
//            }
//            val min: String = if (mins <= 9) {
//                "0$mins"
//            } else {
//                "$mins"
//            }
//            duration = "$min:$sec"
//            binding.playProgressBar.max = millis
//            binding.playProgressBar.progress = 0
//            mediaPlayer.setOnCompletionListener { releaseMediaPlayer() }
//        } catch (_: IOException) {
//        }
//        binding.tvVideoDuration.text = "00:00/$duration"
//    }
//
//    private fun releaseMediaPlayer() {
//        binding.ivPlay.setImageDrawable(this.getDrawable(R.drawable.ic_play))
//        binding.playProgressBar.progress = 0
//    }
}