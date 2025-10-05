package com.example.profile

import com.example.profile.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profilemodule = module {
    viewModelOf(::SettingsViewModel)
}