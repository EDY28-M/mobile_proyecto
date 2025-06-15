package com.example.avisonline.presentation.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.avisonline.presentation.theme.BlueLight
import com.example.avisonline.presentation.theme.BluePrimary
import com.example.avisonline.presentation.theme.ErrorRed
import com.example.avisonline.presentation.viewmodel.CreateProductViewModel
import com.example.avisonline.presentation.viewmodel.ProductCreationState

@Composable
fun CreateProductScreen(viewModel: CreateProductViewModel = viewModel()) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val limitState by viewModel.limitState.collectAsState()
    val creationState by viewModel.creationState.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> imageUri = uri }
    )

    LaunchedEffect(Unit) {
        viewModel.checkProductLimit()
    }
    
     LaunchedEffect(creationState) {
        when (val state = creationState) {
            is ProductCreationState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                // Clear fields after success
                title = ""
                summary = ""
                price = ""
                imageUri = null
                viewModel.resetCreationState()
                viewModel.checkProductLimit() // Re-check limit
            }
            is ProductCreationState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetCreationState()
            }
            else -> {}
        }
    }


    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text("Crear un Nuevo Producto", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            // Product Limit Message
            if (limitState?.limitedAccess == true) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = limitState?.message ?: "Solo tienes permitido crear 3 productos.",
                        modifier = Modifier.padding(16.dp),
                        color = ErrorRed
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Image Picker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Imagen seleccionada",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("Toca para subir una imagen")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            // Form Fields
            OutlinedTextField(value = title, onValueChange = {title = it}, label = { Text("Título del Producto") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = summary, onValueChange = {summary = it}, label = { Text("Resumen") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = price, onValueChange = {price = it}, label = { Text("Precio (S/)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.NumberDecimal))
            Spacer(modifier = Modifier.height(24.dp))
            
            // Submit Button
            if (creationState is ProductCreationState.Loading) {
                CircularProgressIndicator()
            } else {
                 Button(
                    onClick = { viewModel.createProduct(context, title, summary, price, imageUri) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(BlueLight, BluePrimary)
                            )
                        ),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text("Crear Producto", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}