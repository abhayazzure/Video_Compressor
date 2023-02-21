package com.azzuresolutions.videocompressor.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.azzuresolutions.videocompressor.R
import com.azzuresolutions.videocompressor.common.AudioExtractor
import com.azzuresolutions.videocompressor.common.Common
import com.azzuresolutions.videocompressor.databinding.ActivityVideoToAudioBinding
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class VideoToAudioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoToAudioBinding
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var timer: ScheduledExecutorService
    var duration: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoToAudioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        videoLoad()
        buttonClick()
    }

    @SuppressLint("SetTextI18n")
    private fun videoLoad() {
        binding.tvFileName.text = GalleryFileActivity.videoList1[0].name
        binding.tvFileSize.text =
            Common.formatTime(GalleryFileActivity.videoList1[0].duration.toLong(), this)
        binding.tvVideoResolution.text =
            GalleryFileActivity.videoList1[0].width.toString() + " X " +
                    GalleryFileActivity.videoList1[0].height.toString()
        val uri: Uri = Uri.parse(GalleryFileActivity.videoList1[0].uri.toString())
        binding.videoView.setVideoURI(uri)
        binding.videoView.requestFocus()
        createMediaPlayer(uri)

//        binding.videoView.setOnPreparedListener {
//            binding.playProgressBar.max
//        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun buttonClick() {
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
                binding.videoView.seekTo(binding.playProgressBar.progress.toLong())
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                mediaPlayer.seekTo(binding.playProgressBar.progress)
                binding.videoView.seekTo(binding.playProgressBar.progress.toLong())
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

        binding.ryBottom.setOnClickListener {
            binding.rlSaveScreen.visibility = View.VISIBLE

            binding.etAudioSaveName.text =
                stripExtension(GalleryFileActivity.videoList1[0].name)!!.toEditable()
            binding.tvSavePath.text =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString()
        }
        binding.btnBack1.setOnClickListener {
            binding.rlSaveScreen.visibility = View.GONE
        }

        binding.rlSave.setOnClickListener {
            val name = binding.etAudioSaveName.text.trim().toString()
            AudioExtractor().genVideoUsingMuxer(
                getRealPathFromURI(this, GalleryFileActivity.videoList1[0].uri),
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/$name.mp3")
                    .toString(),
                -1,
                -1,
                useAudio = true,
                useVideo = false
            )
            Handler().postDelayed({
                val intent = Intent(this, AudioPlayActivity::class.java)
                intent.putExtra("name", name)
                startActivity(intent)
                finish()
            }, 2000)

        }

    //        binding.ivPlay.setOnClickListener {
    //            binding.videoView.start()
    //            binding.ryThumbContainer.visibility = View.GONE
    //            binding.ivPlay.visibility = View.GONE
    //            binding.imgPlay.setImageDrawable(getDrawable(R.drawable.ic_pause))
    //        }
    }

    fun stripExtension(s: String?): String? {
        return if (s != null && s.lastIndexOf(".") > 0) s.substring(0, s.lastIndexOf(".")) else s
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
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

    private fun releaseMediaPlayer() {
        binding.ivPlay.setImageDrawable(this.getDrawable(R.drawable.ic_play))
        binding.playProgressBar.progress = 0
    }

    private fun getRealPathFromURI(context: Context, contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Video.Media.DATA)
            cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
            val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } finally {
            cursor?.close()
        }
    }
}