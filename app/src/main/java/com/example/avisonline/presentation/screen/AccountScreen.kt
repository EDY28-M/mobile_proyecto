package com.example.avisonline.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.avisonline.presentation.viewmodel.AccountState
import com.example.avisonline.presentation.viewmodel.AccountViewModel

@Composable
fun AccountScreen(viewModel: AccountViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        viewModel.fetchAccountDetails()
    }

    val state by viewModel.accountState.collectAsState()
    
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        when (val accountState = state) {
            is AccountState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is AccountState.Error -> {
                Text(text = accountState.message, modifier = Modifier.align(Alignment.Center))
            }
            is AccountState.Success -> {
                val user = accountState.user
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Mi Cuenta",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    AccountInfoRow(icon = Icons.Default.Person, label = "Nombre", value = "${user.name} ${user.surname ?: ""}")
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    AccountInfoRow(icon = Icons.Default.Email, label = "Email", value = user.email)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    AccountInfoRow(icon = Icons.Default.Phone, label = "Teléfono", value = user.phone ?: "No especificado")
                }
            }
        }
    }
}

@Composable
fun AccountInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}