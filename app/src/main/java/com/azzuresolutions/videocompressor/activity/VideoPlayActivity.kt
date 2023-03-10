package com.azzuresolutions.videocompressor.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.azzuresolutions.videocompressor.R
import com.azzuresolutions.videocompressor.databinding.ActivityVideoPlayBinding
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class VideoPlayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoPlayBinding
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var timer: ScheduledExecutorService
    var duration: String? = null
    private var uri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        videoLoad()
        buttonClick()
    }

    @SuppressLint("SetTextI18n")
    private fun videoLoad() {
//        binding.tvFileName.text = GalleryFileActivity.videoList1[0].name
//        binding.tvFileSize.text =
//            Common.formatTime(GalleryFileActivity.videoList1[0].duration.toLong(), this)
        binding.tvVideoDuration.text =
            GalleryFileActivity.videoList1[0].width.toString() + " X " +
                    GalleryFileActivity.videoList1[0].height.toString()
        uri = Uri.parse(GalleryFileActivity.videoList1[0].uri.toString())
        binding.videoView.setVideoURI(uri)
        binding.videoView.requestFocus()
        createMediaPlayer(uri)

        binding.tvSizeCompressed.text = formatSize(GalleryFileActivity.videoList1[0].size.toLong())
    }

    fun formatSize(size: Long): String {
        var size = size
        var suffix: String? = null
        if (size >= 1024) {
            suffix = " Bytes"
            size /= 1024
            if (size >= 1024) {
                suffix = " MB"
                size /= 1024
            }
        }
        val resultBuffer = StringBuilder(java.lang.Long.toString(size))
        var commaOffset = resultBuffer.length - 3
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',')
            commaOffset -= 3
        }
        if (suffix != null) resultBuffer.append(suffix)
        return resultBuffer.toString()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun buttonClick() {

        binding.btnBack.setOnClickListener {
            if (binding.videoView.isPlaying) {
                mediaPlayer.pause()
                timer.shutdown()
                binding.videoView.pause()
            }
            GalleryFileActivity.videoList1.clear()
            finish()
        }

        binding.playProgressBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val millis = mediaPlayer.currentPosition
                val total_secs =
                    TimeUnit.SECONDS.convert(millis.toLong(), TimeUnit.MILLISECONDS)
                val mins = TimeUnit.MINUTES.convert(total_secs, TimeUnit.SECONDS)
                val secs = total_secs - mins * 60
                val sec: String = if (secs <= 9) {
                    "0$secs"
                } else {
                    "$secs"
                }
                val min: String = if (mins <= 9) {
                    "0$mins"
                } else {
                    "$mins"
                }
                binding.tvVideoDuration.text = "$min:$sec / $duration"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                mediaPlayer.seekTo(binding.playProgressBar.progress)
                binding.videoView.seekTo(binding.playProgressBar.progress)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                mediaPlayer.seekTo(binding.playProgressBar.progress)
                binding.videoView.seekTo(binding.playProgressBar.progress)
            }
        })

        binding.imgPlay.setOnClickListener {
            binding.imgPlay.setImageDrawable(getDrawable(R.drawable.ic_pause))
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                timer.shutdown()
                binding.videoView.pause()
                binding.imgPlay.setImageDrawable(getDrawable(R.drawable.ic_play))
            } else {
                mediaPlayer.start()
                binding.videoView.start()
                binding.imgPlay.setImageDrawable(getDrawable(R.drawable.ic_pause))
                timer = Executors.newScheduledThreadPool(1)
                timer.scheduleAtFixedRate({
                    if (!binding.playProgressBar.isPressed) {
                        binding.playProgressBar.progress = mediaPlayer.currentPosition
                    }
                }, 10, 10, TimeUnit.MILLISECONDS)
            }
        }
        binding.imgWhatsApp.setOnClickListener {
            shareVideoWhatsApp()
        }
        binding.imgShare.setOnClickListener {
            shareVideoOther()
        }
    }

    @SuppressLint("SetTextI18n")
    fun createMediaPlayer(uri: Uri?) {
        mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
        try {
            mediaPlayer.setDataSource(applicationContext, uri!!)
            mediaPlayer.prepare()
            val millis: Int = mediaPlayer.duration
            val total_secs: Long = TimeUnit.SECONDS.convert(millis.toLong(), TimeUnit.MILLISECONDS)
            val mins: Long = TimeUnit.MINUTES.convert(total_secs, TimeUnit.SECONDS)
            val secs = total_secs - mins * 60
            val sec: String = if (secs <= 9) {
                "0$secs"
            } else {
                "$secs"
            }
            val min: String = if (mins <= 9) {
                "0$mins"
            } else {
                "$mins"
            }
            duration = "$min:$sec"
            binding.playProgressBar.max = millis
            binding.playProgressBar.progress = 0
            mediaPlayer.setOnCompletionListener { releaseMediaPlayer() }
        } catch (_: IOException) {
        }
        binding.tvVideoDuration.text = "00:00/$duration"
    }

    fun shareVideoWhatsApp() {
        val uri = uri
        val videoshare = Intent(Intent.ACTION_SEND)
        videoshare.type = "*/*"
        videoshare.setPackage("com.whatsapp")
        videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        videoshare.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(videoshare)
    }

    fun shareVideoOther() {
        val uri = uri
        val videoshare = Intent(Intent.ACTION_SEND)
        videoshare.type = "*/*"
        videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        videoshare.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(videoshare)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun releaseMediaPlayer() {
        binding.ivPlay.setImageDrawable(this.getDrawable(R.drawable.ic_play))
        binding.playProgressBar.progress = 0
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.videoView.isPlaying) {
            mediaPlayer.pause()
            timer.shutdown()
            binding.videoView.pause()
        }
        GalleryFileActivity.videoList1.clear()
        finish()
    }
}