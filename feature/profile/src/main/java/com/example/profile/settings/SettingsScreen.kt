package com.example.profile.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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

    Scaffold(
        modifier = modifier,
        containerColor = androidx.compose.ui.graphics.Color(0xFFECEEF2)
    ) { pv ->
        SettingsScreenContent(
            modifier = Modifier
                .padding(pv)
                .fillMaxSize(),
            state = state,
            action = viewModel::dispatch
        )
    }
}


@Composable
fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    state: SettingsState,
    action: (SettingsAction) -> Unit
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Column(
            modifier = Modifier.background(
                color = Color(0xFFFFFFFF),
                shape = RoundedCornerShape(16.dp)
            )
        ) {
            Text(
                text = "Аллергены",
                modifier = Modifier.padding(16.dp)
            )
            state.stymuly.stimulus.forEach { stim ->
                var localChecked by remember(stim.id) {
                    mutableStateOf(stim.isActive)
                }

                StimuliButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stim.name ?: "",
                    onCheckChange = { newValue ->
                        // Обновляем локальное состояние
                        localChecked = newValue
                        // Отправляем действие в ViewModel
                        action(SettingsAction.OnPressure(newValue, stim.id ?: 0))
                    },
                    checked = localChecked
                )
            }
        }
    }
}

@Composable
fun StimuliButton(
    modifier: Modifier = Modifier,
    text: String,
    checked: Boolean,
    onCheckChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = text
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckChange
        )
    }
}


@Composable
fun StimuliButton(
    modifier: Modifier = Modifier,
    text: String,
    checked: Boolean,
    onCheckChange: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(modifier = Modifier.weight(0.8f), text = text)
        Switch(checked = checked, onCheckedChange = { onCheckChange })
    }
}