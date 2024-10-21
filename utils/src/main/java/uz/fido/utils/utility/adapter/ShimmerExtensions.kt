package uz.fido.utils.utility.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import uz.fido.utils.R
import uz.fido.utils.libs.skeleton.Skeleton
import uz.fido.utils.libs.skeleton.SkeletonScreen

fun showSkeleton(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>, id: Int, count: Int? = null): SkeletonScreen {
    return if (count != null)
        Skeleton.bind(recyclerView)
            .adapter(adapter)
            .count(count)
            .color(R.color.shimmerColor)
            .load(id)
            .show()
    else Skeleton.bind(recyclerView)
        .adapter(adapter)
        .color(R.color.shimmerColor)
        .load(id)
        .show()
}

fun showSkeletonView(id: Int, view: View): SkeletonScreen {
    return Skeleton.bind(view)
        .color(R.color.shimmerColor)
        .load(id)
        .show()
}