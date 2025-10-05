package com.example.profile.settings

import com.example.data.IrritantsResponse

data class SettingsState(
    val stymuly: IrritantsResponse = IrritantsResponse(emptyList())
)