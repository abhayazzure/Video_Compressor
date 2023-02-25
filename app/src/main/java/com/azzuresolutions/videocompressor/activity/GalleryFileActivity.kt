package com.azzuresolutions.videocompressor.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.media.*
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.azzuresolutions.videocompressor.adapter.ViewPagerAdapter
import com.azzuresolutions.videocompressor.databinding.ActivityGalleryFileBinding
import com.azzuresolutions.videocompressor.fragment.GalleryFragment
import com.azzuresolutions.videocompressor.fragment.LargeFragment
import com.azzuresolutions.videocompressor.fragment.SAFFragment
import com.azzuresolutions.videocompressor.model.VideoModel
import java.io.Serializable
import java.nio.ByteBuffer


class GalleryFileActivity : AppCompatActivity() {
    companion object {
        lateinit var videoList1: ArrayList<VideoModel>
    }

    private lateinit var binding: ActivityGalleryFileBinding
    var galleryFragment: GalleryFragment? = null
    var largeFragment: LargeFragment? = null
    private var adapter: ViewPagerAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryFileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.galleryFragment = GalleryFragment()
        this.largeFragment = LargeFragment()
        binding.acbNext.text = intent.getStringExtra("name")
        addTabs(binding.viewPager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        buttonClick()
    }

    private fun buttonClick() {
        binding.acbNext.setOnClickListener {
            if (intent.getStringExtra("name") == "Convert to Mp3") {
                startActivity(
                    Intent(this, VideoToAudioActivity::class.java)
                )
                finish()
            } else if (intent.getStringExtra("name") == "Crop") {
                startActivity(
                    Intent(this, CropActivity::class.java)
                )
                finish()
            } else if (intent.getStringExtra("name") == "Fast") {
                startActivity(
                    Intent(this, VideoPlayBackSpeedChangeActivity::class.java)
                )
                finish()
            } else if (intent.getStringExtra("name") == "Trim") {
                startActivity(
                    Intent(this, VideoTrimActivity::class.java)
                )
                finish()
            }
        }
    }

    private fun addTabs(viewPager: ViewPager) {
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, 0)
        adapter = viewPagerAdapter
        viewPagerAdapter.addFrag(GalleryFragment(), "VIDEO")
        adapter!!.addFrag(LargeFragment(), "Large Video")
//        adapter!!.addFrag(SAFFragment(), "BROWSE")
        viewPager.adapter = adapter
    }

}