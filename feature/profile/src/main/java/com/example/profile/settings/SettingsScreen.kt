package com.example.profile.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Serializable
data object SettingsScreenDestination

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel()
) {

    val state by viewModel.collectAsState()
    Scaffold() { pv ->
        SettingsScreenContent(
            modifier = Modifier.padding(pv),
            state = state
        )
    }
}


@Composable
fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    state: SettingsState
) {

}