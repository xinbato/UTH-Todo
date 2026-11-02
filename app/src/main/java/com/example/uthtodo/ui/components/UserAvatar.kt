package com.example.uthtodo.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.uthtodo.R
import com.example.uthtodo.ui.theme.LightSoftBlue
import com.example.uthtodo.ui.theme.PrimaryBlue
import com.example.uthtodo.ui.theme.UTHTodoTheme

/**
 * Reusable user avatar component supporting Google profile images with fallback initials.
 */
@Composable
fun UserAvatar(
    avatarUrl: String?,
    initials: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    fontSize: TextUnit = 18.sp
) {
    Surface(
        modifier = modifier
            .size(size)
            .clip(CircleShape),
        shape = CircleShape,
        color = LightSoftBlue
    ) {
        if (!avatarUrl.isNullOrBlank()) {
            SubcomposeAsyncImage(
                model = avatarUrl,
                contentDescription = stringResource(R.string.cd_avatar),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = {
                    DefaultAvatarContent(initials = initials, fontSize = fontSize)
                },
                error = {
                    DefaultAvatarContent(initials = initials, fontSize = fontSize)
                }
            )
        } else {
            DefaultAvatarContent(initials = initials, fontSize = fontSize)
        }
    }
}

@Composable
private fun DefaultAvatarContent(
    initials: String,
    fontSize: TextUnit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = initials,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue,
            fontSize = fontSize
        )
    }
}

@Preview(name = "User Avatar Light", showBackground = true)
@Preview(name = "User Avatar Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun UserAvatarPreview() {
    UTHTodoTheme {
        UserAvatar(avatarUrl = null, initials = "UTH")
    }
}
