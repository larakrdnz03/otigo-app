package com.example.otigoapp.ui.parent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.otigoapp.ui.theme.OtiGoAppTheme
import kotlinx.coroutines.launch

class ParentSymptomFormActivity : ComponentActivity() {

    companion object {
        const val EXTRA_CHILD_NAME = "child_name"
        const val EXTRA_CHILD_AGE = "child_age"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OtiGoAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ParentSymptomFormScreen(
                        onBack = {
                            // ⬅️ GERİ: ASLA çocuk ekleme
                            setResult(Activity.RESULT_CANCELED)
                            finish()
                        },
                        onSubmit = { name, age ->
                            // ✅ ÇOCUK SADECE BURADA EKLENİR
                            val resultIntent = Intent().apply {
                                putExtra(EXTRA_CHILD_NAME, name)
                                putExtra(EXTRA_CHILD_AGE, age)
                            }
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ParentSymptomFormScreen(
    onBack: () -> Unit,
    onSubmit: (String, Int) -> Unit
) {
    var childName by rememberSaveable { mutableStateOf("") }
    var childAge by rememberSaveable { mutableStateOf("") }

    var eyeContact by rememberSaveable { mutableStateOf("") }
    var repetitiveMoves by rememberSaveable { mutableStateOf("") }
    var socialDifficulty by rememberSaveable { mutableStateOf("") }
    var otherSymptoms by rememberSaveable { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // 🔙 GERİ BUTONU
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Geri",
                    tint = Color(0xFF9C27B0)
                )
            }

            Spacer(Modifier.height(8.dp))

            // 🟣 BAŞLIK
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF9C27B0), RoundedCornerShape(16.dp))
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Çocuk Belirti Formu",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(Modifier.height(20.dp))

            Text("Çocuğunuzun Adı Soyadı:")
            OutlinedTextField(
                value = childName,
                onValueChange = { childName = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Text("Çocuğunuzun Yaşı:")
            OutlinedTextField(
                value = childAge,
                onValueChange = { childAge = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            FormQuestion(
                "Göz Teması Kuruyor mu?",
                listOf("Evet", "Hayır", "Bazen"),
                eyeContact
            ) { eyeContact = it }

            FormQuestion(
                "Tekrarlayan Hareketleri Var mı?",
                listOf("Evet", "Hayır", "Bazen"),
                repetitiveMoves
            ) { repetitiveMoves = it }

            FormQuestion(
                "Sosyal Etkileşim Kurmakta Zorlanıyor mu?",
                listOf("Evet", "Hayır", "Bazen"),
                socialDifficulty
            ) { socialDifficulty = it }

            Spacer(Modifier.height(12.dp))

            Text("Diğer Belirtiler:")
            OutlinedTextField(
                value = otherSymptoms,
                onValueChange = { otherSymptoms = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (
                        childName.isBlank() ||
                        childAge.isBlank() ||
                        eyeContact.isBlank() ||
                        repetitiveMoves.isBlank() ||
                        socialDifficulty.isBlank()
                    ) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                "Lütfen tüm zorunlu alanları doldurunuz"
                            )
                        }
                        return@Button
                    }

                    onSubmit(childName, childAge.toInt())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Gönder", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FormQuestion(
    question: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Column {
        Text(question, fontWeight = FontWeight.Medium)
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            options.forEach { option ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selected == option,
                        onClick = { onSelected(option) }
                    )
                    Text(option)
                }
            }
        }
    }
}
