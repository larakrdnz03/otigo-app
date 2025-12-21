package com.example.otigoapp.ui.expert

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.otigoapp.R
import com.example.otigoapp.ui.expert.ExpertChildProfileActivity
import com.example.otigoapp.ui.theme.OtiGoAppTheme

// ---- Model
data class Child(
    val name: String,
    val surname: String,
    val age: Int,
    val avatarRes: Int
)

// ---- Activity
    class ExpertPanelActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { OtiGoAppTheme { ExpertPanelScreen() } }
    }
}

@Composable
fun ExpertPanelScreen() {
    val context = LocalContext.current

    // 👶 Örnek veri (drawable id’leri sende mevcut olmalı)
    val children = remember {
        listOf(
            Child("Kerem", "Kafes", 7, R.drawable.avatar_1),
            Child("Lara Mina", "Karadeniz", 6, R.drawable.avatar_2),
            Child("Gizem", "İhtiyaroğlu", 8, R.drawable.avatar_3),
        )
    }

    var selectedChild by remember { mutableStateOf<Child?>(null) }

    Scaffold(
        bottomBar = { ExpertBottomBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "İlgilendiğiniz Çocuklar",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(children) { child ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = child.avatarRes),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE3F2FD))
                                .clickable { selectedChild = child }
                        )
                        Text(text = child.name, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Popup
            selectedChild?.let { child ->
                ChildInfoDialog(
                    child = child,
                    onDismiss = { selectedChild = null },
                    onViewProfile = {
                        // Profili Gör → profil ekranına yönlendir
                        val i = Intent(context, ExpertChildProfileActivity::class.java).apply {
                            putExtra(ExpertChildProfileActivity.EXTRA_NAME, child.name)
                            putExtra(ExpertChildProfileActivity.EXTRA_SURNAME, child.surname)
                            putExtra(ExpertChildProfileActivity.EXTRA_AGE, child.age)
                            putExtra(ExpertChildProfileActivity.EXTRA_AVATAR_RES, child.avatarRes)
                        }
                        context.startActivity(i)
                        selectedChild = null
                    }
                )
            }
        }
    }
}

@Composable
fun ChildInfoDialog(
    child: Child,
    onDismiss: () -> Unit,
    onViewProfile: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("${child.name} ${child.surname}", style = MaterialTheme.typography.titleMedium)
                Text("Yaş: ${child.age}", color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onViewProfile) {
                    Text("Profili Gör")
                }
            }
        }
    }
}

@Composable
fun ExpertBottomBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 36.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { /* TODO: Ayarlar ekranına git */ }) {
            Icon(Icons.Default.Settings, contentDescription = "Ayarlar")
        }
        IconButton(onClick = { /* TODO: Bağlantılar ekranına git */ }) {
            Icon(Icons.Default.GroupAdd, contentDescription = "Bağlantı İstekleri")
        }
        IconButton(onClick = { /* TODO: Chat ekranına git */ }) {
            Icon(Icons.Default.Chat, contentDescription = "Sohbet")
        }
    }
}
