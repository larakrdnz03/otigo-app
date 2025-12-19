package com.example.otigoapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.otigoapp.ui.theme.OtiGoAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OtiGoAppTheme {
                UnityLaunchButton()
            }
        }
    }
}

@Composable
fun UnityLaunchButton() {
    val ctx = LocalContext.current
    Button(onClick = {
        // Unity’nin aktivitesini doğrudan başlat
        val i = Intent().setClassName(
            /* package = */ ctx.packageName,
            /* class   = */ "com.unity3d.player.UnityPlayerActivity"
        )
        // (opsiyonel) sahne adı gibi ekstra gönder
        i.putExtra("unity_scene", "GolgeOyunuScene")
        ctx.startActivity(i)
    }) {
        Text("Unity'yi aç")
    }
}
