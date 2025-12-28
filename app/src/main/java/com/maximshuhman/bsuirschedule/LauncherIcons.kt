package com.maximshuhman.bsuirschedule

enum class LauncherIcons(
    val drawableId: Int,
    val nameId: Int,
    val backgroundColorId: Int
) {
    DefaultIcon(
        R.drawable.default_logo,
        R.string.default_logo,
        R.color.ic_launcher_background,
    ),
    ChristmasIcon(
        R.drawable.christmas_logo,
        R.string.christmas_logo,
        R.color.ic_launcher_christmas_background,
    )
}
