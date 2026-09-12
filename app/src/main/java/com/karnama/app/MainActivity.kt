package com.karnama.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.CompositionLocalProvider
import com.karnama.app.ui.navigation.KarnamaNavGraph
import com.karnama.app.ui.theme.KarnamaTheme
import com.karnama.app.ui.theme.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as KarnamaApplication).container

        setContent {
            KarnamaApp(themeModeFlow = container.settingsRepository.themeMode)
        }
    }
}

@Composable
private fun KarnamaApp(themeModeFlow: kotlinx.coroutines.flow.Flow<ThemeMode>) {
    val themeMode by themeModeFlow.collectAsState(initial = ThemeMode.SYSTEM)

    // کل رابط کاربری «کارنما» راست‌به‌چپ است، صرف‌نظر از زبان دستگاه
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        KarnamaTheme(themeMode = themeMode) {
            Surface(modifier = Modifier.fillMaxSize()) {
                KarnamaNavGraph()
            }
        }
    }
}
