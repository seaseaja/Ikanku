package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun LoginScreen(
    viewModel: com.example.viewmodel.JastipViewModel,
    onLoginSuccess: (role: String) -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var isLoggingIn by remember { mutableStateOf(false) }
    var showForgotPasswordToast by remember { mutableStateOf(false) }
    var showEmailNotRegisteredDialog by remember { mutableStateOf(false) }
    var showNotJastiperWarning by remember { mutableStateOf(false) }
    var loginRole by remember { mutableStateOf("pembeli") }

    val scrollState = rememberScrollState()

    // Validate inputs helper
    fun handleAuthentication() {
        var isValid = true
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
        } else {
            passwordError = null
        }

        if (isValid) {
            // ✅ TAMBAHAN: Cek hardcoded admin credentials
            if (email.trim() == "admin@mai.com" && password == "admin#123") {
                isLoggingIn = false
                onLoginSuccess("admin")
                return
            }

            isLoggingIn = true
            viewModel.login(email, password, loginRole) { success, actualRole, message ->
                isLoggingIn = false
                if (success) {
                    // Pengecekan status Jastiper: Jika pengguna masuk melalui tab Jastiper, 
                    // pastikan role yang dikembalikan dari sistem adalah 'jastiper'.
                    if (loginRole == "jastiper" && actualRole != "jastiper") {
                        showNotJastiperWarning = true
                    } else {
                        // Jika sudah sesuai (Tab Pelanggan -> Role Pelanggan, atau Tab Jastiper -> Role Jastiper)
                        onLoginSuccess(loginRole)
                    }
                } else {
                    if (message?.contains("tidak ditemukan", ignoreCase = true) == true || 
                        message?.contains("belum terdaftar", ignoreCase = true) == true) {
                        showEmailNotRegisteredDialog = true
                    } else {
                        passwordError = message ?: "Email atau password salah"
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE9EEF4)) // Soft dark grey-blue background from design screenshot
            .padding(vertical = 24.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp)
                .verticalScroll(scrollState)
                .testTag("login_card"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFD5DBE4)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Programmatic Dynamic Gradient Fish Logo
                IkanKuGradientLogo(
                    modifier = Modifier
                        .size(90.dp)
                        .testTag("app_fish_logo")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Main Branding Text
                Text(
                    text = "IkanKu",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF102A43), // Rich dark slate navy text
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Menghubungkan kesegaran laut ke meja makan Anda.",
                    fontSize = 13.sp,
                    color = Color(0xFF627D98), // Professional typography gray text
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Elegant Segmented Control for Login Role selection matching brand aesthetics
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEDEFF2))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (loginRole == "pembeli") Color(0xFF1B4E8C) else Color.Transparent)
                            .clickable { loginRole = "pembeli" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Pelanggan",
                            fontWeight = FontWeight.Bold,
                            color = if (loginRole == "pembeli") Color.White else Color(0xFF102A43),
                            fontSize = 13.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (loginRole == "jastiper") Color(0xFF1B4E8C) else Color.Transparent)
                            .clickable { loginRole = "jastiper" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Jastiper",
                            fontWeight = FontWeight.Bold,
                            color = if (loginRole == "jastiper") Color.White else Color(0xFF102A43),
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 3. Email Input Box
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Email",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF102A43),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (emailError != null) emailError = null
                        },
                        placeholder = { Text("Emailmu@gmail.com", color = Color(0xFF9FB3C8)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email Icon",
                                tint = Color(0xFF829AB1)
                            )
                        },
                        trailingIcon = {
                            if (email.isNotEmpty()) {
                                IconButton(onClick = { email = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Hapus tulisan",
                                        tint = Color(0xFF829AB1)
                                    )
                                }
                            }
                        },
                        isError = emailError != null,
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF184E8C), // Exact signature dark blue border
                            unfocusedBorderColor = Color(0xFFD5DBE4),
                            focusedContainerColor = Color(0xFFEDEFF2), // Subtle light input background matching screenshot
                            unfocusedContainerColor = Color(0xFFEDEFF2),
                            focusedTextColor = Color(0xFF102A43),
                            unfocusedTextColor = Color(0xFF102A43)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_email")
                    )

                    emailError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 4. Password Input Box
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Password",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF102A43)
                        )

                        Text(
                            text = "Forgot Password?",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = Color(0xFF184E8C), // High fidelity blue action link
                            modifier = Modifier
                                .clickable { showForgotPasswordToast = true }
                                .testTag("btn_forgot_password")
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (passwordError != null) passwordError = null
                        },
                        placeholder = { Text("**********", color = Color(0xFF9FB3C8)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password Icon",
                                tint = Color(0xFF829AB1)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Tampilkan Password Toggle",
                                    tint = Color(0xFF829AB1)
                                )
                            }
                        },
                        isError = passwordError != null,
                        singleLine = true,
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF184E8C),
                            unfocusedBorderColor = Color(0xFFD5DBE4),
                            focusedContainerColor = Color(0xFFEDEFF2),
                            unfocusedContainerColor = Color(0xFFEDEFF2),
                            focusedTextColor = Color(0xFF102A43),
                            unfocusedTextColor = Color(0xFF102A43)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_password")
                    )

                    passwordError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // 5. Submit Login Action Button
                Button(
                    onClick = { handleAuthentication() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4E8C)),
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_log_in"),
                    enabled = !isLoggingIn
                ) {
                    if (isLoggingIn) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "Log In",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 6. "Atau login Dengan" Horizontal line divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(Color(0xFFD5DBE4))
                    )
                    Text(
                        text = "Atau login Dengan",
                        fontSize = 11.sp,
                        color = Color(0xFF829AB1),
                        modifier = Modifier.padding(horizontal = 12.dp),
                        fontWeight = FontWeight.Medium
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(Color(0xFFD5DBE4))
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 7. Social icons (Google, Discord)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Google circular action button
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(BorderStroke(1.5.dp, Color(0xFFD5DBE4)), CircleShape)
                            .clickable {
                                // Simulate OAuth Login
                                email = "admin@mail.com"
                                password = "rahasia"
                                handleAuthentication()
                            }
                            .testTag("btn_login_google"),
                        contentAlignment = Alignment.Center
                    ) {
                        GoogleLogoIcon(modifier = Modifier.size(24.dp))
                    }

                    // Discord circular action button
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(BorderStroke(1.5.dp, Color(0xFFD5DBE4)), CircleShape)
                            .clickable {
                                // Simulate OAuth Login
                                email = "asep@mail.com"
                                password = "123"
                                handleAuthentication()
                            }
                            .testTag("btn_login_discord"),
                        contentAlignment = Alignment.Center
                    ) {
                        DiscordLogoIcon(modifier = Modifier.size(34.dp))
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // 8. Sign-Up reference text bottom bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Belum Punya Akun? ",
                        fontSize = 13.sp,
                        color = Color(0xFF627D98)
                    )

                    Text(
                        text = "Daftar sekarang",
                        fontSize = 13.sp,
                        color = Color(0xFF1B4E8C),
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .clickable { onSignUpClick() }
                            .testTag("btn_sign_up")
                    )
                }
            }
        }
    }

    if (showEmailNotRegisteredDialog) {
        AlertDialog(
            onDismissRequest = { showEmailNotRegisteredDialog = false },
            confirmButton = {
                Button(
                    onClick = { 
                        showEmailNotRegisteredDialog = false
                        onSignUpClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4E8C))
                ) {
                    Text("Daftar Sekarang")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEmailNotRegisteredDialog = false }) {
                    Text("Batal", color = Color(0xFF627D98))
                }
            },
            title = { Text("Email Belum Terdaftar") },
            text = { Text("Akun dengan email ini tidak ditemukan di sistem kami. Apakah Anda ingin mendaftar akun baru?") },
            containerColor = Color.White
        )
    }

    if (showNotJastiperWarning) {
        AlertDialog(
            onDismissRequest = { showNotJastiperWarning = false },
            confirmButton = {
                Button(
                    onClick = { 
                        showNotJastiperWarning = false
                        onSignUpClick() // Diarahkan ke register untuk daftar jastip
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4E8C))
                ) {
                    Text("Daftar Jastiper")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotJastiperWarning = false }) {
                    Text("Batal", color = Color(0xFF627D98))
                }
            },
            title = { Text("Bukan Akun Jastiper") },
            text = { Text("Maaf, akun Anda saat ini hanya terdaftar sebagai Pelanggan. Silakan mendaftar sebagai Jastip terlebih dahulu untuk mengakses fitur ini. Anda dapat menggunakan email yang sama untuk melakukan upgrade akun.") },
            containerColor = Color.White
        )
    }

    if (showForgotPasswordToast) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordToast = false },
            confirmButton = {
                Button(
                    onClick = { showForgotPasswordToast = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4E8C))
                ) {
                    Text("OK")
                }
            },
            title = { Text("Lupa Kata Sandi?") },
            text = { Text("Tautan pemulihan sandi sudah disetup untuk dikirim ke email Anda. Cukup gunakan email & sandi demo apa saja (min. 8 karakter dengan kombinasi huruf & angka) untuk login instan di sistem IkanKu!") },
            containerColor = Color.White
        )
    }
}

/**
 * IkanKu Logo
 */
@Composable
fun IkanKuGradientLogo(modifier: Modifier = Modifier) {
    androidx.compose.foundation.Image(
        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.logo_ikan),
        contentDescription = "IkanKu Logo",
        modifier = modifier
    )
}

/**
 * Google Logo Icon
 */
@Composable
fun GoogleLogoIcon(modifier: Modifier = Modifier) {
    androidx.compose.foundation.Image(
        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.google_logo),
        contentDescription = "Google Logo",
        modifier = modifier
    )
}

/**
 * Discord Logo Icon
 */
@Composable
fun DiscordLogoIcon(modifier: Modifier = Modifier) {
    androidx.compose.foundation.Image(
        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.discord_logo),
        contentDescription = "Discord Logo",
        modifier = modifier
    )
}
