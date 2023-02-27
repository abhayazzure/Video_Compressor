package com.azzuresolutions.videocompressor.activity

import android.annotation.SuppressLint
import android.database.Cursor
import android.graphics.Movie
import android.media.AudioAttributes
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.azzuresolutions.videocompressor.R
import com.azzuresolutions.videocompressor.databinding.ActivityVideoPlayBackSpeedChangeBinding
import com.google.android.gms.tagmanager.Container
import com.google.ar.core.Track
import life.knowledge4.videotrimmer.utils.BackgroundExecutor
import java.io.*
import java.text.SimpleDateFormat
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
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        binding.rlSave.setOnClickListener {
            saveVideoToInternalStorage(binding.tv1.text.toString())
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
                binding.videoView.seekTo(binding.playProgressBar.progress)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                mediaPlayer.seekTo(binding.playProgressBar.progress)
                binding.videoView.seekTo(binding.playProgressBar.progress)
            }
        })

        var progres: Float? = 1f
        binding.run {
            videoView.setOnPreparedListener {
                if (binding.rb1.isSelected) {
                    progres = 1.5f
                } else if (binding.rb2.isSelected) {
                    progres = 2.0f
                } else if (binding.rb3.isSelected) {
                    progres = 2.5f
                }
                val myPlayBackParams = PlaybackParams()
                myPlayBackParams.speed = progres!! //you can set speed here
                it.playbackParams = myPlayBackParams
            }
        }

    }

    fun stripExtension(s: String?): String? {
        return if (s != null && s.lastIndexOf(".") > 0) s.substring(0, s.lastIndexOf(".")) else s
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    //    private fun saveVi
    open fun getRealPathFromURI(contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Audio.Media.DATA)
        val cursor: Cursor = managedQuery(contentUri, proj, null, null, null)
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    private fun onSaveClicked() {
        binding.videoView.visibility = View.VISIBLE
        binding.videoView.pause()
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(this, GalleryFileActivity.videoList1[0].uri)
        val METADATA_KEY_DURATION =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!
                .toLong()
        val file: File = File(GalleryFileActivity.videoList1[0].uri.path!!)

//        notify that video trimming started
//        if (mOnTrimVideoListener != null) mOnTrimVideoListener.onTrimStarted()
        BackgroundExecutor.execute(
            object : BackgroundExecutor.Task("", 0L, "") {
                override fun execute() {
                    try {
                        startTrim(
                            binding.tv1.text.toString(),
                            file,
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Video Compressor/")
                                .toString(),
                            0,
                            100000,
                        )
                    } catch (e: Throwable) {
                        Thread.getDefaultUncaughtExceptionHandler()
                            .uncaughtException(Thread.currentThread(), e)
                    }
                }
            }
        )
    }

    @Throws(IOException::class)
    fun startTrim(
        filename: String,
        src: File,
        dst: String,
        startMs: Long,
        endMs: Long,
    ) {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val fileName = "$filename.mp4"
        val filePath = dst + fileName
        val file = File(filePath)
        file.parentFile.mkdirs()
//        genVideoUsingMp4Parser(src, file, startMs, endMs)
    }

//    @Throws(IOException::class)
//    private fun genVideoUsingMp4Parser(
//        src: File,
//        dst: File,
//        startMs: Long,
//        endMs: Long
//    ) {
//        // NOTE: Switched to using FileDataSourceViaHeapImpl since it does not use memory mapping (VM).
//        // Otherwise we get OOM with large movie files.
//        val movie: Movie = MovieCreator.build(FileDataSourceViaHeapImpl(src.absolutePath))
//        val tracks: List<Track> = movie.getTracks()
//        movie.setTracks(LinkedList<Track>())
//        // remove all tracks we will create new tracks from the old
//        var startTime1 = (startMs / 1000).toDouble()
//        var endTime1 = (endMs / 1000).toDouble()
//        var timeCorrected = false
//
//        // Here we try to find a track that has sync samples. Since we can only start decoding
//        // at such a sample we SHOULD make sure that the start of the new fragment is exactly
//        // such a frame
//        for (track in tracks) {
//            if (track.getSyncSamples() != null && track.getSyncSamples().size > 0) {
//                if (timeCorrected) {
//                    // This exception here could be a false positive in case we have multiple tracks
//                    // with sync samples at exactly the same positions. E.g. a single movie containing
//                    // multiple qualities of the same video (Microsoft Smooth Streaming file)
//                    throw RuntimeException("The startTime has already been corrected by another track with SyncSample. Not Supported.")
//                }
//                startTime1 = correctTimeToSyncSample(track, startTime1, false)
//                endTime1 = correctTimeToSyncSample(track, endTime1, true)
//                timeCorrected = true
//            }
//        }
//        for (track in tracks) {
//            var currentSample: Long = 0
//            var currentTime = 0.0
//            var lastTime = -1.0
//            var startSample1: Long = -1
//            var endSample1: Long = -1
//            for (i in track.getSampleDurations().indices) {
//                val delta: Long = track.getSampleDurations().get(i)
//                if (currentTime > lastTime && currentTime <= startTime1) {
//                    // current sample is still before the new starttime
//                    startSample1 = currentSample
//                }
//                if (currentTime > lastTime && currentTime <= endTime1) {
//                    // current sample is after the new start time and still before the new endtime
//                    endSample1 = currentSample
//                }
//                lastTime = currentTime
//                currentTime += delta.toDouble() / track.getTrackMetaData().getTimescale().toDouble()
//                currentSample++
//            }
//            movie.addTrack(AppendTrack(CroppedTrack(track, startSample1, endSample1)))
//        }
//        dst.parentFile.mkdirs()
//        if (!dst.exists()) {
//            dst.createNewFile()
//        }
//        val out: Container = DefaultMp4Builder().build(movie)
//        val fos = FileOutputStream(dst)
//        val fc = fos.channel
//        out.writeContainer(fc)
//        fc.close()
//        fos.close()
//    }

//    private fun correctTimeToSyncSample(
//        track: Track,
//        cutHere: Double,
//        next: Boolean
//    ): Double {
//        val timeOfSyncSamples = DoubleArray(track.getSyncSamples().size)
//        var currentSample: Long = 0
//        var currentTime = 0.0
//        for (i in track.getSampleDurations().indices) {
//            val delta: Long = track.getSampleDurations().get(i)
//            if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
//                // samples always start with 1 but we start with zero therefore +1
//                timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(), currentSample + 1)] =
//                    currentTime
//            }
//            currentTime += delta.toDouble() / track.getTrackMetaData().getTimescale().toDouble()
//            currentSample++
//        }
//        var previous = 0.0
//        for (timeOfSyncSample in timeOfSyncSamples) {
//            if (timeOfSyncSample > cutHere) {
//                return if (next) {
//                    timeOfSyncSample
//                } else {
//                    previous
//                }
//            }
//            previous = timeOfSyncSample
//        }
//        return timeOfSyncSamples[timeOfSyncSamples.size - 1]
//    }

    private fun saveVideoToInternalStorage(fileName: String) {
        try {
            val currentFile = File(getRealPathFromURI(GalleryFileActivity.videoList1[0].uri)!!)
            val loc = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val directory = File(loc.absolutePath + "/Video Compressor")
            directory.mkdir()
            val fileName = String.format("$fileName.mp4")
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
            e.printStackTrace()
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}