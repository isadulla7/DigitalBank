package uz.fido.utils.view.custom_snackbar

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import android.view.Gravity
import android.widget.FrameLayout
import uz.fido.utils.R
import uz.fido.utils.const.AlertType
import uz.fido.utils.utility.view.findSuitableParent

@SuppressLint("RestrictedApi")
class CustomSnackbar(
    parent: ViewGroup, content: CustomSnackbarView
) : BaseTransientBottomBar<CustomSnackbar>(parent, content, content) {

    init {
        view.setBackgroundColor(ContextCompat.getColor(view.context, android.R.color.transparent))
        view.setPadding(0, 0, 0, 0)
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        view.layoutParams = params
    }

    companion object {

        fun make(
            view: View,
            message: String,
            alertType: AlertType
        ): CustomSnackbar {
            val parent = view.findSuitableParent()
                ?: throw IllegalArgumentException("No suitable parent found from the given view. Please provide a valid view.")
            val customView = LayoutInflater.from(view.context).inflate(
                R.layout.layout_snackbar_view, parent, false
            ) as CustomSnackbarView
            customView.makeSnackbar(message, alertType)
            return CustomSnackbar(
                parent, customView
            )
        }
    }

}