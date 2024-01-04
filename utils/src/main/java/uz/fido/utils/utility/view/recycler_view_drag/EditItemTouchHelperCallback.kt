package uz.fido.utils.utility.view.recycler_view_drag

import android.animation.AnimatorSet
import android.content.Context
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import uz.fido.utils.R
import uz.fido.utils.device.vibrateTickLevel2

class EditItemTouchHelperCallback(
    private val mAdapter: ItemTouchHelperAdapter,
    private var context: Context
) : ItemTouchHelper.Callback() {

    var position = -1
    var fromPosition = -1

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {

            //called when you drag item

            (viewHolder?.itemView as? CardView)?.also {
                AnimatorSet().apply {
                    this.duration = ANIM_DURATION
                    this.interpolator = AccelerateDecelerateInterpolator()

                    playTogether(
                        getAlphaAnimator(it, ALPHA_DRAG_MIN),
                        getScaleXAnimator(it, SCALE_MAX),
                        getScaleYAnimator(it, SCALE_MAX),
                        getElevationAnimator(it, R.dimen.elevation_6dp)
                    )
                }.start()
            }
        } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            if (position != -1 && fromPosition != -1) {
                mAdapter.onItemMove(fromPosition, position)
            }
        }
    }

    override fun clearView(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
    ) {    //called when you dropped the item
        super.clearView(recyclerView, viewHolder)

        (viewHolder.itemView as? CardView)?.also {
            AnimatorSet().apply {
                this.duration = ANIM_DURATION
                this.interpolator = AccelerateDecelerateInterpolator()
                playTogether(
                    getAlphaAnimator(it, ALPHA_DRAG_MAX),
                    getScaleXAnimator(it, SCALE_MIN),
                    getScaleYAnimator(it, SCALE_MIN),
                    getElevationAnimator(it, R.dimen.elevation_2dp)
                )
            }.start()
        }
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        if (target.adapterPosition != position) {
            position = target.adapterPosition
            fromPosition = viewHolder.adapterPosition
            vibrateTickLevel2(context)
        }
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        mAdapter.onItemDismiss(viewHolder.adapterPosition)
    }

    companion object {
        private const val ANIM_DURATION = 10L
        private const val ALPHA_DRAG_MIN = 0.9f
        private const val ALPHA_DRAG_MAX = 1f
        private const val SCALE_MIN = 1f
        private const val SCALE_MAX = 1.02f
    }

}