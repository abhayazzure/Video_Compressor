package com.azzuresolutions.videocompressor.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.azzuresolutions.videocompressor.R
import com.azzuresolutions.videocompressor.common.Common
import com.azzuresolutions.videocompressor.databinding.ActivityVideoToAudioBinding
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
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
            GalleryFileActivity.videoList1[0].width.toString() + " X " + GalleryFileActivity.videoList1[0].height.toString()
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
                val total_secs = TimeUnit.SECONDS.convert(millis.toLong(), TimeUnit.MILLISECONDS)
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
                this,
                getRealPathFromURI(this, GalleryFileActivity.videoList1[0].uri),
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + "/Video Compressor" + "/$name.mp3",
                -1,
                -1,
                useAudio = true,
                useVideo = false,
                name
            )

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
    @SuppressLint("SetTextI18n")
    fun createMediaPlayer(uri: Uri?) {
        mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA).build()
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

    @SuppressLint("UseCompatLoadingForDrawables")
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

    private class AudioExtractor {
        private val DEFAULT_BUFFER_SIZE = 1 * 1024 * 1024
        private val TAG = "AudioExtractorDecoder"

        @SuppressLint("WrongConstant")
        fun genVideoUsingMuxer(
            context: Context,
            srcPath: String?,
            dstPath: String?,
            startMs: Int,
            endMs: Int,
            useAudio: Boolean,
            useVideo: Boolean,
            name: String?
        ) {
            // Set up MediaExtractor to read from the source.
            val extractor = MediaExtractor()
            extractor.setDataSource(srcPath!!)
            val trackCount = extractor.trackCount
            val mediaStorageDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "/Video Compressor"
            )

            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("App", "failed to create directory")
                }
            }
            // Set up MediaMuxer for the destination.
            val muxer = MediaMuxer(dstPath!!, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            // Set up the tracks and retrieve the max buffer size for selected
            // tracks.
            val indexMap = HashMap<Int, Int>(trackCount)
            var bufferSize = -1
            for (i in 0 until trackCount) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)
                var selectCurrentTrack = false
                if (mime!!.startsWith("audio/") && useAudio) {
                    selectCurrentTrack = true
                } else if (mime.startsWith("video/") && useVideo) {
                    selectCurrentTrack = true
                }
                if (selectCurrentTrack) {
                    extractor.selectTrack(i)
                    val dstIndex = muxer.addTrack(format)
                    indexMap[i] = dstIndex
                    if (format.containsKey(MediaFormat.KEY_MAX_INPUT_SIZE)) {
                        val newSize = format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
                        bufferSize = if (newSize > bufferSize) newSize else bufferSize
                    }
                }
            }
            if (bufferSize < 0) {
                bufferSize = DEFAULT_BUFFER_SIZE
            }
            // Set up the orientation and starting time for extractor.
            val retrieverSrc = MediaMetadataRetriever()
            retrieverSrc.setDataSource(srcPath)
            val degreesString =
                retrieverSrc.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
            if (degreesString != null) {
                val degrees = degreesString.toInt()
                if (degrees >= 0) {
                    muxer.setOrientationHint(degrees)
                }
            }
            if (startMs > 0) {
                extractor.seekTo((startMs * 1000).toLong(), MediaExtractor.SEEK_TO_CLOSEST_SYNC)
            }
            // Copy the samples from MediaExtractor to MediaMuxer. We will loop
            // for copying each sample and stop when we get to the end of the source
            // file or exceed the end time of the trimming.
            val offset = 0
            var trackIndex = -1
            val dstBuf: ByteBuffer = ByteBuffer.allocate(bufferSize)
            val bufferInfo = MediaCodec.BufferInfo()
            muxer.start()
            while (true) {
                bufferInfo.offset = offset
                bufferInfo.size = extractor.readSampleData(dstBuf, offset)
                if (bufferInfo.size < 0) {
                    Log.d(TAG, "Saw input EOS.")
                    bufferInfo.size = 0
                    break
                } else {
                    bufferInfo.presentationTimeUs = extractor.sampleTime
                    if (endMs > 0 && bufferInfo.presentationTimeUs > endMs * 1000) {
                        Log.d(TAG, "The current sample is over the trim end time.")
                        break
                    } else {
                        bufferInfo.flags = extractor.sampleFlags
                        trackIndex = extractor.sampleTrackIndex
                        muxer.writeSampleData(indexMap[trackIndex]!!, dstBuf, bufferInfo)
                        extractor.advance()
                    }
                }
            }
            muxer.stop()
            muxer.release()
            val intent = Intent(context, AudioPlayActivity::class.java)
            intent.putExtra("name", name)
            context.startActivity(intent)
            return
        }
    }
}
