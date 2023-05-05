package uz.fido.utils.utility.language

import android.content.Context
import android.content.res.Configuration
import io.paperdb.Paper
import uz.fido.utils.const.Const.APP_LANGUAGE
import java.util.Locale

object LocaleHelper {

    fun getLanguage(): String {
        return if (getPersistedData(Locale.getDefault().language).isNotEmpty()) ({
            getPersistedData(Locale.getDefault().language)
        }).toString() else {
            "uz"
        }
    }

    fun setLocale(context: Context, language: String): Context {
        persist(language)
        return updateResources(context, language)
    }

    fun getSelectedLang(): Int {
        val lang = getLanguage().lowercase()
        return when (lang) {
            "en", "eng" -> 3
            "uz", "uzl" -> 2
            "uzc" -> 1
            else -> 0
        }
    }

    private fun getPersistedData(defaultLanguage: String): String {
        return Paper.book().read(APP_LANGUAGE, defaultLanguage)
    }

    private fun persist(language: String) {
        Paper.book().write(APP_LANGUAGE, language)
    }

    private fun updateResources(context: Context, language: String): Context {
        var context = context
        val locale = Locale(language)
        Locale.setDefault(locale)
        val res = context.resources
        val config = Configuration(res.configuration)
        config.setLocale(locale)
        context = context.createConfigurationContext(config)
        return context
    }
}