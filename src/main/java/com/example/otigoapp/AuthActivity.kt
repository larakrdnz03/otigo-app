package com.example.otigoapp


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.otigoapp.data.api.RetrofitClient
import com.example.otigoapp.data.model.LoginRequest
import com.example.otigoapp.data.model.RegisterRequest
import com.example.otigoapp.ui.auth.RoleSelectActivity
import com.example.otigoapp.ui.auth.data.TokenStore
import kotlinx.coroutines.launch

/**
 * AUTH ACTIVITY
 */
class AuthActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TokenStore init (UI DIŞINDA – doğru yer)
        val tokenStore = TokenStore(applicationContext)

        setContent {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()

            MaterialTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF6F7F9)),
                    contentAlignment = Alignment.Center
                ) {
                    AuthCard(
                        onSubmit = { mode, email, pass ->

                            scope.launch {
                                try {
                                    val response = if (mode == AuthMode.Login) {
                                        // LOGIN
                                        val req = LoginRequest(email, pass)
                                        RetrofitClient.api.login(req)
                                    } else {
                                        // REGISTER
                                        val req = RegisterRequest(
                                            firstname = "Test",
                                            lastname = "User",
                                            email = email,
                                            password = pass,
                                            role = "VELI"
                                        )
                                        RetrofitClient.api.register(req)
                                    }

                                    if (response.isSuccessful && response.body() != null) {
                                        val token = response.body()!!.access_token

                                        // Token kaydet
                                        tokenStore.saveToken(token)

                                        Toast.makeText(
                                            context,
                                            "Giriş Başarılı! 🔓",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        // Ekran geçişi
                                        val intent = Intent(context, RoleSelectActivity::class.java)
                                        context.startActivity(intent)
                                        (context as? Activity)?.finish()

                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Hata: ${response.code()}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }

                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Bağlantı Hatası: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    e.printStackTrace()
                                }
                            }
                        },
                        onForgotPassword = {
                            // İleride eklenecek
                        }
                    )
                }
            }
        }
    }
}

/* ---------------------- UI ---------------------- */

private enum class AuthMode { Login, SignUp }

@Composable
private fun AuthCard(
    onSubmit: (mode: AuthMode, email: String, password: String) -> Unit,
    onForgotPassword: () -> Unit
) {
    var mode by rememberSaveable { mutableStateOf(AuthMode.Login) }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(16.dp)
            .widthIn(max = 380.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {

            AuthTabs(selected = mode, onSelect = { mode = it })

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email address") },
                placeholder = { Text("Enter your email address") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                placeholder = { Text("Enter your password") },
                singleLine = true,
                visualTransformation =
                    if (showPassword) VisualTransformation.None
                    else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector =
                                if (showPassword) Icons.Outlined.VisibilityOff
                                else Icons.Outlined.RemoveRedEye,
                            contentDescription = "Toggle password"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (mode == AuthMode.Login) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = "Forgot password?",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color(0xFF6B7280),
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clickable { onForgotPassword() }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { onSubmit(mode, email.trim(), password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF111827),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (mode == AuthMode.Login) "Log In" else "Create an account",
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                val prompt =
                    if (mode == AuthMode.Login) "Don't have an account yet? "
                    else "Already have an account? "
                val action =
                    if (mode == AuthMode.Login) "Sign up" else "Login"

                Text(text = prompt, color = Color(0xFF6B7280), fontSize = 13.sp)
                Spacer(Modifier.width(2.dp))
                Text(
                    text = action,
                    color = Color(0xFF111827),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable {
                        mode =
                            if (mode == AuthMode.Login) AuthMode.SignUp
                            else AuthMode.Login
                    }
                )
            }
        }
    }
}

@Composable
private fun AuthTabs(selected: AuthMode, onSelect: (AuthMode) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF3F4F6))
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ToggleChip(
            text = "Login",
            active = selected == AuthMode.Login,
            onClick = { onSelect(AuthMode.Login) },
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        ToggleChip(
            text = "Sign Up",
            active = selected == AuthMode.SignUp,
            onClick = { onSelect(AuthMode.SignUp) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ToggleChip(
    text: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    Surface(
        modifier = modifier
            .height(36.dp)
            .clip(shape)
            .clickable { onClick() },
        color = if (active) Color.White else Color.Transparent,
        shadowElevation = if (active) 1.dp else 0.dp,
        border = if (active)
            ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
        else null
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontWeight =
                    if (active) FontWeight.SemiBold else FontWeight.Medium,
                color =
                    if (active) Color(0xFF111827) else Color(0xFF6B7280),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}
