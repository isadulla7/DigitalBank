package uz.fido.utils.sticky

import android.view.View

interface StickyHeaderInterface {
    fun headerPositionForItem(itemPosition: Int): Int
    fun headerLayout(headerPosition: Int): Int
    fun bindHeaderData(header: View, headerPosition: Int)
    fun isHeader(itemPosition: Int): Boolean
}