package com.example.otigoapp.ui.child

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.otigoapp.ui.theme.OtiGoAppTheme

class ChildPanelActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ZORLA YATAY
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        setContent {
            OtiGoAppTheme {
                ChildPanelScreen(
                    onGameClick = { index ->
                        println("Oyun $index tıklandı")
                    },
                    onBackToParent = {
                        finish()
                    }
                )
            }
        }
    }
}
