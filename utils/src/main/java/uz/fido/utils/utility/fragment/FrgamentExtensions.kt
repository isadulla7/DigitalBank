package uz.fido.utils.utility.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.Navigator
import uz.fido.utils.R

fun Fragment.goto(id: Int) {
    if (view == null) return
    Navigation.findNavController(view!!).navigate(id)
}

fun Fragment.gotoWithTransition(id: Int) {
    if (view == null) return
    Navigation.findNavController(view!!).navigate(id, null)
}

fun Fragment.gotoWithSlide(id: Int, bundle: Bundle) {
    if (view == null) return
    Navigation.findNavController(view!!).navigate(id, bundle, getNavOptions())
}

fun Fragment.gotoWithSlide(id: Int) {
    if (view == null) return
    Navigation.findNavController(view!!).navigate(id, null, getNavOptions())
}

fun Fragment.gotoWithSlideLeft(id: Int) {
    if (view == null) return
    Navigation.findNavController(view!!).navigate(id, null, getNavOptionsLeft())
}

fun Fragment.gotoWithTransition(id: Int, bundle: Bundle) {
    if (view == null) return
    Navigation.findNavController(requireView()).navigate(id, bundle)
}

fun Fragment.gotoWithTransitionAndExtrasAndBundle(
    id: Int,
    navExtras: Navigator.Extras,
    bundle: Bundle
) {
    if (view == null) return
    Navigation.findNavController(view!!).navigate(id, bundle, null, navExtras)
}

fun Fragment.gotoWithTransition(directions: NavDirections) {
    if (view == null) return
    Navigation.findNavController(view!!).navigate(directions)
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

fun Fragment.goto(id: Int, bundle: Bundle) {
    if (view == null) return
    Navigation.findNavController(view!!).navigate(id, bundle)
}

fun Fragment.goto(directions: NavDirections) {
    if (view == null) return
    Navigation.findNavController(view!!).navigate(directions)
}

fun Fragment.pop() {
    if (view == null) return
    Navigation.findNavController(view!!).popBackStack()
}