package com.example.squadup.core.utils

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

enum class AppLanguage {
    EN,
    PT
}

fun setAppLanguage(
    context: Context,
    language: AppLanguage
) {
    val languageTag = when (language) {
        AppLanguage.EN -> "en"
        AppLanguage.PT -> "pt-PT"
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.getSystemService(LocaleManager::class.java)
            .applicationLocales = LocaleList.forLanguageTags(languageTag)
    } else {
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(languageTag)
        )
    }
}

fun getCurrentLanguage(context: Context): AppLanguage {
    val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val locales = context.getSystemService(LocaleManager::class.java).applicationLocales
        if (locales.isEmpty) null else locales[0]
    } else {
        val locales = AppCompatDelegate.getApplicationLocales()
        if (locales.isEmpty) null else locales[0]
    } ?: context.resources.configuration.locales[0]

    return if (locale.language.startsWith("pt")) {
        AppLanguage.PT
    } else {
        AppLanguage.EN
    }
}