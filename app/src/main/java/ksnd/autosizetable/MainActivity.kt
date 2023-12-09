package ksnd.autosizetable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ksnd.autosizetable.ui.theme.AutoSizeTableTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AutoSizeTableTheme(
                darkTheme = false,
            ) {
                SampleScreen()
            }
        }
    }
}
