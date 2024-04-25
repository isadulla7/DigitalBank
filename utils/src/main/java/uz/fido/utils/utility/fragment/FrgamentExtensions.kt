package uz.fido.utils.utility.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController
import uz.fido.utils.R

fun Fragment.goto(id: Int) {
    if (view == null) return
    findNavController().navigate(id, null)
}

fun Fragment.goto(id: Int, bundle: Bundle) {
    if (view == null) return
    findNavController().navigate(id, bundle)
}

fun Fragment.goto(directions: NavDirections) {
    if (view == null) return
    findNavController().navigate(directions)
}

fun Fragment.gotoWithSlide(id: Int) {
    if (view == null) return
    findNavController().navigate(id, null, getNavOptions())
}

fun Fragment.gotoWithSlide(id: Int, bundle: Bundle) {
    if (view == null) return
    findNavController().navigate(id, bundle, getNavOptions())
}

fun Fragment.pop() {
    if (view == null) return
    findNavController().popBackStack()
}

fun Fragment.gotoWithSlideLeft(id: Int) {
    if (view == null) return
    findNavController().navigate(id, null, getNavOptionsLeft())
}

fun Fragment.gotoWithTransitionAndExtras(
    id: Int, navExtras: Navigator.Extras
) {
    if (view == null) return
    findNavController().navigate(id, null, null, navExtras)
}


fun Fragment.gotoWithTransitionAndExtrasAndBundle(
    id: Int, navExtras: Navigator.Extras, bundle: Bundle
) {
    if (view == null) return
    findNavController().navigate(id, bundle, null, navExtras)
}

fun getNavOptions(): NavOptions {
    return NavOptions.Builder().setEnterAnim(R.anim.enter_from_right)
        .setExitAnim(R.anim.exit_to_left).setPopEnterAnim(R.anim.enter_from_left)
        .setPopExitAnim(R.anim.exit_to_right).build()
}

fun getNavOptionsLeft(): NavOptions {
    return NavOptions.Builder().setEnterAnim(R.anim.enter_from_left)
        .setExitAnim(R.anim.exit_to_right).setPopEnterAnim(R.anim.enter_from_right)
        .setPopExitAnim(R.anim.exit_to_left).build()
}
