package com.karnama.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────────────────────────────────
// فونت Vazirmatn:
// به دلیل محدودیت دسترسی به شبکه در محیط تولید این پروژه، فایل‌های باینری
// فونت به‌صورت خودکار اضافه نشده‌اند. برای فعال‌سازی فونت فارسی Vazirmatn:
//   ۱) سه فایل زیر را از https://github.com/rastikerdar/vazirmatn دانلود کنید:
//      Vazirmatn-Regular.ttf, Vazirmatn-Medium.ttf, Vazirmatn-SemiBold.ttf
//   ۲) آن‌ها را با نام‌های vazirmatn_regular.ttf، vazirmatn_medium.ttf و
//      vazirmatn_semibold.ttf در پوشه app/src/main/res/font/ کپی کنید.
//   ۳) بلوک کامنت زیر را باز کرده و FontFamily.Default در پایین فایل را
//      با Vazirmatn جایگزین کنید.
// تا آن زمان، برنامه با فونت پیش‌فرض سیستم (که فارسی را هم پشتیبانی می‌کند)
// کامپایل و اجرا می‌شود.
// ─────────────────────────────────────────────────────────────────────────
//
// import androidx.compose.ui.text.font.Font
// import com.karnama.app.R
//
// val Vazirmatn = FontFamily(
//     Font(R.font.vazirmatn_regular, FontWeight.Normal),
//     Font(R.font.vazirmatn_medium, FontWeight.Medium),
//     Font(R.font.vazirmatn_semibold, FontWeight.SemiBold)
// )

val Vazirmatn = FontFamily.Default

val KarnamaTypography = Typography(
    headlineMedium = TextStyle(
        fontFamily = Vazirmatn,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Vazirmatn,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Vazirmatn,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Vazirmatn,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Vazirmatn,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Vazirmatn,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Vazirmatn,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
)
