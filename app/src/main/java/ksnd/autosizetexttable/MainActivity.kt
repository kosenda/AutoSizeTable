package ksnd.autosizetexttable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ksnd.autosizetexttable.ui.theme.AutoSizeTextTableTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AutoSizeTextTableTheme(
                darkTheme = false,
            ) {
                SampleScreen()
            }
        }
    }
}
