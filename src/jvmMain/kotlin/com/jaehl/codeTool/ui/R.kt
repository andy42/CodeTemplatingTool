package com.jaehl.codeTool.ui

import androidx.compose.ui.graphics.Color

object R {
    object Color {
        val dialogBackground = Color(0x8f000000)

        val rowBackgroundOdd = Color(0xffffffff)
        val rowBackgroundEven = Color(0xffededed)

        val primary = Color(0xffbfa456)
        val textDark = Color(0xff000000)
        val textLight = Color(0xffffffff)

        val dividerColor = Color(0xffadadad)

        val pageBackground = Color(0xffededed)
        val cardBackground = Color(0xffffffff)
        val cardTitleBackground = Color(0xffdedede)

        val errorText = Color(0xffc23838)

        val debugRed = Color(0xffff0000)
        val debugGreen = Color(0xff00ff00)
        val debugBlue = Color(0xff0000ff)

        val transparent = Color(0x00000000)

        val disabledBackground = Color(0xffbababa)

        val codeBlock = Color(0xffe0e0e0)

        val rowBackground = Color(0x00000000)
        val rowText = textDark
        val rowHoverBackground = Color(0x11000000)
        val rowHoverText = Color(0xff000000)
        val rowSelectedBackground = primary
        val rowSelectedText = textLight

        var deleteButtonBackground = Color(0xffc23838)
        var deleteButtonText = Color(0xffffffff)

        object Button {
            var background = primary
            var text = textLight
        }

        object ButtonOutlined {
            var border = primary
            var text = primary
        }

        object ButtonDelete {
            var background = Color(0xffc23838)
            var text = Color(0xffffffff)
        }

        object TopAppBar {
            var background = primary
            var text = textLight
        }

        object Card {
            object SubTitle {
                var background = primary
                var text = textLight
            }
        }
    }
}