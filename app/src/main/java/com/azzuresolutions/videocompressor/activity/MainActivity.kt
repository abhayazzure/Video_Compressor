package com.azzuresolutions.videocompressor.activity

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.azzuresolutions.videocompressor.adapter.VideoAdapter
import com.azzuresolutions.videocompressor.databinding.ActivityMainBinding
import com.azzuresolutions.videocompressor.model.VideoModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var READ_AND_WRITE =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        buttonClick()
    }

    private fun buttonClick() {
        binding.btnVideocrop.setOnClickListener {
            requestPermissions(READ_AND_WRITE, 1)
        }
        binding.btnVideotrim.setOnClickListener {
            requestPermissions(READ_AND_WRITE, 2)
        }
        binding.btnVideoslowmotion.setOnClickListener {
            requestPermissions(READ_AND_WRITE, 3)
        }
        binding.btnVideofastmotion.setOnClickListener {
            requestPermissions(READ_AND_WRITE, 4)
        }
        binding.btnVideotoaudio.setOnClickListener {
            requestPermissions(READ_AND_WRITE, 5)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                checkWritePermission(grantResults, "Crop")
            }
            2 -> {
                checkWritePermission(grantResults, "Trim")
            }
            3 -> {
                checkWritePermission(grantResults, "Slow")
            }
            4 -> {
                checkWritePermission(grantResults, "Fast")
            }
            5 -> {
                checkWritePermission(grantResults,"Convert to Mp3")
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun checkWritePermission(iArr: IntArray, name: String) {
        if (iArr.isNotEmpty()) {
            var z = false
            if (iArr[0] == 0 || iArr[1] == 0) {
                z = true
            }
            if (z) {
                startActivity(
                    Intent(
                        this,
                        GalleryFileActivity::class.java
                    ).putExtra("name", name)
                )
            } else if (Build.VERSION.SDK_INT >= 25 && shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_VIDEO)) {
                if (Build.VERSION.SDK_INT >= 25) {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.POST_NOTIFICATIONS
                        ), 1009
                    )
                }
            }
        }
    }
}