package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SignUpScreen(
    viewModel: com.example.viewmodel.JastipViewModel,
    onSignUpSuccess: () -> Unit,
    onJastipSignUpClick: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRole by remember { mutableStateOf("Pelanggan") }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    
    var isRegistering by remember { mutableStateOf(false) }
    var showRegistrationSuccessDialog by remember { mutableStateOf(false) }
    
    val scrollState = rememberScrollState()

    fun handleRegistration() {
        var isValid = true
        
        if (fullName.trim().isEmpty()) {
            fullNameError = "Nama lengkap harus diisi"
            isValid = false
        } else {
            fullNameError = null
        }

        if (email.trim().isEmpty()) {
            emailError = "Email tidak boleh kosong"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            emailError = "Format email tidak valid"
            isValid = false
        } else {
            emailError = null
        }

        if (password.trim().isEmpty()) {
            passwordError = "Password tidak boleh kosong"
            isValid = false
        } else if (password.length < 8) {
            passwordError = "Password minimal terdiri dari 8 karakter"
            isValid = false
        } else if (password.all { it.isDigit() } || password.all { it.isLetter() }) {
            passwordError = "Kata sandi lemah (gunakan kombinasi huruf dan angka)"
            isValid = false
        } else {
            passwordError = null
        }

        if (confirmPassword.trim().isEmpty()) {
            confirmPasswordError = "Konfirmasi password tidak boleh kosong"
            isValid = false
        } else if (confirmPassword != password) {
            confirmPasswordError = "Konfirmasi password tidak cocok dengan password"
            isValid = false
        } else {
            confirmPasswordError = null
        }

        if (isValid) {
            if (selectedRole == "Jastip") {
                // Untuk Jastip, kita arahkan ke screen pendaftaran multi-step
                onJastipSignUpClick()
            } else {
                isRegistering = true
                val apiRole = when(selectedRole) {
                    "Pelanggan" -> "pembeli"
                    else -> "pembeli"
                }
                viewModel.register(email, password, apiRole, fullName) { success, message ->
                    isRegistering = false
                    if (success) {
                        showRegistrationSuccessDialog = true
                    } else {
                        emailError = message ?: "Gagal mendaftar. Silakan coba lagi."
                    }
                }
            }
        }
    }

    if (selectedRole == "Jastip") {
        JastipRegisterScreen(
            viewModel = viewModel, // Inject viewModel
            onRegistrationCompleted = onSignUpSuccess,
            onBackToLogin = onLoginClick,
            onRoleChange = { newRole -> selectedRole = newRole },
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFE9EEF4))
                .padding(vertical = 16.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
                    .verticalScroll(scrollState),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFD5DBE4))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IkanKuGradientLogo(modifier = Modifier.size(90.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "IkanKu", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF102A43))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Menghubungkan kesegaran laut ke meja makan Anda.", fontSize = 13.sp, color = Color(0xFF627D98), textAlign = TextAlign.Center)

                    Spacer(modifier = Modifier.height(28.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val roles = listOf("Pelanggan", "Jastip")
                        roles.forEach { role ->
                            val isSelected = selectedRole == role
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Color(0xFF1B4E8C) else Color.White)
                                    .border(if (isSelected) BorderStroke(0.dp, Color.Transparent) else BorderStroke(1.dp, Color(0xFF1B4E8C)), RoundedCornerShape(12.dp))
                                    .clickable { selectedRole = role },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = role, color = if (isSelected) Color.White else Color(0xFF1B4E8C), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Form Fields
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it; fullNameError = null },
                        label = { Text("Nama Lengkap") },
                        placeholder = { Text("Sesuai KTP") },
                        isError = fullNameError != null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    fullNameError?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; emailError = null },
                        label = { Text("Email") },
                        placeholder = { Text("Emailmu@gmail.com") },
                        isError = emailError != null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    emailError?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; passwordError = null },
                        label = { Text("Password") },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = passwordError != null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                            }
                        }
                    )
                    passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; confirmPasswordError = null },
                        label = { Text("Confirmasi Password") },
                        visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = confirmPasswordError != null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                                Icon(if (isConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                            }
                        }
                    )
                    confirmPasswordError?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = { handleRegistration() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4E8C)),
                        shape = CircleShape,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = !isRegistering
                    ) {
                        if (isRegistering) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        else Text("Sign In", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Sudah Punya Akun? ", fontSize = 13.sp, color = Color(0xFF627D98))
                        Text("Login sekarang", fontSize = 13.sp, color = Color(0xFF1B4E8C), fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline, modifier = Modifier.clickable { onLoginClick() })
                    }
                }
            }
        }
    }

    if (showRegistrationSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showRegistrationSuccessDialog = false; onLoginClick() },
            confirmButton = { Button(onClick = { showRegistrationSuccessDialog = false; onLoginClick() }) { Text("OK") } },
            title = { Text("Registrasi Berhasil") },
            text = { Text("Akun Anda telah berhasil dibuat. Silakan login.") }
        )
    }
}
