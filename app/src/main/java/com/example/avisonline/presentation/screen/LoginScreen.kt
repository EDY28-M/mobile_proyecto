package com.example.avisonline.presentation.screen

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.avisonline.R
import com.example.avisonline.presentation.navigation.Screen
import com.example.avisonline.presentation.theme.BlueLight
import com.example.avisonline.presentation.theme.BluePrimary
import com.example.avisonline.presentation.viewmodel.AuthResult
import com.example.avisonline.presentation.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsState()

    // Google Sign-In setup
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }
    val googleAuthLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { idToken ->
                authViewModel.loginWithGoogle(idToken)
            }
        } catch (e: ApiException) {
            Toast.makeText(
                context,
                "Error de Google Sign-In: ${e.statusCode}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthResult.Success -> {
                Toast.makeText(context, state.welcomeMessage, Toast.LENGTH_SHORT).show()
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
            is AuthResult.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                authViewModel.resetState()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "AVISONLINE",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(32.dp))

            if (authState is AuthResult.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { authViewModel.login(email, password) },
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
                    Text("Iniciar Sesión", color = Color.White, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("o inicia sesión con", color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    val signInIntent = googleSignInClient.signInIntent
                    googleAuthLauncher.launch(signInIntent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                // Icon(painter = painterResource(id = R.drawable.ic_google_logo), contentDescription = "Google sign-in")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Continuar con Google")
            }

            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }
}
