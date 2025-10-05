package com.example.profile.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.Serializable

@Serializable
data object ProfileScreenDestination

@Composable
fun ProfileScreen(modifier: Modifier = Modifier, onNavToScreen: () -> Unit) {
    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color(0xFFECEEF2)
    ) { pv ->
        ProfileScreenContent(
            modifier = modifier
                .padding(pv)
                .fillMaxSize(),
            onNavToScreen = onNavToScreen
        )
    }
}


@Composable
fun ProfileScreenContent(modifier: Modifier = Modifier, onNavToScreen: () -> Unit) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        PersonHeader()
        Spacer(modifier = Modifier.height(8.dp))
        PersonalInfoBlock(onNavToScreen = onNavToScreen)
    }
}

@Composable
fun PersonHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(
                color = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(text = "Иван Петров")
            Text(text = "+7 968 131 12 12")
        }
    }
}

@Composable
fun PersonalInfoBlock(
    modifier: Modifier = Modifier,
    onNavToScreen: () -> Unit,
) {
    Column(
        modifier = modifier
            .background(
                color = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
                shape = RoundedCornerShape(12.dp)
            )
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "Имя")
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                onValueChange = {},
                value = "",
                shape = RoundedCornerShape(20.dp)
            )
            Text(text = "Фамилия")
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                onValueChange = {},
                value = "",
                shape = RoundedCornerShape(20.dp)
            )
            Text(text = "Отчество")
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                onValueChange = {},
                value = "",
                shape = RoundedCornerShape(20.dp)
            )
            Text(text = "Дата рождения")
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                onValueChange = {},
                value = "",
                shape = RoundedCornerShape(20.dp)

            )
            Text(text = "Почта")
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                onValueChange = {},
                value = "",
                shape = RoundedCornerShape(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        PersonalizationButtonWithIcon(
            modifier = Modifier.fillMaxWidth(),
            onClick = onNavToScreen
        )
    }
}


@Composable
private fun PersonalizationButtonWithIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors()
    ) {
        Text(
            text = "Персонализация",
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
        )
    }
}

