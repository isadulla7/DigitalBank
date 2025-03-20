package uz.fido.universaldigital.ui.utils.extensions

import android.app.Activity
import android.app.Activity.OVERRIDE_TRANSITION_OPEN
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.navigation.NavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.paperdb.Paper
import uz.fido.universaldigital.R
import uz.fido.utils.const.Const
import uz.fido.utils.const.Const.USER_LOGGED

fun Activity.pendingTransition(enterAnim: Int, exitAnim: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, enterAnim, exitAnim)
    } else {
        overridePendingTransition(enterAnim, exitAnim)
    }
}

fun Activity.isActive(): Boolean {
    return !isDestroyed && !isFinishing
}

fun Context.openPage(navController: NavController, id: Int, bundle: Bundle? = null) {
    navController.navigate(id, bundle, null)
}

fun adjustBottomNavForKeyboard(bottomNavigationView: BottomNavigationView) {
    ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView) { view, insets ->
        val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
        view.updateLayoutParams<CoordinatorLayout.LayoutParams> {
            bottomMargin = imeInsets.bottom
        }
        insets
    }
}

fun Activity.getStartDestination(): Int = if (getFromPaper(Const.NEW_DESIGN, "N") == "Y") R.id.menuNewHomeFragment else R.id.productsFragment

fun Activity.isNewDesign() = getFromPaper(Const.NEW_DESIGN, "N") == "Y"

fun isUserLogged() = Paper.book().read(USER_LOGGED, false) == false

fun getLoginStartDestination(): Int = if (isUserLogged()) R.id.chooseLanguageFragment else R.id.passCodeFragment
