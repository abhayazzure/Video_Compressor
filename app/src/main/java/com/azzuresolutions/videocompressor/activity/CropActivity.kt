package com.azzuresolutions.videocompressor.activity

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Point
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Size
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.azzuresolutions.videocompressor.ClearColorItem
import com.azzuresolutions.videocompressor.GesturePlayerTextureView
import com.azzuresolutions.videocompressor.SceneCropColor
import com.azzuresolutions.videocompressor.databinding.ActivityCropBinding
import com.daasuu.mp4compose.FillMode
import com.daasuu.mp4compose.FillModeCustomItem
import com.daasuu.mp4compose.composer.Mp4Composer
import com.daasuu.mp4compose.filter.GlFilter
import java.io.File

class CropActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCropBinding

    private val CROP_REQUEST = 200
    private lateinit var playerTextureView: GesturePlayerTextureView
    private var baseWidthSize = 0f
    private lateinit var srcPath: String
    private val sceneCropColor: SceneCropColor = SceneCropColor.BLACK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCropBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        srcPath = getRealPathFromURI(GalleryFileActivity.videoList1[0].uri)!!
        initPlayer()
        buttonClick()
    }

    private fun buttonClick() {
        binding.btnCodec.setOnClickListener {
            binding.rlSaveScreen.visibility = View.VISIBLE
        }
        binding.btnBack1.setOnClickListener {
            binding.rlSaveScreen.visibility = View.GONE
        }
        binding.rlSave.setOnClickListener {
            codec()
        }
    }

    private fun initPlayer() {
        playerTextureView = GesturePlayerTextureView(
            applicationContext,
            srcPath
        )
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams.gravity = Gravity.CENTER
        playerTextureView.layoutParams = layoutParams
        baseWidthSize =
            (getWindowHeight(this) - dp2px(
                192f,
                this
            )) / 16f * 9
        playerTextureView.setBaseWidthSize(baseWidthSize)
        binding.layoutCropChange.addView(playerTextureView, 1)
    }


    override fun onResume() {
        super.onResume()
        playerTextureView.play()
    }

    override fun onPause() {
        super.onPause()
        playerTextureView.pause()
    }

    private fun codec() {
        binding.layoutCodec.visibility = View.VISIBLE
        binding.btnCodec.isEnabled = false
        binding.btnRotate.isEnabled = false
        binding.btnColorChange.isEnabled = false
        binding.progressBar.max = 100
        val resolution = getVideoResolution(srcPath)
        val fillModeCustomItem = FillModeCustomItem(
            playerTextureView.scaleX,
            playerTextureView.rotation,
            playerTextureView.translationX / baseWidthSize * 2f,
            playerTextureView.translationY / (baseWidthSize / 9f * 16) * 2f,
            resolution!!.width.toFloat(),
            resolution.height
                .toFloat()
        )

        val videoPath = getVideoFilePath()

        binding.startPlayMovie.isEnabled = false

        binding.startPlayMovie.setOnClickListener {
            val uri = Uri.parse(videoPath)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setDataAndType(uri, "video/mp4")
            startActivity(intent)
        }

        val glFilter = GlFilter()
        val clearColorItem: ClearColorItem = sceneCropColor.clearColorItem

        glFilter.setClearColor(
            clearColorItem.red,
            clearColorItem.green,
            clearColorItem.blue,
            clearColorItem.alpha
        )

        Mp4Composer(srcPath, videoPath)
            .size(720, 1280)
            .filter(glFilter)
            .fillMode(FillMode.CUSTOM)
            .customFillMode(fillModeCustomItem)
            .listener(object : Mp4Composer.Listener {
                override fun onProgress(progress: Double) {
                    runOnUiThread { binding.progressBar.progress = (progress * 100).toInt() }
                }

                override fun onCompleted() {
                    exportMp4ToGallery(applicationContext, videoPath)
                    runOnUiThread {
                        binding.progressBar.progress = 100
                        binding.startPlayMovie.isEnabled = true
                        binding.close.visibility = View.VISIBLE

                        binding.close.setOnClickListener {
                            binding.layoutCodec.visibility = View.GONE
                            binding.close.isEnabled = true
                            binding.btnRotate.isEnabled = true
                            binding.btnColorChange.isEnabled = true
                        }
                        finish()
                    }
                }

                override fun onCanceled() {}
                override fun onCurrentWrittenVideoTime(timeUs: Long) {}
                override fun onFailed(exception: Exception) {
                }
            })
            .start()
    }

    private fun getWindowHeight(context: Context): Int {
        val disp = (context.getSystemService(WINDOW_SERVICE) as WindowManager).defaultDisplay
        val size = Point()
        disp.getSize(size)
        return size.y
    }

    private fun getRealPathFromURI(contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Video.Media.DATA)
        val cursor: Cursor = managedQuery(contentUri, proj, null, null, null)
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    private fun dp2px(dpValue: Float, context: Context): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    private fun getAndroidMoviesFolder(): File {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    }

    fun getVideoFilePath(): String {
        return getAndroidMoviesFolder().absolutePath + "/Video Compressor/" + binding.etCropSaveName.text + ".mp4"
    }

    fun getVideoResolution(path: String?): Size? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        val width = Integer.valueOf(
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)!!
        )
        val height = Integer.valueOf(
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)!!
        )
        retriever.release()
        val rotation: Int = getVideoRotation(path)
        return if (rotation == 90 || rotation == 270) {
            Size(height, width)
        } else Size(width, height)
    }

    private fun getVideoRotation(videoFilePath: String?): Int {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(videoFilePath)
        val orientation = mediaMetadataRetriever.extractMetadata(
            MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION
        )
        return Integer.valueOf(orientation)
    }

    fun exportMp4ToGallery(context: Context, filePath: String) {
        val values = ContentValues(2)
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        values.put(MediaStore.Video.Media.DATA, filePath)
        context.contentResolver.insert(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            values
        )
        context.sendBroadcast(
            Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://$filePath")
            )
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        srcPath = null.toString()
    }
}