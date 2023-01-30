package com.azzuresolutions.videocompressor.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.azzuresolutions.videocompressor.R
import com.azzuresolutions.videocompressor.adapter.ViewPagerAdapter
import com.azzuresolutions.videocompressor.databinding.ActivityGalleryFileBinding
import com.azzuresolutions.videocompressor.fragment.GalleryFragment
import com.azzuresolutions.videocompressor.fragment.LargeFragment
import com.azzuresolutions.videocompressor.fragment.SAFFragment

class GalleryFileActivity : AppCompatActivity() {

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
        addTabs(binding.viewPager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    private fun addTabs(viewPager: ViewPager) {
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, 0)
        adapter = viewPagerAdapter
        viewPagerAdapter.addFrag(GalleryFragment(), "VIDEO")
        adapter!!.addFrag(LargeFragment(), "Large Video")
        adapter!!.addFrag(SAFFragment(), "BROWSE")
        viewPager.adapter = adapter
    }
}