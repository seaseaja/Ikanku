package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.JastipViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UbahPinScreen(
    viewModel: JastipViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var oldPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    
    var isSaving by remember { mutableStateOf(false) }

    val focusOldPin = remember { FocusRequester() }
    val focusNewPin = remember { FocusRequester() }
    val focusConfirmPin = remember { FocusRequester() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF7F9FC),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Security Settings",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF003049)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF003049)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFF7F9FC)
                )
            )
        },
        bottomBar = {
            Surface(
                color = Color(0xFFF7F9FC),
                modifier = Modifier.fillMaxWidth().navigationBarsPadding()
            ) {
                Button(
                    onClick = {
                        if (oldPin.length < 6 || newPin.length < 6 || confirmPin.length < 6) {
                            Toast.makeText(context, "PIN harus 6 digit", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (newPin != confirmPin) {
                            Toast.makeText(context, "Konfirmasi PIN tidak cocok", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        
                        isSaving = true
                        // Simulate saving
                        Toast.makeText(context, "PIN Berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                        onBackClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003049)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(imageVector = Icons.Outlined.LockReset, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Simpan PIN", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Ubah PIN",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Pastikan PIN baru Anda mudah diingat dan tidak diketahui orang lain untuk keamanan akun IkanKu.",
                fontSize = 14.sp,
                color = Color.DarkGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp),
                lineHeight = 22.sp
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            PinInputRow(
                label = "Masukkan PIN Lama",
                value = oldPin,
                onValueChange = { 
                    if (it.length <= 6) {
                        oldPin = it
                        if (it.length == 6) focusNewPin.requestFocus()
                    }
                },
                focusRequester = focusOldPin
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            PinInputRow(
                label = "Masukkan PIN Baru",
                value = newPin,
                onValueChange = { 
                    if (it.length <= 6) {
                        newPin = it
                        if (it.length == 6) focusConfirmPin.requestFocus()
                    }
                },
                focusRequester = focusNewPin
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            PinInputRow(
                label = "Konfirmasi PIN Baru",
                value = confirmPin,
                onValueChange = { 
                    if (it.length <= 6) confirmPin = it
                },
                focusRequester = focusConfirmPin
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PinInputRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF334155)
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        Box(contentAlignment = Alignment.Center) {
            // Hidden TextField to handle actual input
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(0f)
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
            )
            
            // Visual representation of PIN boxes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { focusRequester.requestFocus() },
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                repeat(6) { index ->
                    val char = when {
                        index < value.length -> "●"
                        else -> ""
                    }
                    val isFocused = index == value.length
                    
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (char.isNotEmpty()) Color(0xFFE0F2FE) else Color(0xFFF1F5F9),
                        border = BorderStroke(
                            width = if (isFocused) 2.dp else 1.dp,
                            color = if (isFocused) Color(0xFF003049) else Color.Transparent
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = char,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF003049)
                            )
                        }
                    }
                }
            }
        }
    }
}

