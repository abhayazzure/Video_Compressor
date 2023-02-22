package com.azzuresolutions.videocompressor.activity

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.azzuresolutions.videocompressor.R
import com.azzuresolutions.videocompressor.databinding.ActivityVideoPlayBackSpeedChangeBinding
import java.io.*
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class VideoPlayBackSpeedChangeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoPlayBackSpeedChangeBinding
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var timer: ScheduledExecutorService
    var duration: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayBackSpeedChangeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        videoLoad()
        buttonClick()
    }

    private fun videoLoad() {
        val uri: Uri = Uri.parse(GalleryFileActivity.videoList1[0].uri.toString())
        binding.videoView.setVideoURI(uri)
        binding.videoView.requestFocus()
        createMediaPlayer(uri)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun buttonClick() {
//        binding.ivPlay.setOnClickListener {
//            binding.listItemVideoClickerSmall.visibility = View.GONE
//            binding.cardThumb.visibility = View.GONE
//            binding.ivPlay.visibility = View.GONE
//            binding.videoView.start()
//            binding.imgPlay.setImageDrawable(getDrawable(R.drawable.ic_pause))
//            if (mediaPlayer.isPlaying) {
//                mediaPlayer.pause()
//                timer.shutdown()
//                binding.imgPlay.setImageDrawable(getDrawable(R.drawable.ic_play))
//            } else {
//                mediaPlayer.start()
//                binding.videoView.start()
//                binding.imgPlay.setImageDrawable(getDrawable(R.drawable.ic_pause))
//                timer = Executors.newScheduledThreadPool(1)
//                timer.scheduleAtFixedRate({
//                    if (!binding.playProgressBar.isPressed) {
//                        binding.playProgressBar.progress = mediaPlayer.currentPosition
//                        binding.videoView.seekTo(mediaPlayer.currentPosition.toLong())
//                    }
//                }, 10, 10, TimeUnit.MILLISECONDS)
//            }
//        }
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
            binding.etFastSaveName.text =
                stripExtension(GalleryFileActivity.videoList1[0].name)!!.toEditable()
            binding.tvSavePath.text =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
                    .toString()
        }
        binding.seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.videoView.seekTo(1)
                mediaPlayer.playbackParams = mediaPlayer.playbackParams.apply {
                    speed = progress.toFloat()
                }
                binding.videoView.start()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                TODO("Not yet implemented")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                TODO("Not yet implemented")
            }
        })
        binding.rlSave.setOnClickListener {
            saveVideoToInternalStorage( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath)
            SaveVideo(GalleryFileActivity.videoList1[0].uri)
        }

        binding.playProgressBar.setOnSeekBarChangeListener(object :
            OnSeekBarChangeListener {
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
        var progres: Int? = 0
        binding.run {
            seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    progres = progress
                    progres = progres!! / 100
                    binding.txtStartDuration.text = progress.toString()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    var myPlayBackParams: PlaybackParams? = null
                    myPlayBackParams = PlaybackParams()
                    myPlayBackParams.speed = progres!!.toFloat() //you can set speed here
                    mediaPlayer.playbackParams = myPlayBackParams
                }
            })
            videoView.setOnPreparedListener {
                //works only from api 23

            }
        }

    }
    private fun SaveVideo(f: Uri) {


        //I don't know what data type I have to pass in the methods parameters
        val root =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath
        val myDir = File("$root/Push Videos")
        if (!myDir.exists()) {
            myDir.mkdirs()
        }
        val generator = Random()
        var n = 10000
        n = generator.nextInt(n)
        val fname = "Video-$n.mp4"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            Toast.makeText(this, "PUSH Video Saved", Toast.LENGTH_LONG).show()
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stripExtension(s: String?): String? {
        return if (s != null && s.lastIndexOf(".") > 0) s.substring(0, s.lastIndexOf(".")) else s
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    //    private fun saveVi
    private fun saveVideoToInternalStorage(filePath: String) {
        val uuid: UUID = UUID.randomUUID()
        try {
            val currentFile = File(filePath)
            val loc = Environment.getExternalStorageDirectory().absolutePath
            val directory = File(loc, "Video Compressor.mp4")
            directory.parentFile!!.createNewFile()
            val fileName = String.format("$uuid.mp4")
            val newfile = File(directory, fileName)
            if (currentFile.exists()) {
                val inputStream: InputStream = FileInputStream(currentFile)
                val outputStream: OutputStream = FileOutputStream(newfile)
                val buf = ByteArray(1024)
                var len: Int
                while (inputStream.read(buf).also { len = it } > 0) {
                    outputStream.write(buf, 0, len)
                }
                outputStream.flush()
                inputStream.close()
                outputStream.close()
                Toast.makeText(applicationContext, "Video has just saved!!", Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Video has failed for saving!!",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: java.lang.Exception) {
            Log.e(
                "",
                e.toString()
            )
        }
    }

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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}