package com.example.niftylive.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
fun TickerText(
    text: String,
    color: Color = Color.Unspecified,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    // Split the text into characters and display them in a Row
    Row(modifier = modifier) {
        text.forEach { char ->
            // AnimatedContent checks if 'targetState' (the char) has changed.
            // If it's the same, no animation happens.
            // If it's different, it runs the animation.
            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    // This creates the "slot machine" slide effect
                    // New number slides in from bottom, old slides out to top
                    (slideInVertically { height -> height } + fadeIn())
                        .togetherWith(slideOutVertically { height -> -height } + fadeOut())
                },
                label = "TickerAnimation"
            ) { targetChar ->
                Text(
                    text = targetChar.toString(),
                    style = style,
                    color = color,
                    softWrap = false
                )
            }
        }
    }
}
