package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextDarkGray
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.delay

@Composable
fun OtpAuthDialog(
    currentEmail: String,
    aiName: String,
    onVerifyOtp: (email: String, otp: String) -> Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var emailInput by remember { mutableStateOf(currentEmail) }
    var otpCodeInput by remember { mutableStateOf("") }
    var isCodeSent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var countdown by remember { mutableIntStateOf(60) }
    var simulatedSentCode by remember { mutableStateOf("") }

    LaunchedEffect(isCodeSent) {
        if (isCodeSent) {
            countdown = 60
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, DarkCardBorder, RoundedCornerShape(24.dp))
                .testTag("otp_auth_dialog"),
            color = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Brush.linearGradient(listOf(IndigoAccent, PurpleAccent))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.VpnKey,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Connexion Sécurisée",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite,
                                    fontSize = 18.sp
                                )
                            )
                            Text(
                                text = "Authentification OTP par e-mail",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextMutedGray,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Fermer", tint = TextMutedGray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Email field
                Text(
                    text = "ADRESSE E-MAIL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkGray,
                        letterSpacing = 1.sp,
                        fontSize = 10.sp
                    ),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    placeholder = { Text("nom@domaine.com", color = TextDarkGray) },
                    singleLine = true,
                    enabled = !isCodeSent,
                    leadingIcon = {
                        Icon(Icons.Filled.Email, contentDescription = null, tint = IndigoAccent, modifier = Modifier.size(18.dp))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_otp_email"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IndigoAccent,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (!isCodeSent) {
                    Button(
                        onClick = {
                            if (emailInput.isNotBlank() && emailInput.contains("@")) {
                                isLoading = true
                                val generatedCode = (100000..999999).random().toString()
                                simulatedSentCode = generatedCode
                                isLoading = false
                                isCodeSent = true
                                Toast.makeText(context, "Code OTP envoyé : $generatedCode (Valable 10 min)", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Veuillez entrer un e-mail valide", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_send_otp"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = IndigoAccent,
                            contentColor = Color.White
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Recevoir le code OTP de $aiName",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                } else {
                    // Step 2: Code Input
                    Text(
                        text = "CODE DE VÉRIFICATION (6 CHIFFRES)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkGray,
                            letterSpacing = 1.sp,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    OutlinedTextField(
                        value = otpCodeInput,
                        onValueChange = {
                            if (it.length <= 6) otpCodeInput = it
                        },
                        placeholder = { Text("Ex: $simulatedSentCode", color = TextDarkGray) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(18.dp))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_otp_code"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (countdown > 0) "Renvoyer dans ${countdown}s" else "Code non reçu ?",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = TextMutedGray)
                        )
                        if (countdown == 0) {
                            Text(
                                text = "Renvoyer un code",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IndigoAccent
                                ),
                                modifier = Modifier.clickable {
                                    val newCode = (100000..999999).random().toString()
                                    simulatedSentCode = newCode
                                    countdown = 60
                                    Toast.makeText(context, "Nouveau code : $newCode", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (otpCodeInput.length == 6) {
                                val success = onVerifyOtp(emailInput, otpCodeInput)
                                if (success) {
                                    Toast.makeText(context, "Connecté avec succès !", Toast.LENGTH_SHORT).show()
                                    onDismiss()
                                } else {
                                    Toast.makeText(context, "Code invalide", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Entrez les 6 chiffres du code", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_verify_otp"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldAccent,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = "Valider et se connecter",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Changer d'e-mail",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMutedGray,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable {
                                isCodeSent = false
                                otpCodeInput = ""
                            }
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}
