package com.maximshuhman.bsuirschedule

enum class ApplicationThemes(
    val drawableId: Int,
    val nameId: Int,
    val backgroundColorId: Int
) {
    SystemTheme(R.drawable.dark_theme, R.string.system_theme, R.color.background_dialog),
    DarkTheme(R.drawable.dark_theme, R.string.dark_theme, R.color.background_dialog),
    LightTheme(R.drawable.light_theme, R.string.light_theme, R.color.background_dialog),
    PancakesTheme(R.drawable.light_theme, R.string.panckakes_theme, R.color.background_dialog),
}