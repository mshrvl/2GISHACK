package com.example.GISHACK

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.GISHACK.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import ru.dgis.sdk.DGis

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DontPanicApp(modifier = Modifier.padding(innerPadding))
                }
            }
            DGis.initialize(applicationContext)
        }
    }
}