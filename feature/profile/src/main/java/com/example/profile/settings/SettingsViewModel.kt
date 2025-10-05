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
            is SettingsAction.OnPressure -> onCheckAlert(action.id, action.isChecked)
            is SettingsAction.GetStimulus -> getData()
        }
    }

    init {
        dispatch(SettingsAction.GetStimulus)
    }

    private fun getData() = intent {
        val stimuly = mapRepo.getStimuli()
        reduce { state.copy(stymuly = stimuly) }
    }

    private fun onCheckAlert(id: Int, newValue: Boolean) = intent {
        mapRepo.setStimulus(id, newValue)
        val updatedStimuli = state.stymuly.stimulus.map { stim ->
            if (stim.id == id) stim.copy(isActive = newValue) else stim
        }
        reduce {
            state.copy(stymuly = state.stymuly.copy(stimulus = updatedStimuli))
        }
    }
}