package ir.saltech.myapps.stutter.ui.theme

import android.os.Build
import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import ir.saltech.myapps.stutter.R

val defaultFontFamily = FontFamily(
    Font(
        R.font.vazirmatn_vf_fanum
    )
)

@OptIn(ExperimentalTextApi::class)
val displayFontFamily = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    FontFamily(
        Font(
            R.font.vazirmatn_vf_fanum,
            variationSettings = FontVariation.Settings(
                FontVariation.weight(575),
                FontVariation.width(30f),
                FontVariation.slant(-6f),
            )
        )
    )
} else {
    defaultFontFamily
}

@OptIn(ExperimentalTextApi::class)
val headlineFontFamily = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    FontFamily(
        Font(
            R.font.vazirmatn_vf_fanum,
            variationSettings = FontVariation.Settings(
                FontVariation.weight(525),
                FontVariation.width(30f),
                FontVariation.slant(-6f),
            )
        )
    )
} else {
    defaultFontFamily
}

@OptIn(ExperimentalTextApi::class)
val titleFontFamily = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    FontFamily(
        Font(
            R.font.vazirmatn_vf_fanum,
            variationSettings = FontVariation.Settings(
                FontVariation.weight(475),
                FontVariation.width(30f),
                FontVariation.slant(-6f),
            )
        )
    )
} else {
    defaultFontFamily
}

@OptIn(ExperimentalTextApi::class)
val bodyFontFamily = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    FontFamily(
        Font(
            R.font.vazirmatn_vf_fanum,
            variationSettings = FontVariation.Settings(
                FontVariation.weight(425),
                FontVariation.width(30f),
                FontVariation.slant(-6f),
            )
        )
    )
} else {
    defaultFontFamily
}

@OptIn(ExperimentalTextApi::class)
val labelFontFamily = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    FontFamily(
        Font(
            R.font.vazirmatn_vf_fanum,
            variationSettings = FontVariation.Settings(
                FontVariation.weight(325),
                FontVariation.width(30f),
                FontVariation.slant(-6f),
            )
        )
    )
} else {
    defaultFontFamily
}


// Default Material 3 typography values
val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = headlineFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = headlineFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = headlineFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = titleFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = titleFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = titleFontFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = labelFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = labelFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = labelFontFamily),
)

