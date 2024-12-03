package app.c14220170.tasklist

import android.os.Parcel
import android.os.Parcelable

data class task(
    var title: String,
    var desc: String,
    var startDate: String,
    var status: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(desc)
        parcel.writeString(startDate)
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<task> {
        override fun createFromParcel(parcel: Parcel): task {
            return task(parcel)
        }

        override fun newArray(size: Int): Array<task?> {
            return arrayOfNulls(size)
        }
    }
}
