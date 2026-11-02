package com.example.uthtodo.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.model.StartPageModel
import com.example.uthtodo.ui.theme.Dimens
import com.example.uthtodo.ui.theme.PrimaryBlue
import com.example.uthtodo.ui.theme.SoftBlueBg
import com.example.uthtodo.ui.theme.UTHTodoTheme

@Composable
fun StartScreen(
    modifier: Modifier = Modifier,
    data: StartPageModel = StartPageModel(),
    onStartClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBlue)
            .clickable { onStartClick() }
            .systemBarsPadding()
            .padding(Dimens.PaddingLarge),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(110.dp),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(40.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(SoftBlueBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(30.dp)
                                .clip(CircleShape)
                                .background(SoftBlueBg)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(data.titleBold)
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                        append(data.titleRegular)
                    }
                },
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 36.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = data.subtitleResId?.let { stringResource(it) } ?: data.subtitle,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                fontSize = Dimens.FontBodyMedium,
                fontWeight = FontWeight.Normal
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = Dimens.PaddingLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .width(110.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.28f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimary)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = data.versionResId?.let { stringResource(it) } ?: data.version,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                fontSize = Dimens.FontCaption
            )
        }
    }
}

@Preview(name = "Start Screen Light", showBackground = true)
@Preview(name = "Start Screen Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun StartScreenPreview() {
    UTHTodoTheme {
        StartScreen(onStartClick = {})
    }
}
