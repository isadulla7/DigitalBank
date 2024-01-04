package uz.fido.universaldigital.ui.utils.recyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import uz.fido.universaldigital.R

class ItemHelper(
    private val context: Context,
    private val itemPosition: (Int) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val icon = ContextCompat.getDrawable(context, R.drawable.ic_delete_red)
        val background = ColorDrawable()

        val top = itemView.top + (itemView.height - icon!!.intrinsicHeight) / 2
        val left =
            itemView.width - icon.intrinsicWidth - (itemView.height - icon.intrinsicHeight) / 2
        val right = left + icon.intrinsicHeight
        val bottom = top + icon.intrinsicHeight
        val backgroundColor = Color.parseColor("#FFFFFF")
        background.color = backgroundColor
        if (dX < 0) {
            background.setBounds(
                itemView.right + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
            )
            icon.setBounds(left, top, right, bottom)
        } else if (dX > 0) {
            background.setBounds(
                itemView.left + dX.toInt(),
                itemView.top,
                itemView.left,
                itemView.bottom
            )
            icon.setBounds(top, top, top, bottom)
        }
        background.draw(c)
        icon.draw(c)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        itemPosition.invoke(viewHolder.adapterPosition)
    }
}