package com.example.otigoapp.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.otigoapp.ui.expert.ExpertPanelActivity
import com.example.otigoapp.ui.parent.ParentMainPanelActivity
import com.example.otigoapp.ui.parent.ParentSymptomFormActivity
import com.example.otigoapp.ui.theme.OtiGoAppTheme

class RoleSelectActivity : ComponentActivity() {

    companion object {
        const val EXTRA_ROLE = "selected_role"
        const val ROLE_PARENT = "parent"
        const val ROLE_EXPERT = "expert"
    }

    // ✅ VELİ FORMU → RESULT OK GELİRSE VELİ PANELİNE GİDİLİR
    private val parentFormLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult

            val data = result.data ?: return@registerForActivityResult

            val intent = Intent(this, ParentMainPanelActivity::class.java).apply {
                putExtras(data) // child_name & child_age
            }

            startActivity(intent)
            finish()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OtiGoAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RoleSelectScreen(
                        onSelectParent = {
                            // 🔥 SADECE FORM AÇILIR
                            parentFormLauncher.launch(
                                Intent(this, ParentSymptomFormActivity::class.java)
                            )
                        },
                        onSelectExpert = {
                            val i = Intent(this, ExpertPanelActivity::class.java)
                            i.putExtra(EXTRA_ROLE, ROLE_EXPERT)
                            startActivity(i)
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RoleSelectScreen(
    onSelectParent: () -> Unit,
    onSelectExpert: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Profilinizi Seçin",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // 👨‍👩‍👧 VELİ
        RoleCard(
            title = "Veli",
            subtitle = "Çocuğunu takip et",
            tint = Color(0xFFB9F227),
            icon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = Color(0xFF222222),
                    modifier = Modifier.size(32.dp)
                )
            },
            onClick = onSelectParent
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 👩‍⚕️ UZMAN
        RoleCard(
            title = "Uzman",
            subtitle = "Danışman yönetimi",
            tint = Color(0xFF90CAF9),
            icon = {
                Icon(
                    imageVector = Icons.Filled.VerifiedUser,
                    contentDescription = null,
                    tint = Color(0xFF1E88E5),
                    modifier = Modifier.size(32.dp)
                )
            },
            onClick = onSelectExpert
        )
    }
}

@Composable
fun RoleCard(
    title: String,
    subtitle: String,
    tint: Color,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(tint.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}
