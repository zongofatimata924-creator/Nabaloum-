package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCardBorderSubtle
import com.example.ui.theme.TextWhite

@Composable
fun AppTopTabBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkBg)
            .drawBehind {
                // border-b border-white/5
                val strokeWidth = 1.dp.toPx()
                val y = size.height - strokeWidth / 2
                drawLine(
                    color = DarkCardBorderSubtle,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            }
            .testTag("app_top_tab_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 0: Chat
            SleekNavTabButton(
                title = "Chat",
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("tab_chat")
            )

            // Tab 1: Imagine
            SleekNavTabButton(
                title = "Imagine",
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("tab_imagine")
            )
        }
    }
}

@Composable
private fun SleekNavTabButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val textColor by animateColorAsState(
        targetValue = if (isSelected) TextWhite else TextWhite.copy(alpha = 0.4f),
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "navTabTextColor"
    )

    Box(
        modifier = modifier
            .height(52.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color.White.copy(alpha = 0.1f))
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
            ),
            color = textColor
        )

        // border-b-2 border-white indicator
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color.White)
            )
        }
    }
}

