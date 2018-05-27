package com.evernote.android.state.test

import android.os.Parcel
import android.os.Parcelable
import com.evernote.android.state.StateReflection

/**
 * @author rwondratschek
 */
class TestKotlinPrivateInnerClass {
    @StateReflection
    private var inner= Inner.A

    @StateReflection
    private var parcelable = ParcelableImpl(1)

    @StateReflection
    private var innerList = arrayListOf(Inner.A)

    @StateReflection
    private var parcelableList = arrayListOf(ParcelableImpl(1))

    init {
        setToA()
    }

    fun setToA() {
        inner = Inner.A
        parcelable = ParcelableImpl(1)
        innerList = arrayListOf(Inner.A)
        parcelableList = arrayListOf(ParcelableImpl(1))
    }

    fun setToB() {
        inner = Inner.B
        parcelable = ParcelableImpl(2)
        innerList = arrayListOf(Inner.B)
        parcelableList = arrayListOf(ParcelableImpl(2))
    }

    fun isA(): Boolean {
        return inner == Inner.A && parcelable.value == 1 && innerList[0] == Inner.A && parcelableList[0].value == 1
    }

    fun isB(): Boolean {
        return inner == Inner.B && parcelable.value == 2 && innerList[0] == Inner.B && parcelableList[0].value == 2
    }

    private enum class Inner {
        A, B
    }

    private class ParcelableImpl : Parcelable {
        var value: Int = 0

        constructor(anInt: Int) {
            value = anInt
        }

        protected constructor(`in`: Parcel) {
            value = `in`.readInt()
        }

        override fun equals(o: Any?): Boolean {
            if (this === o) return true
            if (o == null || javaClass != o.javaClass) return false

            val that = o as ParcelableImpl?

            return value == that!!.value
        }

        override fun hashCode(): Int {
            return value
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeInt(value)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object {

            val CREATOR: Parcelable.Creator<ParcelableImpl> = object : Parcelable.Creator<ParcelableImpl> {
                override fun createFromParcel(`in`: Parcel): ParcelableImpl {
                    return ParcelableImpl(`in`)
                }

                override fun newArray(size: Int): Array<ParcelableImpl?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

}