package uz.fido.universaldigital.ui.activities.app_icon_changer

import android.app.Activity
import android.content.ComponentName
import android.content.pm.PackageManager
import uz.fido.universaldigital.base.AppIcons
import uz.fido.universaldigital.ui.utils.extensions.getFromPaper
import uz.fido.utils.const.Const

fun Activity.changeAppIcon() {
    val iconAliasName = getFromPaper(Const.CURRENT_APP_ICON, AppIcons.APP_ICON_DEFAULT)
    // Disable other aliases
    val aliases = listOf(
        AppIcons.APP_ICON_SPRING,
        AppIcons.APP_ICON_SUMMER,
        AppIcons.APP_ICON_AUTUMN,
        AppIcons.APP_ICON_WINTER,
        AppIcons.APP_ICON_8MARCH,
        AppIcons.APP_ICON_FLAG,
        AppIcons.APP_ICON_DEFAULT
    )
    val packageManager = applicationContext.packageManager
    for (alias in aliases) {
        if (alias != iconAliasName) {
            packageManager.setComponentEnabledSetting(
                ComponentName(packageName, "$packageName.$alias"),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        }
    }
    // Enable the selected alias
    if (aliases.contains(iconAliasName)) {
        packageManager.setComponentEnabledSetting(
            ComponentName(packageName, "$packageName.$iconAliasName"),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    } else {
        packageManager.setComponentEnabledSetting(
            ComponentName(packageName, "$packageName.default"),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }
}