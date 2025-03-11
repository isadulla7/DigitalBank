package uz.fido.utils.utility.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import uz.fido.utils.R

fun Fragment.goto(id: Int) {
    if (view == null) return
    findNavController().navigate(id)
}

fun Fragment.gotoWithPopUp(targetFragmentId: Int, popupFragmentId: Int) {
    if (view == null) return
    findNavController().navigate(targetFragmentId, null, popUpNavOptions(popupFragmentId))
}

fun Fragment.gotoWithPopUp(targetFragmentId: Int, popupFragmentId: Int, inclusive: Boolean) {
    if (view == null) return
    findNavController().navigate(targetFragmentId, null, popUpNavOptions(popupFragmentId, inclusive))
}

fun Fragment.goto(id: Int, bundle: Bundle) {
    if (view == null) return
    findNavController().navigate(id, bundle)
}

fun Fragment.goto(targetFragmentId: Int, popupFragmentId: Int, bundle: Bundle) {
    if (view == null) return
    findNavController().navigate(targetFragmentId, bundle, popUpNavOptions(popupFragmentId))
}

fun Fragment.goto(directions: NavDirections) {
    if (view == null) return
    findNavController().navigate(directions)
}

fun Fragment.gotoWithSlide(id: Int) {
    if (view == null) return
    findNavController().navigate(id, null, slideSideNavOptions())
}

fun Fragment.gotoWithSlide(id: Int, bundle: Bundle) {
    if (view == null) return
    if (findNavController().currentDestination?.id != id) {
        findNavController().navigate(id, bundle, slideSideNavOptions())
    }
}

fun Fragment.gotoWithPopupSlide(targetFragmentId: Int, popupFragmentId: Int, bundle: Bundle) {
    if (view == null) return
    if (findNavController().currentDestination?.id != targetFragmentId) {
        findNavController().navigate(targetFragmentId, bundle, popUpNavOptions(popupFragmentId))
    }
}

fun Fragment.gotoWithSlideUp(id: Int, bundle: Bundle) {
    if (view == null) return
    if (findNavController().currentDestination?.id != id) {
        findNavController().navigate(id, bundle, slideUpNavOptions())
    }
}

fun Fragment.pop() {
    if (view == null) return
    findNavController().popBackStack()
}

fun getNavOptions(popupFragmentId: Int? = null): NavOptions {
    return if (popupFragmentId != null) {
        NavOptions.Builder().setEnterAnim(R.anim.enter_from_right)
            .setExitAnim(R.anim.exit_to_left).setPopEnterAnim(R.anim.enter_from_left)
            .setPopExitAnim(R.anim.exit_to_right).setPopUpTo(popupFragmentId, false, saveState = false).build()
    } else {
        NavOptions.Builder().setEnterAnim(R.anim.enter_from_right)
            .setExitAnim(R.anim.exit_to_left).setPopEnterAnim(R.anim.enter_from_left)
            .setPopExitAnim(R.anim.exit_to_right).build()
    }
}

fun popUpNavOptions(popupFragmentId: Int, inclusive: Boolean): NavOptions {
    return NavOptions.Builder().setPopUpTo(destinationId = popupFragmentId, inclusive = inclusive).build()
}

fun popUpNavOptions(popupFragmentId: Int): NavOptions {
    return NavOptions.Builder().setPopUpTo(destinationId = popupFragmentId, inclusive = false).build()
}

fun slideSideNavOptions(): NavOptions {
    return NavOptions.Builder().setEnterAnim(R.anim.enter_from_right)
        .setExitAnim(R.anim.exit_to_left).setPopEnterAnim(R.anim.enter_from_left)
        .setPopExitAnim(R.anim.exit_to_right).build()
}

fun slideUpNavOptions(): NavOptions {
    return NavOptions.Builder().setEnterAnim(R.anim.slide_up).build()
}