package com.jaehl.codeTool.ui.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object Icons {
    public val CopyAll: ImageVector
        get() {
            if (_CopyAll != null) {
                return _CopyAll!!
            }
            _CopyAll = ImageVector.Builder(
                name = "Copy_all",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 960f,
                viewportHeight = 960f
            ).apply {
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(120f, 740f)
                    verticalLineToRelative(-80f)
                    horizontalLineToRelative(80f)
                    verticalLineToRelative(80f)
                    close()
                    moveToRelative(0f, -140f)
                    verticalLineToRelative(-80f)
                    horizontalLineToRelative(80f)
                    verticalLineToRelative(80f)
                    close()
                    moveToRelative(0f, -140f)
                    verticalLineToRelative(-80f)
                    horizontalLineToRelative(80f)
                    verticalLineToRelative(80f)
                    close()
                    moveTo(260f, 880f)
                    verticalLineToRelative(-80f)
                    horizontalLineToRelative(80f)
                    verticalLineToRelative(80f)
                    close()
                    moveToRelative(100f, -160f)
                    quadToRelative(-33f, 0f, -56.5f, -23.5f)
                    reflectiveQuadTo(280f, 640f)
                    verticalLineToRelative(-480f)
                    quadToRelative(0f, -33f, 23.5f, -56.5f)
                    reflectiveQuadTo(360f, 80f)
                    horizontalLineToRelative(360f)
                    quadToRelative(33f, 0f, 56.5f, 23.5f)
                    reflectiveQuadTo(800f, 160f)
                    verticalLineToRelative(480f)
                    quadToRelative(0f, 33f, -23.5f, 56.5f)
                    reflectiveQuadTo(720f, 720f)
                    close()
                    moveToRelative(0f, -80f)
                    horizontalLineToRelative(360f)
                    verticalLineToRelative(-480f)
                    horizontalLineTo(360f)
                    close()
                    moveToRelative(40f, 240f)
                    verticalLineToRelative(-80f)
                    horizontalLineToRelative(80f)
                    verticalLineToRelative(80f)
                    close()
                    moveToRelative(-200f, 0f)
                    quadToRelative(-33f, 0f, -56.5f, -23.5f)
                    reflectiveQuadTo(120f, 800f)
                    horizontalLineToRelative(80f)
                    close()
                    moveToRelative(340f, 0f)
                    verticalLineToRelative(-80f)
                    horizontalLineToRelative(80f)
                    quadToRelative(0f, 33f, -23.5f, 56.5f)
                    reflectiveQuadTo(540f, 880f)
                    moveTo(120f, 320f)
                    quadToRelative(0f, -33f, 23.5f, -56.5f)
                    reflectiveQuadTo(200f, 240f)
                    verticalLineToRelative(80f)
                    close()
                    moveToRelative(420f, 80f)
                }
            }.build()
            return _CopyAll!!
        }

    private var _CopyAll: ImageVector? = null
}