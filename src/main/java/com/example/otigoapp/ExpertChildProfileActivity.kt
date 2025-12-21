package com.example.otigoapp.ui.expert

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.otigoapp.R
import com.example.otigoapp.ui.theme.OtiGoAppTheme

class ExpertChildProfileActivity : ComponentActivity() {

    companion object {
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_SURNAME = "extra_surname"
        const val EXTRA_AGE = "extra_age"
        const val EXTRA_AVATAR_RES = "extra_avatar_res"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Intent’ten bilgileri çek
        val name = intent.getStringExtra(EXTRA_NAME) ?: "Ad"
        val surname = intent.getStringExtra(EXTRA_SURNAME) ?: "Soyad"
        val age = intent.getIntExtra(EXTRA_AGE, 0)
        val avatar = intent.getIntExtra(EXTRA_AVATAR_RES, R.drawable.avatar_1)

        setContent {
            OtiGoAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ExpertChildProfileScreen(
                        childName = "$name $surname",
                        childAge = age,
                        avatarRes = avatar
                    )
                }
            }
        }
    }
}

@Composable
fun ExpertChildProfileScreen(childName: String, childAge: Int, avatarRes: Int) {
    var selectedTab by remember { mutableStateOf(0) } // 0=Görevlendirme, 1=Raporlar

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Üst bilgi (avatar + ad/yaş)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = avatarRes),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(72.dp)
                    .background(Color(0xFFE3F2FD), RoundedCornerShape(12.dp))
                    .padding(8.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = childName,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(text = "Yaş: $childAge", color = Color.Gray)
            }
        }

        Spacer(Modifier.height(24.dp))

        // Sekme butonları (eşit genişlikte)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabButton(
                text = "Görevlendirme",
                icon = Icons.Default.Assignment,
                selected = selectedTab == 0,
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
            ) { selectedTab = 0 }

            Spacer(Modifier.width(8.dp))

            TabButton(
                text = "Raporlar",
                icon = Icons.Default.Description,
                selected = selectedTab == 1,
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
            ) { selectedTab = 1 }
        }

        Spacer(Modifier.height(24.dp))

        when (selectedTab) {
            0 -> TaskAssignmentSection()
            1 -> ReportsSection()
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bg = if (selected) Color(0xFF90CAF9) else Color.Transparent
    val fg = if (selected) Color.Black else Color.Gray

    // RowScope içindeki children’a weight verebilmek için modifier parametresini dışarıdan alıyoruz
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = bg),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Icon(icon, contentDescription = null, tint = fg)
        Spacer(Modifier.width(8.dp))
        Text(text, color = fg)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskAssignmentSection() {
    var selectedGame by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf("") }
    val taskList = remember { mutableStateListOf<String>() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sol: dinamik liste
        Column(
            modifier = Modifier
                .weight(1f)
                .height(250.dp)
                .background(Color(0xFFF1F1F1), RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (taskList.isEmpty()) {
                Text("Henüz görev eklenmedi", color = Color.Gray)
            } else {
                taskList.forEach { Text("• $it") }
            }
        }

        // Sağ: seçimler
        Column(
            modifier = Modifier
                .weight(1f)
                .background(Color(0xFFF9F9F9), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            var expandedGame by remember { mutableStateOf(false) }
            var expandedLevel by remember { mutableStateOf(false) }

            val gameOptions = listOf("Puzzle", "Gölge Eşleştirme", "Zıt Kavramlar")
            val levelOptions = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9" )

            ExposedDropdownMenuBox(
                expanded = expandedGame,
                onExpandedChange = { expandedGame = !expandedGame }
            ) {
                OutlinedTextField(
                    value = selectedGame,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Oyun") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGame) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedGame,
                    onDismissRequest = { expandedGame = false }
                ) {
                    gameOptions.forEach { game ->
                        DropdownMenuItem(
                            text = { Text(game) },
                            onClick = {
                                selectedGame = game
                                expandedGame = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = expandedLevel,
                onExpandedChange = { expandedLevel = !expandedLevel }
            ) {
                OutlinedTextField(
                    value = selectedLevel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Seviye") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLevel) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedLevel,
                    onDismissRequest = { expandedLevel = false }
                ) {
                    levelOptions.forEach { level ->
                        DropdownMenuItem(
                            text = { Text(level) },
                            onClick = {
                                selectedLevel = level
                                expandedLevel = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (selectedGame.isNotEmpty() && selectedLevel.isNotEmpty()) {
                        taskList.add("$selectedGame - $selectedLevel")
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) { Text("Ekle") }
        }
    }
}

@Composable
fun ReportsSection() {
    val reports = listOf("Hafta 1 Raporu.pdf", "Hafta 2 Raporu.pdf")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        if (reports.isEmpty()) {
            Text("Henüz rapor bulunmuyor", color = Color.Gray)
        } else {
            reports.forEach { report ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(report)
                        Button(onClick = { /* TODO: PDF indir */ }) { Text("İndir") }
                    }
                }
            }
        }
    }
}
