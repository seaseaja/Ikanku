package com.example.ui.screens

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JastipRegisterScreen(
    viewModel: com.example.viewmodel.JastipViewModel,
    onRegistrationCompleted: () -> Unit,
    onBackToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onRoleChange: (String) -> Unit = { _ -> onBackToLogin() }
) {
    // Current register step (1 = Biodata, 2 = Verifikasi, 3 = Operasional)
    var currentStep by remember { mutableIntStateOf(1) }

    // --- State Step 1: Personal Data / Biodata ---
    var namaLengkap by remember { mutableStateOf("") }
    var whatsappNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isProfilePicUploaded by remember { mutableStateOf(true) } 
    
    // Validation Errors
    var namaError by remember { mutableStateOf<String?>(null) }
    var whatsappError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // --- State Step 2: Verification ---
    val otpValues = remember { mutableStateListOf("", "", "", "", "", "") }
    var isKtpPicUploaded by remember { mutableStateOf(false) }
    var otpError by remember { mutableStateOf<String?>(null) }
    var ktpError by remember { mutableStateOf<String?>(null) }

    // --- State Step 3: Operational ---
    var selectedBank by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var accountHolderName by remember { mutableStateOf("") }
    var listOpened by remember { mutableStateOf(false) }
    
    var bankError by remember { mutableStateOf<String?>(null) }
    var accountNoError by remember { mutableStateOf<String?>(null) }
    var accountHolderError by remember { mutableStateOf<String?>(null) }

    val operationalAreas = remember {
        mutableStateListOf(
            "Pasar Ciroyom" to true,
            "Pasar Caringin" to false,
            "Pasar Kosambi" to false,
            "Pasar Muara Baru" to true,
            "Pasar Tambak Lorok" to false
        )
    }
    var showAddAreaDialog by remember { mutableStateOf(false) }
    var newAreaName by remember { mutableStateOf("") }

    var showDialogSummary by remember { mutableStateOf(false) }
    var registrationSuccess by remember { mutableStateOf(false) }
    var customSuccessMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()
    val userProfile by viewModel.userProfile.collectAsState()

    val isUpgradeMode = remember(userProfile) {
        userProfile.email.isNotEmpty() && userProfile.email != "admin@mail.com"
    }

    LaunchedEffect(userProfile) {
        if (isUpgradeMode) {
            if (namaLengkap.isEmpty()) namaLengkap = userProfile.fullName
            if (email.isEmpty()) email = userProfile.email
            if (whatsappNumber.isEmpty()) whatsappNumber = userProfile.phoneNumber
        }
    }

    val navyColor = Color(0xFF102A43)
    val infoTextColor = Color(0xFF627D98)
    val signatureBlue = Color(0xFF1B4E8C)
    val successTeal = Color(0xFF50C5B0)
    val softInputColor = Color(0xFFEDEFF2)
    val borderColor = Color(0xFFD5DBE4)

    fun validateStep1(): Boolean {
        var isValid = true
        if (namaLengkap.trim().isEmpty()) {
            namaError = "Nama lengkap harus diisi sesuai KTP"
            isValid = false
        } else { namaError = null }

        if (whatsappNumber.trim().isEmpty()) {
            whatsappError = "Nomor WhatsApp aktif diperlukan"
            isValid = false
        } else if (whatsappNumber.length < 9) {
            whatsappError = "Format nomor WhatsApp tidak valid"
            isValid = false
        } else { whatsappError = null }

        if (email.trim().isEmpty()) {
            emailError = "Email tidak boleh kosong"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            emailError = "Format email tidak valid"
            isValid = false
        } else { emailError = null }

        if (password.isEmpty()) {
            passwordError = "Password wajib diisi"
            isValid = false
        } else if (password.length < 8) {
            passwordError = "Password minimal 8 karakter"
            isValid = false
        } else { passwordError = null }

        return isValid
    }

    fun validateStep2(): Boolean {
        var isValid = true
        val fullOtp = otpValues.joinToString("")
        if (fullOtp.length < 6) {
            otpError = "Masukkan kode OTP 6 digit"
            isValid = false
        } else { otpError = null }

        if (!isKtpPicUploaded) {
            ktpError = "Harap unggah KTP berserta foto wajah Anda"
            isValid = false
        } else { ktpError = null }

        return isValid
    }

    fun validateStep3(): Boolean {
        var isValid = true
        if (selectedBank.isEmpty()) {
            bankError = "Pilih bank Anda"
            isValid = false
        } else { bankError = null }

        if (accountNumber.trim().isEmpty()) {
            accountNoError = "Nomor rekening tidak boleh kosong"
            isValid = false
        } else { accountNoError = null }

        if (accountHolderName.trim().isEmpty()) {
            accountHolderError = "Nama pemilik rekening diperlukan"
            isValid = false
        } else { accountHolderError = null }

        return isValid
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFEFF3F8))
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .widthIn(max = 500.dp)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Logo (Centered)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    modifier = Modifier.size(52.dp),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF1B4E8C),
                                        Color(0xFF102A43)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = com.example.R.drawable.logo_ikan),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(0.7f)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "IkanKu",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = navyColor,
                    letterSpacing = (-0.5).sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Menghubungkan kesegaran laut ke meja makan Anda.",
                fontSize = 13.sp,
                color = infoTextColor,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = Color(0xFFFFF8E1),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFFFFD54F)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color(0xFFF57F17), modifier = Modifier.size(20.dp))
                    Text(
                        text = "Jika Anda sebelumnya sudah terdaftar sebagai pengguna biasa dan ingin meng-upgrade akun menjadi Jastiper, Anda dapat menggunakan alamat email yang sama. Sistem akan memperbarui status akun Anda tanpa perlu membuat akun baru.",
                        fontSize = 12.sp,
                        color = Color(0xFF7F4E00),
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Pelanggan", "Jastip").forEach { tab ->
                    val isSelected = tab == "Jastip"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) navyColor else Color.White)
                            .then(if (!isSelected) Modifier.border(BorderStroke(1.dp, signatureBlue), RoundedCornerShape(10.dp)) else Modifier)
                            .clickable { if (tab == "Pelanggan") onRoleChange(tab) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = tab, color = if (isSelected) Color.White else signatureBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), contentAlignment = Alignment.TopCenter) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 19.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.weight(1f).height(2.dp).background(if (currentStep >= 2) successTeal else borderColor))
                    Box(modifier = Modifier.weight(1f).height(2.dp).background(if (currentStep >= 3) successTeal else borderColor))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    StepBubbleItem(1, "Biodata", currentStep == 1, currentStep > 1, navyColor, successTeal, infoTextColor)
                    StepBubbleItem(2, "Verifikasi", currentStep == 2, currentStep > 2, navyColor, successTeal, infoTextColor)
                    StepBubbleItem(3, "Operasional", currentStep == 3, false, navyColor, successTeal, infoTextColor)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            when (currentStep) {
                1 -> BiodataStepLayout(
                    namaLengkap = namaLengkap,
                    onNamaChange = { namaLengkap = it; namaError = null },
                    namaError = namaError,
                    whatsappNumber = whatsappNumber,
                    onWhatsappChange = { whatsappNumber = it; whatsappError = null },
                    whatsappError = whatsappError,
                    email = email,
                    onEmailChange = { email = it; emailError = null },
                    emailError = emailError,
                    password = password,
                    onPasswordChange = { password = it; passwordError = null },
                    passwordError = passwordError,
                    isPasswordVisible = isPasswordVisible,
                    onPasswordVisibilityToggle = { isPasswordVisible = !isPasswordVisible },
                    onUploadProfileClick = { isProfilePicUploaded = !isProfilePicUploaded },
                    onNextStep = { if (validateStep1()) currentStep = 2 },
                    infoTextColor = infoTextColor,
                    navyColor = navyColor,
                    softInputColor = softInputColor,
                    signatureBlue = signatureBlue,
                    borderColor = borderColor
                )
                2 -> VerifikasiStepLayout(
                    otpValues = otpValues,
                    onOtpChange = { idx, v -> otpValues[idx] = v; otpError = null },
                    otpError = otpError,
                    whatsappNumber = whatsappNumber.ifEmpty { "0812-3456-7890" },
                    isKtpPicUploaded = isKtpPicUploaded,
                    onUploadKtpClick = { isKtpPicUploaded = !isKtpPicUploaded; ktpError = null },
                    ktpError = ktpError,
                    onNextStep = { if (!isKtpPicUploaded) isKtpPicUploaded = true else if (validateStep2()) currentStep = 3 },
                    onBackStep = { currentStep = 1 },
                    infoTextColor = infoTextColor,
                    navyColor = navyColor,
                    signatureBlue = signatureBlue,
                    softInputColor = softInputColor,
                    borderColor = borderColor
                )
                3 -> OperasionalStepLayout(
                    selectedBank = selectedBank,
                    onBankSelect = { selectedBank = it; bankError = null },
                    bankError = bankError,
                    accountNumber = accountNumber,
                    onAccountNoChange = { accountNumber = it; accountNoError = null },
                    accountNoError = accountNoError,
                    accountHolderName = accountHolderName,
                    onAccountHolderChange = { accountHolderName = it; accountHolderError = null },
                    accountHolderError = accountHolderError,
                    operationalAreas = operationalAreas,
                    onToggleArea = { idx -> val curr = operationalAreas[idx]; operationalAreas[idx] = curr.first to !curr.second },
                    onAddAreaClick = { showAddAreaDialog = true },
                    onFinishRegistration = { if (validateStep3()) showDialogSummary = true },
                    onBackStep = { currentStep = 2 },
                    listOpened = listOpened,
                    onToggleList = { listOpened = !listOpened },
                    infoTextColor = infoTextColor,
                    navyColor = navyColor,
                    signatureBlue = signatureBlue,
                    softInputColor = softInputColor,
                    borderColor = borderColor
                )
            }
        }

        // Back Button (Fixed at the top of the Box)
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(top = 8.dp, start = 8.dp)
                .align(Alignment.TopStart)
                .testTag("btn_back_register")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                contentDescription = "Kembali",
                tint = navyColor
            )
        }
    }

    if (showAddAreaDialog) {
        AlertDialog(
            onDismissRequest = { showAddAreaDialog = false },
            confirmButton = { Button(onClick = { if (newAreaName.trim().isNotEmpty()) { operationalAreas.add(newAreaName.trim() to true); newAreaName = ""; showAddAreaDialog = false } }, colors = ButtonDefaults.buttonColors(containerColor = signatureBlue)) { Text("Tambah") } },
            dismissButton = { TextButton(onClick = { showAddAreaDialog = false }) { Text("Batal", color = infoTextColor) } },
            title = { Text("Tambah Area Operasional") },
            text = { OutlinedTextField(value = newAreaName, onValueChange = { newAreaName = it }, placeholder = { Text("Nama pasar/wilayah...", color = Color(0xFF9FB3C8)) }, singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = signatureBlue, unfocusedBorderColor = borderColor), modifier = Modifier.fillMaxWidth()) },
            containerColor = Color.White
        )
    }

    if (showDialogSummary) {
        var isSubmitting by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        AlertDialog(
            onDismissRequest = { if (!isSubmitting) showDialogSummary = false },
            confirmButton = {
                if (registrationSuccess) {
                    Button(onClick = { showDialogSummary = false; onRegistrationCompleted() }, colors = ButtonDefaults.buttonColors(containerColor = signatureBlue), modifier = Modifier.fillMaxWidth()) { Text(if (isUpgradeMode) "Mulai Berjastip" else "Ke Halaman Login") }
                } else {
                    Button(onClick = { isSubmitting = true; viewModel.register(email, password, "jastiper", namaLengkap, whatsappNumber) { s, m -> isSubmitting = false; if (s) { registrationSuccess = true; customSuccessMessage = m } else { errorMessage = m } } }, colors = ButtonDefaults.buttonColors(containerColor = signatureBlue), modifier = Modifier.fillMaxWidth(), enabled = !isSubmitting) {
                        if (isSubmitting) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp)) else Text(if (isUpgradeMode) "Konfirmasi & Upgrade" else "Konfirmasi & Daftar")
                    }
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(imageVector = if (registrationSuccess) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.Assignment, contentDescription = null, tint = if (registrationSuccess) successTeal else signatureBlue, modifier = Modifier.size(28.dp))
                    Text(text = if (registrationSuccess) (if (isUpgradeMode) "Upgrade Berhasil" else "Registrasi Berhasil") else (if (isUpgradeMode) "Ringkasan Upgrade Akun" else "Ringkasan Pendaftaran"), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = navyColor)
                }
            },
            text = {
                if (registrationSuccess) {
                    Text(text = customSuccessMessage ?: (if (isUpgradeMode) "Akun Anda telah berhasil diperbarui menjadi Jastiper. Sekarang Anda dapat mulai menerima pesanan." else "Akun Jastiper Anda telah berhasil dibuat. Silakan login untuk mulai melayani pelanggan."), fontSize = 14.sp, color = navyColor, modifier = Modifier.padding(vertical = 8.dp))
                } else {
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        errorMessage?.let { Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }
                        Text("Silakan verifikasi ringkasan registrasi Jastip Anda:")
                        HorizontalDivider(color = borderColor)
                        Text(text = "BIODATA", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = signatureBlue)
                        Text("• Nama Lengkap: $namaLengkap", fontSize = 13.sp, color = navyColor)
                        Text("• WhatsApp: $whatsappNumber", fontSize = 13.sp, color = navyColor)
                        Text("• Email: $email", fontSize = 13.sp, color = navyColor)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "VERIFIKASI IDENTITAS", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = signatureBlue)
                        Text("• Status OTP: Terverifikasi (Sukses)", fontSize = 13.sp, color = navyColor)
                        Text("• Foto KTP: Terupload (Berhasil)", fontSize = 13.sp, color = navyColor)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "OPERASIONAL JASTIP", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = signatureBlue)
                        Text("• Rekening Bank: $selectedBank - $accountNumber", fontSize = 13.sp, color = navyColor)
                        Text("• Pemilik: $accountHolderName", fontSize = 13.sp, color = navyColor)
                        Text("• Area: ${operationalAreas.filter { it.second }.joinToString(", ") { it.first }}", fontSize = 13.sp, color = navyColor)
                    }
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
fun StepBubbleItem(stepNumber: Int, label: String, isCurrent: Boolean, isSuccess: Boolean, activeColor: Color, successColor: Color, inactiveColor: Color) {
    Column(modifier = Modifier.width(76.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        val circleBg = when { isSuccess -> successColor; isCurrent -> activeColor; else -> Color.White }
        val circleBorder = if (isSuccess || isCurrent) null else BorderStroke(1.5.dp, activeColor)
        Box(modifier = Modifier.size(38.dp).clip(CircleShape).background(circleBg).then(if (circleBorder != null) Modifier.border(circleBorder, CircleShape) else Modifier), contentAlignment = Alignment.Center) {
            Text(text = stepNumber.toString(), color = if (isSuccess || isCurrent) Color.White else activeColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, fontSize = 12.sp, fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium, color = if (isCurrent) activeColor else inactiveColor, textAlign = TextAlign.Center)
    }
}

@Composable
fun BiodataStepLayout(namaLengkap: String, onNamaChange: (String) -> Unit, namaError: String?, whatsappNumber: String, onWhatsappChange: (String) -> Unit, whatsappError: String?, email: String, onEmailChange: (String) -> Unit, emailError: String?, password: String, onPasswordChange: (String) -> Unit, passwordError: String?, isPasswordVisible: Boolean, onPasswordVisibilityToggle: () -> Unit, onUploadProfileClick: () -> Unit, onNextStep: () -> Unit, infoTextColor: Color, navyColor: Color, softInputColor: Color, signatureBlue: Color, borderColor: Color) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Data Pribadi", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = navyColor, modifier = Modifier.fillMaxWidth())
        Text(text = "Lengkapi identitas diri sesuai KTP.", fontSize = 13.sp, color = infoTextColor, modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp))
        Box(modifier = Modifier.size(116.dp).testTag("upload_profile_box"), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) { drawCircle(color = signatureBlue.copy(alpha = 0.6f), style = Stroke(width = 1.5.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f))) }
            Box(modifier = Modifier.size(96.dp).clip(CircleShape).background(Color(0xFFE9EEF4)).clickable { onUploadProfileClick() }, contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = navyColor, modifier = Modifier.size(32.dp))
                    Text(text = "Foto profile", fontSize = 11.sp, color = signatureBlue, fontWeight = FontWeight.Bold)
                }
            }
            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(navyColor).align(Alignment.BottomEnd).border(BorderStroke(2.dp, Color.White), CircleShape).clickable { onUploadProfileClick() }.testTag("btn_capture_profile"), contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color(0xFF9FB3C8), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Foto wajah yang jelas & tidak blur.\nJPG/PNG, maks 2MB.", fontSize = 11.sp, color = infoTextColor, lineHeight = 15.sp)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Nama Lengkap", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = navyColor, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = namaLengkap, onValueChange = onNamaChange, placeholder = { Text("Sesuai KTP", color = Color(0xFF9FB3C8)) }, isError = namaError != null, singleLine = true, shape = RoundedCornerShape(10.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = signatureBlue, unfocusedBorderColor = borderColor, focusedContainerColor = softInputColor, unfocusedContainerColor = softInputColor), modifier = Modifier.fillMaxWidth().testTag("input_jastip_fullname"))
        if (namaError != null) Text(text = namaError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "No. WhatsApp", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = navyColor)
                OutlinedTextField(value = whatsappNumber, onValueChange = onWhatsappChange, placeholder = { Text("0812-3456-7890", fontSize = 12.sp) }, isError = whatsappError != null, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), shape = RoundedCornerShape(10.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = signatureBlue, unfocusedBorderColor = borderColor, focusedContainerColor = softInputColor, unfocusedContainerColor = softInputColor), modifier = Modifier.fillMaxWidth().testTag("input_jastip_whatsapp"))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Email", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = navyColor)
                OutlinedTextField(value = email, onValueChange = onEmailChange, placeholder = { Text("Emailmu@gmail.com", fontSize = 12.sp) }, isError = emailError != null, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), shape = RoundedCornerShape(10.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = signatureBlue, unfocusedBorderColor = borderColor, focusedContainerColor = softInputColor, unfocusedContainerColor = softInputColor), modifier = Modifier.fillMaxWidth().testTag("input_jastip_email"))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Password Akun", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = navyColor, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = password, onValueChange = onPasswordChange, placeholder = { Text("Minimal 8 karakter") }, visualTransformation = if (isPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(), isError = passwordError != null, trailingIcon = { IconButton(onClick = onPasswordVisibilityToggle) { Icon(imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null) } }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = signatureBlue, unfocusedBorderColor = borderColor, focusedContainerColor = softInputColor, unfocusedContainerColor = softInputColor))
        Spacer(modifier = Modifier.height(32.dp))
        HorizontalDivider(color = borderColor)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onNextStep, colors = ButtonDefaults.buttonColors(containerColor = navyColor), shape = CircleShape, modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_step1_next")) { Text(text = "Lanjut ke Verifikasi", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White) }
    }
}

@Composable
fun VerifikasiStepLayout(otpValues: List<String>, onOtpChange: (Int, String) -> Unit, otpError: String?, whatsappNumber: String, isKtpPicUploaded: Boolean, onUploadKtpClick: () -> Unit, ktpError: String?, onNextStep: () -> Unit, onBackStep: () -> Unit, infoTextColor: Color, navyColor: Color, signatureBlue: Color, softInputColor: Color, borderColor: Color) {
    val focusRequester = remember { FocusRequester() }
    val otpString = otpValues.joinToString("")

    // Manage internal state for the hidden field to control cursor/direction
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(otpString, TextRange(otpString.length)))
    }

    // Sync with external state
    LaunchedEffect(otpString) {
        if (textFieldValue.text != otpString) {
            textFieldValue = TextFieldValue(otpString, TextRange(otpString.length))
        }
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Verifikasi identitas", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = navyColor, modifier = Modifier.fillMaxWidth())
        Text(text = "Pastikan data kamu aman dan valid.", fontSize = 13.sp, color = infoTextColor, modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp))
        Text(text = "Kode OTP", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = navyColor, modifier = Modifier.fillMaxWidth())
        Text(text = "Kode 6 digit dikirim ke WhatsApp $whatsappNumber", fontSize = 11.sp, color = infoTextColor, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp))
        
        // Force LTR layout for OTP boxes to prevent right-to-left input issues
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Box(contentAlignment = Alignment.Center) {
                // Hidden BasicTextField to capture input
                BasicTextField(
                    value = textFieldValue,
                    onValueChange = { newValue ->
                        if (newValue.text.length <= 6 && newValue.text.all { it.isDigit() }) {
                            // Always keep selection at the end to ensure LTR-like typing behavior
                            textFieldValue = newValue.copy(selection = TextRange(newValue.text.length))
                            for (i in 0 until 6) {
                                val char = if (i < newValue.text.length) newValue.text[i].toString() else ""
                                onOtpChange(i, char)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0f)
                        .focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(
                        textAlign = TextAlign.Start,
                        textDirection = TextDirection.Ltr
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { focusRequester.requestFocus() }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    otpValues.forEachIndexed { i, v ->
                        val isFocused = (i == otpString.length) || (i == 5 && otpString.length == 6)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = softInputColor),
                            border = BorderStroke(
                                width = if (isFocused) 2.dp else 1.dp,
                                color = if (isFocused) navyColor else borderColor
                            )
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = v.ifEmpty { "•" },
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = navyColor
                                )
                            }
                        }
                    }
                }
            }
        }

        if (otpError != null) Text(text = otpError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = borderColor)
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Foto KTP", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = navyColor, modifier = Modifier.fillMaxWidth())
        Box(modifier = Modifier.fillMaxWidth().height(130.dp).clip(RoundedCornerShape(14.dp)).background(softInputColor).clickable { onUploadKtpClick() }, contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) { drawRoundRect(color = signatureBlue, cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx()), style = Stroke(width = 1.5.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f))) }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(imageVector = if (isKtpPicUploaded) Icons.Default.CheckCircle else Icons.Default.PhotoCamera, contentDescription = null, tint = if (isKtpPicUploaded) Color(0xFF50C5B0) else signatureBlue, modifier = Modifier.size(36.dp))
                Text(text = if (isKtpPicUploaded) "Foto KTP Berhasil!" else "Ambil foto selfie dengan KTP", fontSize = 13.sp, color = infoTextColor)
            }
        }
        if (ktpError != null) Text(text = ktpError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(32.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onBackStep, border = BorderStroke(1.dp, signatureBlue), shape = CircleShape, modifier = Modifier.weight(1f).height(48.dp)) { Text("Kembali", color = signatureBlue) }
            Button(onClick = onNextStep, colors = ButtonDefaults.buttonColors(containerColor = signatureBlue), shape = CircleShape, modifier = Modifier.weight(1.5f).height(48.dp)) { Text(if (isKtpPicUploaded) "Lanjut" else "Ambil Foto", color = Color.White) }
        }
    }
}

@Composable
fun OperasionalStepLayout(selectedBank: String, onBankSelect: (String) -> Unit, bankError: String?, accountNumber: String, onAccountNoChange: (String) -> Unit, accountNoError: String?, accountHolderName: String, onAccountHolderChange: (String) -> Unit, accountHolderError: String?, operationalAreas: List<Pair<String, Boolean>>, onToggleArea: (Int) -> Unit, onAddAreaClick: () -> Unit, onFinishRegistration: () -> Unit, onBackStep: () -> Unit, listOpened: Boolean, onToggleList: () -> Unit, infoTextColor: Color, navyColor: Color, signatureBlue: Color, softInputColor: Color, borderColor: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Rekening bank", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = navyColor)
        Text(text = "Pastikan atas nama sendiri.", fontSize = 12.sp, color = infoTextColor, modifier = Modifier.padding(bottom = 12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.weight(1f).height(54.dp).clip(RoundedCornerShape(12.dp)).background(softInputColor).border(BorderStroke(1.dp, borderColor), RoundedCornerShape(12.dp)).clickable { onToggleList() }.padding(horizontal = 12.dp), contentAlignment = Alignment.CenterStart) {
                Text(text = selectedBank.ifEmpty { "Pilih Bank" }, color = navyColor, fontSize = 13.sp)
                DropdownMenu(expanded = listOpened, onDismissRequest = onToggleList) { listOf("Bank BCA", "Bank Mandiri", "Bank BRI").forEach { b -> DropdownMenuItem(text = { Text(b) }, onClick = { onBankSelect(b); onToggleList() }) } }
            }
            OutlinedTextField(value = accountNumber, onValueChange = onAccountNoChange, placeholder = { Text("No. rekening") }, isError = accountNoError != null, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1.2f).height(54.dp))
        }
        if (bankError != null || accountNoError != null) Text(text = (bankError ?: "") + (accountNoError ?: ""), color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(14.dp))
        OutlinedTextField(value = accountHolderName, onValueChange = onAccountHolderChange, placeholder = { Text("Nama pemilik rekening") }, isError = accountHolderError != null, singleLine = true, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
        if (accountHolderError != null) Text(text = accountHolderError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = borderColor)
        Text(text = "Area operasional", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = navyColor, modifier = Modifier.padding(top = 20.dp))
        Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Row 1
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for (i in 0..2) {
                    if (i < operationalAreas.size) {
                        ChipItem(operationalAreas[i].first, operationalAreas[i].second, { onToggleArea(i) }, signatureBlue, Modifier.weight(1f))
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            // Row 2
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for (i in 3..4) {
                    if (i < operationalAreas.size) {
                        ChipItem(operationalAreas[i].first, operationalAreas[i].second, { onToggleArea(i) }, signatureBlue, Modifier.weight(1f))
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Box(modifier = Modifier.weight(1f).height(38.dp).clip(RoundedCornerShape(30.dp)).background(Color.White).border(BorderStroke(1.dp, signatureBlue), RoundedCornerShape(30.dp)).clickable { onAddAreaClick() }, contentAlignment = Alignment.Center) {
                    Text("+ Tambah", color = signatureBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onBackStep, border = BorderStroke(1.dp, signatureBlue), shape = CircleShape, modifier = Modifier.weight(1f).height(48.dp)) { Text("Kembali", color = signatureBlue) }
            Button(onClick = onFinishRegistration, colors = ButtonDefaults.buttonColors(containerColor = signatureBlue), shape = CircleShape, modifier = Modifier.weight(1.5f).height(48.dp)) { Text("Ringkasan", color = Color.White) }
        }
    }
}

@Composable
fun ChipItem(text: String, isActive: Boolean, onClick: () -> Unit, signatureBlue: Color, modifier: Modifier = Modifier) {
    Box(modifier = modifier.height(38.dp).clip(RoundedCornerShape(30.dp)).background(if (isActive) signatureBlue else Color.White).then(if (!isActive) Modifier.border(BorderStroke(1.dp, signatureBlue), RoundedCornerShape(30.dp)) else Modifier).clickable { onClick() }, contentAlignment = Alignment.Center) { Text(text = text, color = if (isActive) Color.White else signatureBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) }
}
