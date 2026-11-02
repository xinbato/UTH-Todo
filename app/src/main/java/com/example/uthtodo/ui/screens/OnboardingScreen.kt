package com.example.uthtodo.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.uthtodo.model.CardContentDetail
import com.example.uthtodo.model.CategoryTagModel
import com.example.uthtodo.model.OnboardingPageModel
import com.example.uthtodo.model.ReminderItemModel
import com.example.uthtodo.model.TaskItemModel
import com.example.uthtodo.ui.theme.BorderGray
import com.example.uthtodo.ui.theme.Dimens
import com.example.uthtodo.ui.theme.IndicatorInactive
import com.example.uthtodo.ui.theme.LightSoftBlue
import com.example.uthtodo.ui.theme.OrangeTagBg
import com.example.uthtodo.ui.theme.OrangeTagText
import com.example.uthtodo.ui.theme.PrimaryBlue
import com.example.uthtodo.ui.theme.SoftBlueBg
import com.example.uthtodo.ui.theme.UTHTodoTheme

@Composable
fun OnboardingScreen(
    pageModel: OnboardingPageModel,
    onNextClick: () -> Unit,
    onSkipClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(Dimens.PaddingLarge),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.PaddingMedium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = PrimaryBlue
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Box(
                                    modifier = Modifier
                                        .height(4.dp)
                                        .width(14.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(SoftBlueBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(9.dp)
                                            .clip(CircleShape)
                                            .background(PrimaryBlue)
                                    )
                                }
                                Spacer(modifier = Modifier.width(3.dp))
                                Box(
                                    modifier = Modifier
                                        .height(4.dp)
                                        .width(10.dp)
                                        .clip(CircleShape)
                                        .background(SoftBlueBg)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)) {
                            append(pageModel.headerModel.appNameBold)
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, color = PrimaryBlue)) {
                            append(pageModel.headerModel.appNameRegular)
                        }
                    },
                    fontSize = 20.sp
                )
            }

            Text(
                text = pageModel.headerModel.skipTextResId?.let { stringResource(it) } ?: pageModel.headerModel.skipText,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = Dimens.FontBodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onSkipClick() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceVariant else LightSoftBlue)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val detail = pageModel.cardDetail) {
                is CardContentDetail.TaskListDetail -> OnboardingCard1a(detail.tasks)
                is CardContentDetail.CategoryDetail -> OnboardingCard1b(detail.categories)
                is CardContentDetail.NotificationDetail -> OnboardingCard1c(detail.reminder)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = pageModel.titleResId?.let { stringResource(it) } ?: pageModel.title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = pageModel.descriptionResId?.let { stringResource(it) } ?: pageModel.description,
                fontSize = Dimens.FontBodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(pageModel.totalSteps) { index ->
                    if (index == pageModel.stepIndex) {
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(IndicatorInactive)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onNextClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.ButtonHeight),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(
                    text = pageModel.buttonTextResId?.let { stringResource(it) } ?: pageModel.buttonText,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun OnboardingCard1a(
    tasks: List<TaskItemModel>,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        tasks.forEach { task ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (task.isChecked) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .border(2.dp, IndicatorInactive, CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (task.isChecked) 110.dp else if (task.id % 2 == 0) 90.dp else 130.dp)
                            .clip(CircleShape)
                            .background(if (isSystemInDarkTheme()) MaterialTheme.colorScheme.outlineVariant else BorderGray)
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingCard1b(
    categories: List<CategoryTagModel>,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        categories.forEach { category ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (category.isPrimary) LightSoftBlue else OrangeTagBg)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = category.name,
                            color = if (category.isPrimary) PrimaryBlue else OrangeTagText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (category.isPrimary) 140.dp else 110.dp)
                            .clip(CircleShape)
                            .background(if (isSystemInDarkTheme()) MaterialTheme.colorScheme.outlineVariant else BorderGray)
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingCard1c(
    reminder: ReminderItemModel,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        // Bell Circle Icon
        Surface(
            modifier = Modifier.size(76.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(38.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Speech Bubble Reminder
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Text(
                text = reminder.text,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
            )
        }
    }
}

@Preview(name = "Onboarding 1a Light", showBackground = true)
@Preview(name = "Onboarding 1a Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun OnboardingScreen1aPreview() {
    UTHTodoTheme {
        OnboardingScreen(
            pageModel = OnboardingPageModel.getPages()[0],
            onNextClick = {},
            onSkipClick = {}
        )
    }
}

@Preview(name = "Onboarding 1b Light", showBackground = true)
@Preview(name = "Onboarding 1b Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun OnboardingScreen1bPreview() {
    UTHTodoTheme {
        OnboardingScreen(
            pageModel = OnboardingPageModel.getPages()[1],
            onNextClick = {},
            onSkipClick = {}
        )
    }
}

@Preview(name = "Onboarding 1c Light", showBackground = true)
@Preview(name = "Onboarding 1c Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun OnboardingScreen1cPreview() {
    UTHTodoTheme {
        OnboardingScreen(
            pageModel = OnboardingPageModel.getPages()[2],
            onNextClick = {},
            onSkipClick = {}
        )
    }
}
