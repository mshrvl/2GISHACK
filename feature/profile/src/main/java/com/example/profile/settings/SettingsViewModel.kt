package com.example.profile.settings

import androidx.lifecycle.ViewModel
import com.example.data.repository.MapRepository
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class SettingsViewModel(private val mapRepo: MapRepository) : ViewModel(),
    ContainerHost<SettingsState, SettingsSideEffect> {

    override val container = container<SettingsState, SettingsSideEffect>(SettingsState())

    fun dispatch(action: SettingsAction) {
        when (action) {
            is SettingsAction.OnPressure -> {}
            is SettingsAction.GetStimulus -> getData()
        }
    }

    init {
        dispatch(SettingsAction.GetStimulus)
    }

    private fun getData() = intent {
        val stimuly = mapRepo.getStimuli()
    }

    private fun onCheckAlert(isChecked: Boolean) = intent {

    }
}