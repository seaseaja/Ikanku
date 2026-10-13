package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.JastipViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: JastipViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()
    val scrollState = rememberScrollState()
    
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    // Initialize states when profile is loaded
    LaunchedEffect(userProfile) {
        if (fullName.isEmpty()) fullName = userProfile.fullName
        if (email.isEmpty()) email = userProfile.email
        if (phoneNumber.isEmpty()) phoneNumber = userProfile.phoneNumber
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF5FAFD),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Edit Profil",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF003B5B),
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF003B5B)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            if (fullName.isBlank() || email.isBlank() || phoneNumber.isBlank()) {
                                Toast.makeText(context, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            
                            isSaving = true
                            viewModel.updateProfile(email, fullName, phoneNumber) { success ->
                                isSaving = false
                                if (success) {
                                    Toast.makeText(context, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                                    onBackClick()
                                } else {
                                    Toast.makeText(context, "Gagal memperbarui profil. Periksa koneksi internet Anda.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003B5B)),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Icon(imageVector = Icons.Outlined.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Simpan Perubahan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Avatar Section
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0F2FE))
                        .border(2.dp, Color.White, CircleShape)
                        .clickable { 
                            Toast.makeText(context, "Fitur ubah foto akan segera hadir", Toast.LENGTH_SHORT).show()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person, 
                        contentDescription = null, 
                        modifier = Modifier.size(48.dp), 
                        tint = Color(0xFF81D4FA)
                    )
                }
                
                // Edit Icon
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF003B5B))
                        .border(2.dp, Color.White, CircleShape)
                        .clickable { 
                            Toast.makeText(context, "Membuka galeri...", Toast.LENGTH_SHORT).show()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit, 
                        contentDescription = "Edit Photo", 
                        tint = Color.White, 
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Ketuk untuk ubah foto profil", 
                color = Color(0xFF003B5B), 
                fontWeight = FontWeight.Medium, 
                fontSize = 13.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Form Fields
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                EditProfileField(
                    label = "Nama Lengkap",
                    value = fullName,
                    icon = Icons.Outlined.Person,
                    onValueChange = { fullName = it }
                )
                
                EditProfileField(
                    label = "Alamat Email",
                    value = email,
                    icon = Icons.Outlined.Mail,
                    onValueChange = { email = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                
                EditProfileField(
                    label = "Nomor Telepon",
                    value = phoneNumber,
                    icon = Icons.Outlined.Phone,
                    onValueChange = { phoneNumber = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
            }
        }
    }
}

@Composable
fun EditProfileField(
    label: String, 
    value: String, 
    icon: ImageVector, 
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label, 
            fontSize = 13.sp, 
            color = Color(0xFF003B5B), 
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            leadingIcon = { 
                Icon(
                    imageVector = icon, 
                    contentDescription = null, 
                    tint = Color(0xFF003B5B), 
                    modifier = Modifier.size(20.dp)
                ) 
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF003B5B),
                unfocusedBorderColor = Color(0xFFCFD8DC),
                cursorColor = Color(0xFF003B5B)
            ),
            singleLine = true,
            keyboardOptions = keyboardOptions,
            textStyle = TextStyle(fontSize = 16.sp, color = Color.Black)
        )
    }
}
