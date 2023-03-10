package com.azzuresolutions.videocompressor.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.azzuresolutions.videocompressor.adapter.ViewPagerAdapter
import com.azzuresolutions.videocompressor.databinding.ActivityMyCreationBinding
import com.azzuresolutions.videocompressor.fragment.AudioFragment
import com.azzuresolutions.videocompressor.fragment.VideoFragment
import com.google.android.material.tabs.TabLayout

class MyCreationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyCreationBinding
    var audioFragment: AudioFragment? = null
    var videoFragment: VideoFragment? = null
    private var adapter: ViewPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.audioFragment = AudioFragment()
        this.videoFragment = VideoFragment()
        addTabs(binding.viewPager)

        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab!!.position == 1) {
                    AudioFragment.click = true
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                if (tab!!.position == 1) {
                    AudioFragment.click = false
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                if (tab!!.position == 1) {
                    AudioFragment.click = false
                }
            }
        })
    }

    private fun addTabs(viewPager: ViewPager) {
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, 0)
        adapter = viewPagerAdapter
        viewPagerAdapter.addFrag(VideoFragment(), "VIDEO")
        adapter!!.addFrag(AudioFragment(), "Audio")
//        adapter!!.addFrag(SAFFragment(), "BROWSE")
        viewPager.adapter = adapter
    }
}