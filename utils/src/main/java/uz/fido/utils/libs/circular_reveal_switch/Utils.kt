@file:JvmName("Utils")

package uz.fido.utils.libs.circular_reveal_switch

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import java.lang.ref.WeakReference

/**
 * Extension property to get the Activity from a Context object.
 * It unwraps the Context object if it's a ContextWrapper.
 * Throws an IllegalStateException if the Context is not an Activity.
 */
internal val Context.activity: Activity
    get() {
        var context = this
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = context.baseContext
        }
        error("Activity not found")
    }

/**
 * Extension function to create a weak reference to an object.
 */
@Suppress("NOTHING_TO_INLINE")
internal inline fun <T> T.weak() = WeakReference(this)