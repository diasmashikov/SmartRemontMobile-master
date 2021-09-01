package kz.cheesenology.smartremontmobile.util.apkdownload

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

class Download : Parcelable {

    var progress: Int = 0
    var currentFileSize: Int = 0
    var totalFileSize: Int = 0

    internal constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(progress)
        dest.writeInt(currentFileSize)
        dest.writeInt(totalFileSize)
    }

    private constructor(`in`: Parcel) {
        progress = `in`.readInt()
        currentFileSize = `in`.readInt()
        totalFileSize = `in`.readInt()
    }

    companion object {

        @SuppressLint("ParcelCreator")
        val CREATOR: Parcelable.Creator<Download> = object : Parcelable.Creator<Download> {
            override fun createFromParcel(`in`: Parcel): Download {
                return Download(`in`)
            }

            override fun newArray(size: Int): Array<Download?> {
                return arrayOfNulls(size)
            }
        }
    }
}