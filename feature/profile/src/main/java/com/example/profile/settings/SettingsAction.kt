package com.example.profile.settings

sealed interface SettingsAction {
    data class OnPressure(val isChecked: Boolean, val id: Int) : SettingsAction
    data object GetStimulus : SettingsAction
}