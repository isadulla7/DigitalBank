package uz.fido.utils.utility.theme

import android.content.Context
import android.content.SharedPreferences

private const val KEY_NAME = "KEY_NAME"
private const val KEY_DARK = "KEY_DARK"
private const val KEY_SYSTEM = "KEY_SYSTEM"

class PreferencesImpl(preferences: SharedPreferences) : IPreferences {

    override var isTheme: String by TextPreference(preferences, KEY_NAME)
    override var isThemeDark: String by TextPreference(preferences, KEY_DARK)

    companion object {
        private var instanse: IPreferences? = null

        fun instance(context: Context): IPreferences {
            if (instanse == null) {
                val preference = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
                instanse = PreferencesImpl(preference)
            }
            return instanse!!
        }

    }
}