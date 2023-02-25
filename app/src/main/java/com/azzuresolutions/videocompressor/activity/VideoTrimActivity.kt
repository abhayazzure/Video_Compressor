package com.azzuresolutions.videocompressor.activity

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.azzuresolutions.videocompressor.databinding.ActivityVideoTrimBinding
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener
import java.io.*
import java.net.URI
import java.util.*

class VideoTrimActivity : AppCompatActivity(), OnTrimVideoListener {
    private lateinit var binding: ActivityVideoTrimBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoTrimBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.timeLine.setMaxDuration(60)
        binding.timeLine.setOnTrimVideoListener(this)
        binding.timeLine.setOnTrimVideoListener(this)
        binding.timeLine.setDestinationPath("/storage/emulated/0/Download/Video Compressor/")
        binding.timeLine.setVideoURI(Uri.parse(getRealPathFromURI(GalleryFileActivity.videoList1[0].uri)))
    }

    open fun getRealPathFromURI(contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Audio.Media.DATA)
        val cursor: Cursor = managedQuery(contentUri, proj, null, null, null)
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    override fun onTrimStarted() {
    }

    override fun getResult(uri: Uri?) {
//        val uuid: UUID = UUID.randomUUID()
//        saveVideoToInternalStorage(uuid, uri.toString())
    }

    override fun cancelAction() {
    }

    override fun onError(message: String?) {
    }

    private fun saveVideoToInternalStorage(fileName: UUID, filePath: String) {
        try {
            val currentFile = File(URI.create(filePath))
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
}