package com.azzuresolutions.videocompressor.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.azzuresolutions.videocompressor.R
import com.azzuresolutions.videocompressor.databinding.ActivityAudioPlayBinding
import com.bumptech.glide.Glide
import java.io.File
import java.io.IOException
import java.text.DecimalFormat
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class AudioPlayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioPlayBinding
    var fileName: String? = null
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var timer: ScheduledExecutorService
    var duration: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fileName = intent.getStringExtra("name") + ".mp3"
        dataCall()
        buttonClick()
    }

    private var file: File? = null
    private fun dataCall() {
        Glide.with(this).load(GalleryFileActivity.videoList1[0].uri).into(binding.imagePhoto)
        val completePath: String =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Video Compressor/" + fileName)
                .toString()
        file = File(completePath)
        val myUri1: Uri = Uri.fromFile(file)
        createMediaPlayer(myUri1)

        binding.tvFileName.text = fileName
        binding.tvFileSize.text = getStringSizeLengthFile(file!!.length())
    }

    private fun getStringSizeLengthFile(size: Long): String {
        val df = DecimalFormat("0.00")
        val sizeKb = 1024.0f
        val sizeMb = sizeKb * sizeKb
        val sizeGb = sizeMb * sizeKb
        val sizeTerra = sizeGb * sizeKb
        if (size < sizeMb) return df.format((size / sizeKb).toDouble()) + " Kb" else if (size < sizeGb) return df.format(
            (size / sizeMb).toDouble()
        ) + " Mb" else if (size < sizeTerra) return df.format((size / sizeGb).toDouble()) + " Gb"
        return ""
    }

    @SuppressLint("SetTextI18n")
    fun createMediaPlayer(uri: Uri?) {
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
        try {
            mediaPlayer!!.setDataSource(applicationContext, uri!!)
            mediaPlayer!!.prepare()
            val millis: Int = mediaPlayer!!.duration
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
            binding.seekAudioPlay.max = millis
            binding.seekAudioPlay.progress = 0
            mediaPlayer!!.setOnCompletionListener { releaseMediaPlayer() }
        } catch (_: IOException) {
        }
        binding.tvAudioDuration.text = "00:00/$duration"
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun releaseMediaPlayer() {
        binding.ivAudioPlay.setImageDrawable(this.getDrawable(R.drawable.ic_play))
        binding.seekAudioPlay.progress = 0
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun buttonClick() {

        binding.btnHome.setOnClickListener {
            finish()
        }

        binding.btnWhatsapp.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(
                Intent.EXTRA_STREAM, Uri.fromFile(
                    File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/" + fileName)
                            .toString()
                    )
                )
            )
            intent.type = "audio/*"
            val builder = VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
            startActivity(Intent.createChooser(intent, "Share Audio Abd Alazez..."))
        }

        binding.seekAudioPlay.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val millis = mediaPlayer!!.currentPosition
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
                binding.tvAudioDuration.text = "$min:$sec / $duration"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                mediaPlayer!!.seekTo(binding.seekAudioPlay.progress)
            }
        })

        binding.btnBack.setOnClickListener {
            mediaPlayer!!.stop()
            finish()
        }

        binding.ivAudioPlay.setOnClickListener {
            binding.ivAudioPlay.setImageDrawable(getDrawable(R.drawable.ic_pause))
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.pause()
                timer.shutdown()
                binding.ivAudioPlay.setImageDrawable(getDrawable(R.drawable.ic_play))
            } else {
                mediaPlayer!!.start()
                binding.ivAudioPlay.setImageDrawable(getDrawable(R.drawable.ic_pause))
                timer = Executors.newScheduledThreadPool(1)
                timer.scheduleAtFixedRate({
                    if (!binding.seekAudioPlay.isPressed) {
                        binding.seekAudioPlay.progress = mediaPlayer!!.currentPosition
                    }
                }, 10, 10, TimeUnit.MILLISECONDS)
            }
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        GalleryFileActivity.videoList1.clear()
        if (mediaPlayer !!.isPlaying) {
            mediaPlayer!!.stop()
        }
        finish()
    }
}