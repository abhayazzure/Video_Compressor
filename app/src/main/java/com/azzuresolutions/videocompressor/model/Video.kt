package com.azzuresolutions.videocompressor.model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

class Video(
    var imageName: String? = null,
    var imageType: String? = null,
    var imageUri: Uri? = null
) : Parcelable {

    constructor(parcel: Parcel) : this() {
        imageUri = parcel.readParcelable<Parcelable>(Uri::class.java.classLoader) as Uri?
        imageName = parcel.readString()
        imageType = parcel.readString()
    }

    constructor(uri: Uri?) : this() {
        imageUri = uri
    }

    constructor(uri: Uri?, str: String?) : this() {
        imageUri = uri
        imageName = str
    }

    constructor(uri: Uri?, str: String?, str2: String?) : this() {
        imageUri = uri
        imageName = str
        imageType = str2
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(imageUri, flags)
        dest.writeString(imageName)
        dest.writeString(imageType)
    }

    companion object CREATOR : Parcelable.Creator<Video> {

        override fun createFromParcel(parcel: Parcel): Video {
            return Video(parcel)
        }

        override fun newArray(size: Int): Array<Video?> {
            return arrayOfNulls(size)
        }
    }
}