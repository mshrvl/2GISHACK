package com.example.profile.settings

sealed interface SettingsAction {
    data class OnPressure(val isChecked: Boolean) : SettingsAction
    data object GetStimulus : SettingsAction
}