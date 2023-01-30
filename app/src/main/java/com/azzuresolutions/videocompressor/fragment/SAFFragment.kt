package com.azzuresolutions.videocompressor.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.azzuresolutions.videocompressor.databinding.FragmentSAFBinding
import com.azzuresolutions.videocompressor.model.Video

class SAFFragment : Fragment() {

    private lateinit var binding: FragmentSAFBinding
    var mArrayUri: ArrayList<Video>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSAFBinding.inflate(layoutInflater, container, false)
        buttonClick()
        return binding.root
    }

    private fun buttonClick() {
        binding.cardBrowse.setOnClickListener {
            val intent = Intent()
            intent.type = "video/*"
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            intent.action = "android.intent.action.OPEN_DOCUMENT"
            this@SAFFragment.startActivityForResult(
                Intent.createChooser(intent, "Select Picture"), 1001
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && data != null) {
            openCompressionActivity(data)
        }
    }

    @SuppressLint("WrongConstant")
    private fun openCompressionActivity(intent: Intent) {
        val flags = intent.flags and 3
        if (intent.clipData != null) {
            mArrayUri = ArrayList()
            val clipData = intent.clipData
            for (i in 0 until clipData!!.itemCount) {
                val uri = clipData.getItemAt(i).uri
                requireActivity().contentResolver.takePersistableUriPermission(uri, flags)
                (this.mArrayUri as ArrayList<Video>).add(Video(uri))
            }
        } else if (intent.data != null) {
            mArrayUri = ArrayList()
            val data = intent.data
            requireActivity().contentResolver.takePersistableUriPermission(data!!, flags)
            (mArrayUri as ArrayList<Video>).add(Video(data))
        }
//        val intent2 = Intent(activity, CompressImageActivity::class.java)
//        intent2.putParcelableArrayListExtra("images", mArrayUri as ArrayList<Image>?)
//        startActivity(intent2)
//        requireActivity().finish()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}