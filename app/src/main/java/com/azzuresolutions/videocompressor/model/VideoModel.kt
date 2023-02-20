package com.azzuresolutions.videocompressor.model

import android.net.Uri

class VideoModel(
    val uri: Uri,
    val name: String,
    val duration: Int,
    val width:Int,
    val height:Int,
    val size: Int,
    var isSelect: Boolean
) {
}