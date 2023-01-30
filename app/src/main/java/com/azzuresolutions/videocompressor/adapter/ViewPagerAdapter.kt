package com.azzuresolutions.videocompressor.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter constructor(supportFragmentManager: FragmentManager, behavior: Int) :
    FragmentPagerAdapter(supportFragmentManager, behavior) {

    private var fragmentList: ArrayList<Fragment> = ArrayList()
    private var fragmentTitleList: ArrayList<String> = ArrayList()

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    fun addFrag(fragment: Fragment, str: String) {
        fragmentList.add(fragment)
        fragmentTitleList.add(str)
    }

    override fun getPageTitle(i: Int): CharSequence {
        return fragmentTitleList[i]
    }
}