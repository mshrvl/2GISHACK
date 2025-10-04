package com.example.profile.settings

import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class SettingsViewModel : ViewModel(), ContainerHost<SettingsState, SettingsSideEffect> {

    override val container = container<SettingsState, SettingsSideEffect>(SettingsState())

    fun dispatch(action: SettingsAction) {
        when (action) {
            is SettingsAction.OnPressure -> {}
        }
    }

    private fun onCheckAlert(isChecked: Boolean) = intent {

    }
}