package com.example.otigoapp.ui.parent

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.otigoapp.R
import com.example.otigoapp.ui.theme.OtiGoAppTheme

class ParentChildDetailActivity : ComponentActivity() {

    companion object {
        const val EXTRA_NAME = "child_name"
        const val EXTRA_AGE = "child_age"
        const val EXTRA_AVATAR = "child_avatar"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val name = intent.getStringExtra(EXTRA_NAME) ?: ""
        val age = intent.getIntExtra(EXTRA_AGE, -1)
        val avatar = intent.getIntExtra(EXTRA_AVATAR, R.drawable.avatar_1)

        setContent {
            OtiGoAppTheme {
                Surface {
                    ParentChildDetailScreen(
                        name = name,
                        age = age,
                        avatarRes = avatar,
                        onBack = { finish() }
                    )
                }
            }
        }
    }
}

@Composable
fun ParentChildDetailScreen(
    name: String,
    age: Int,
    avatarRes: Int,
    onBack: () -> Unit
) {
    // ⭐ Rapolar dinamik olarak buraya gelecek (ViewModel, API, Room DB)
    var reports by remember { mutableStateOf<List<String>>(emptyList()) }

    // ÖRNEK: ileride şöyle doldurulabilir:
    // LaunchedEffect(Unit) {
    //     reports = listOf("Rapor1.pdf", "Rapor2.pdf") // API veya DB çağrısı
    // }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // ← GERİ BUTONU
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Geri",
            modifier = Modifier
                .size(32.dp)
                .clickable { onBack() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ⭐ ÇOCUK BİLGİ KARTI
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(avatarRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEDE7F6))
                )

                Spacer(modifier = Modifier.width(20.dp))

                Column {
                    Text(name, fontWeight = FontWeight.Bold)
                    Text("Yaş: $age", color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ⭐ RAPOR BAŞLIK KARTI
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Description, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Raporlar", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ⭐ RAPOR LİSTESİ (Dinamik)
        if (reports.isEmpty()) {
            Text(
                "Bu çocuk için kayıtlı rapor bulunmuyor.",
                color = Color.Gray,
                modifier = Modifier.padding(6.dp)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                reports.forEach { report ->
                    ReportCard(reportName = report)
                }
            }
        }
    }
}

@Composable
fun ReportCard(reportName: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(reportName)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { /* PDF açılacak */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9575CD))
                ) {
                    Text("Aç")
                }
                Button(
                    onClick = { /* PDF indirilecek */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E57C2))
                ) {
                    Text("İndir")
                }
            }
        }
    }
}
