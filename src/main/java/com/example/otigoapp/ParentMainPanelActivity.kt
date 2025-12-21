package com.example.otigoapp.ui.parent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.otigoapp.R
import com.example.otigoapp.data.ParentChildRepository
import com.example.otigoapp.ui.child.ChildPanelActivity
import com.example.otigoapp.ui.theme.OtiGoAppTheme

// -----------------------------
// MODEL
// -----------------------------
data class ParentChild(
    val name: String,
    val age: Int,
    val avatar: Int = R.drawable.avatar_1
)

class ParentMainPanelActivity : ComponentActivity() {

    // ➕ Çocuk ekleme (formdan dönüş)
    private val addChildLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult

            val data = result.data ?: return@registerForActivityResult
            val name =
                data.getStringExtra(ParentSymptomFormActivity.EXTRA_CHILD_NAME)
                    ?: return@registerForActivityResult
            val age =
                data.getIntExtra(
                    ParentSymptomFormActivity.EXTRA_CHILD_AGE,
                    -1
                )

            if (age != -1) {
                addChildIfNotExists(name, age)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔥 İlk belirti formundan gelen çocuk
        intent?.let {
            val name =
                it.getStringExtra(ParentSymptomFormActivity.EXTRA_CHILD_NAME)
            val age =
                it.getIntExtra(
                    ParentSymptomFormActivity.EXTRA_CHILD_AGE,
                    -1
                )

            if (!name.isNullOrBlank() && age != -1) {
                addChildIfNotExists(name, age)
            }
        }

        setContent {
            OtiGoAppTheme {
                ParentMainPanelScreen(
                    children = ParentChildRepository.children,

                    onAddChild = {
                        addChildLauncher.launch(
                            Intent(this, ParentSymptomFormActivity::class.java)
                        )
                    },

                    // 👤 PROFİL (avatar tıklanınca)
                    onOpenChildProfile = { child ->
                        val i = Intent(this, ParentChildDetailActivity::class.java).apply {
                            putExtra(ParentChildDetailActivity.EXTRA_NAME, child.name)
                            putExtra(ParentChildDetailActivity.EXTRA_AGE, child.age)
                            putExtra(ParentChildDetailActivity.EXTRA_AVATAR, child.avatar)
                        }
                        startActivity(i)
                    },

                    // 🎮 OYUN PANELİ (buton + popup)
                    onOpenChildGamePanel = { child ->
                        val i = Intent(this, ChildPanelActivity::class.java).apply {
                            putExtra("child_name", child.name)
                            putExtra("child_age", child.age)
                        }
                        startActivity(i)
                    }
                )
            }
        }
    }

    // 🛑 Aynı çocuk 2 kere eklenmez
    private fun addChildIfNotExists(name: String, age: Int) {
        val exists = ParentChildRepository.children.any {
            it.name == name && it.age == age
        }
        if (!exists) {
            ParentChildRepository.children.add(
                ParentChild(name = name, age = age)
            )
        }
    }
}

@Composable
fun ParentMainPanelScreen(
    children: SnapshotStateList<ParentChild>,
    onAddChild: () -> Unit,
    onOpenChildProfile: (ParentChild) -> Unit,
    onOpenChildGamePanel: (ParentChild) -> Unit
) {
    var showChildPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Çocuklar",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {

            // 👤 Avatar → PROFİL
            children.forEach { child ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        onOpenChildProfile(child)
                    }
                ) {
                    Image(
                        painter = painterResource(child.avatar),
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8EAF6))
                    )
                    Text(child.name)
                }
            }

            // ➕ Çocuk ekle
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD1C4E9))
                        .clickable { onAddChild() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                }
                Text("Çocuk Ekle")
            }
        }

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = {
                when (children.size) {
                    0 -> {}
                    1 -> onOpenChildGamePanel(children.first())
                    else -> showChildPicker = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Çocuk Paneline Geç")
        }
    }

    // 👇 POPUP (OYUN PANELİNE GİDER)
    if (showChildPicker) {
        ChildPickerDialog(
            children = children,
            onSelect = { child ->
                showChildPicker = false
                onOpenChildGamePanel(child)
            },
            onDismiss = { showChildPicker = false }
        )
    }
}

@Composable
fun ChildPickerDialog(
    children: List<ParentChild>,
    onSelect: (ParentChild) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Hangi çocuğun paneline geçilsin?",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                children.forEach { child ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelect(child) }
                            .background(Color(0xFFF3E5F5))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(child.avatar),
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(child.name, fontWeight = FontWeight.Medium)
                            Text("${child.age} yaş", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}
