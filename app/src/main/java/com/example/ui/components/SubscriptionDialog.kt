package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.repository.AiSaasConstants
import com.example.data.repository.BankTransferRequest
import com.example.data.repository.UserSettings
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.PremiumGoldEnd
import com.example.ui.theme.PremiumGoldStart
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextDarkGray
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextWhite

@Composable
fun SubscriptionDialog(
    userSettings: UserSettings,
    onSelectCurrency: (String) -> Unit,
    onActivateStripe: (String, String) -> Unit,
    onCreateBankTransfer: (String, String) -> BankTransferRequest,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCurrency by remember { mutableStateOf(userSettings.preferredCurrency) }
    var selectedPlanKey by remember { mutableStateOf("monthly") }
    var paymentMethod by remember { mutableStateOf("card") } // "card" or "bank_transfer"
    var generatedTransfer by remember { mutableStateOf<BankTransferRequest?>(null) }
    var transferSubmitted by remember { mutableStateOf(false) }

    val currentPricingPlans = AiSaasConstants.PRICING_BY_CURRENCY[selectedCurrency.lowercase()]
        ?: AiSaasConstants.PRICING_BY_CURRENCY["eur"]!!

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, DarkCardBorder, RoundedCornerShape(24.dp))
                .testTag("subscription_dialog"),
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
                                .background(
                                    Brush.linearGradient(listOf(PremiumGoldStart, PremiumGoldEnd))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Stars,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${userSettings.aiName} SaaS Pro",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite,
                                    fontSize = 18.sp
                                )
                            )
                            Text(
                                text = "Accès Illimité • Multi-devises",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = AmberAccent,
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

                // Currency Selector Chips
                Text(
                    text = "CHOISISSEZ VOTRE DEVISE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkGray,
                        letterSpacing = 1.sp,
                        fontSize = 10.sp
                    ),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AiSaasConstants.CURRENCIES.forEach { (code, label) ->
                        val isSelected = selectedCurrency.equals(code, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) IndigoAccent else DarkSurfaceVariant)
                                .border(1.dp, if (isSelected) Color.Transparent else DarkCardBorder, RoundedCornerShape(10.dp))
                                .clickable {
                                    selectedCurrency = code
                                    onSelectCurrency(code)
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextMutedGray
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Plans Grid (Weekly, Monthly, Yearly)
                Text(
                    text = "CHOISISSEZ VOTRE PLAN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkGray,
                        letterSpacing = 1.sp,
                        fontSize = 10.sp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    currentPricingPlans.forEach { plan ->
                        val isSelected = selectedPlanKey == plan.planKey
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .border(
                                    1.5.dp,
                                    if (isSelected) IndigoAccent else DarkCardBorder,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    selectedPlanKey = plan.planKey
                                    generatedTransfer = null
                                    transferSubmitted = false
                                },
                            color = if (isSelected) IndigoAccent.copy(alpha = 0.12f) else DarkSurfaceVariant,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .border(2.dp, if (isSelected) IndigoAccent else TextDarkGray, CircleShape)
                                            .background(if (isSelected) IndigoAccent else Color.Transparent),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                Icons.Filled.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = plan.label,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextWhite,
                                                    fontSize = 14.sp
                                                )
                                            )
                                            if (plan.badge != null) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(
                                                            if (plan.badge.contains("Populaire")) IndigoAccent
                                                            else EmeraldAccent.copy(alpha = 0.2f)
                                                        )
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = plan.badge,
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (plan.badge.contains("Populaire")) Color.White else EmeraldAccent
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = "Génération d'images et vidéos illimitées",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = TextMutedGray,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }

                                Text(
                                    text = plan.formattedPrice,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isSelected) IndigoLight else TextWhite,
                                        fontSize = 16.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Payment Method Selector (Card Stripe vs Bank Transfer)
                Text(
                    text = "MODE DE PAIEMENT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkGray,
                        letterSpacing = 1.sp,
                        fontSize = 10.sp
                    ),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PaymentMethodTab(
                        title = "Carte (Stripe)",
                        icon = Icons.Filled.CreditCard,
                        isSelected = paymentMethod == "card",
                        onClick = { paymentMethod = "card" },
                        modifier = Modifier.weight(1f)
                    )

                    PaymentMethodTab(
                        title = "Virement Bancaire",
                        icon = Icons.Filled.AccountBalance,
                        isSelected = paymentMethod == "bank_transfer",
                        onClick = { paymentMethod = "bank_transfer" },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Card Checkout view vs Bank Transfer generator
                if (paymentMethod == "card") {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Lock, contentDescription = null, tint = EmeraldAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Paiement sécurisé par Stripe (SSL 256-bit)",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = EmeraldAccent,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    onActivateStripe(selectedPlanKey, selectedCurrency)
                                    Toast.makeText(context, "Abonnement activé avec succès !", Toast.LENGTH_SHORT).show()
                                    onDismiss()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_stripe_checkout"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = IndigoAccent,
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = "Payer par Carte Bancaire",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }
                    }
                } else {
                    // Bank Transfer Section
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            if (generatedTransfer == null) {
                                Text(
                                    text = "Générez un ordre de virement sécurisé avec référence unique.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextMutedGray,
                                        fontSize = 12.sp
                                    )
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        generatedTransfer = onCreateBankTransfer(selectedPlanKey, selectedCurrency)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp)
                                        .testTag("btn_generate_bank_transfer"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = CyanAccent,
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Icon(Icons.Filled.AccountBalance, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Générer la référence de virement",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            } else {
                                val transfer = generatedTransfer!!
                                // Reference Box
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(DarkBg)
                                        .border(1.dp, IndigoAccent.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "RÉFÉRENCE OBLIGATOIRE DU VIREMENT",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextDarkGray
                                            )
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = transfer.reference,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 16.sp,
                                                    color = CyanAccent,
                                                    letterSpacing = 1.sp
                                                )
                                            )
                                            IconButton(
                                                onClick = {
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    clipboard.setPrimaryClip(ClipData.newPlainText("Référence virement", transfer.reference))
                                                    Toast.makeText(context, "Référence copiée !", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Filled.ContentCopy, contentDescription = "Copier", tint = CyanAccent, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Bank details
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    BankDetailRow("Bénéficiaire", transfer.beneficiary)
                                    BankDetailRow("IBAN", transfer.iban)
                                    BankDetailRow("BIC/SWIFT", transfer.bic)
                                    BankDetailRow("Montant exact", "${transfer.amountFormatted} (${transfer.currency})")
                                    BankDetailRow("Validité", "3 jours ouvrés")
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        transferSubmitted = true
                                        onActivateStripe(selectedPlanKey, selectedCurrency)
                                        Toast.makeText(context, "Demande de virement validée ! Accès Pro activé.", Toast.LENGTH_LONG).show()
                                        onDismiss()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EmeraldAccent,
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "J'ai effectué le virement",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom feature highlights
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Security, contentDescription = null, tint = TextMutedGray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Garantie satisfait ou remboursé • Sans engagement",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = TextMutedGray)
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentMethodTab(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) IndigoAccent.copy(alpha = 0.25f) else DarkSurfaceVariant)
            .border(1.dp, if (isSelected) IndigoAccent else DarkCardBorder, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) IndigoLight else TextMutedGray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else TextMutedGray
                )
            )
        }
    }
}

@Composable
private fun BankDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = TextMutedGray)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextWhite
            )
        )
    }
}
