package uz.fido.utils.utility.activity

import androidx.lifecycle.LifecycleOwner

fun <T> LifecycleOwner.observe(liveData: LiveEvent<T>, action: (t: T) -> Unit) {
    liveData.observe(this) { it?.let { t -> action(t) } }
}