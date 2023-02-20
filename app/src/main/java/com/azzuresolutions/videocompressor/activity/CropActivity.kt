package com.azzuresolutions.videocompressor.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.azzuresolutions.videocompressor.R
import com.azzuresolutions.videocompressor.databinding.ActivityCropBinding

class CropActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCropBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCropBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}