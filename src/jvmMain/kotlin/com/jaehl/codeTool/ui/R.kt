package com.jaehl.codeTool.ui

import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.ui.graphics.Color

object R {
    object Color {
        val dialogBackground = Color(0x8f000000)

        val rowBackgroundOdd = Color(0xffffffff)
        val rowBackgroundEven = Color(0xffededed)

        val textDark = Color(0xff000000)
        val textLight = Color(0xffffffff)

        object Primary {
            val background = Color(0xff596157)
            val content = textLight
        }
        object Secondary {
            val background = Color(0xff5B8C5A)
            val content = textLight
        }

        object Tertiary {
            val background = Color(0xffCFD186)
            val content = textDark
        }

        object Warning {
            val background = Color(0xffE3655B)
            val content = textLight
        }

        object Disabled {
            val background = Color(0xffbababa)
            val content = Color(0xff4a4a4a)
        }

        val dividerColor = Color(0xffadadad)

        val pageBackground = Color(0xffededed)
        val cardBackground = Color(0xffffffff)
        val cardTitleBackground = Color(0xffdedede)

        val errorText = Color(0xffc23838)

        val transparent = Color(0x00000000)

        //val disabledBackground = Color(0xffbababa)

        val codeBlock = Color(0xffe0e0e0)

        val rowBackground = Color(0x00000000)
        val rowText = textDark
        val rowHoverBackground = Color(0x11000000)
        val rowHoverText = Color(0xff000000)

        val rowSelectedBackground = Tertiary.background
        val rowSelectedText = Tertiary.content

        object Button {
            var background = Secondary.background
            var text = Secondary.content
        }

        object ButtonOutlined {
            var border = Secondary.background
            var text = Secondary.background
        }

        object ButtonDelete {
            var background = Warning.background
            var text = Warning.content
        }

        object TopAppBar {
            var background = Primary.background
            var text = Primary.content
        }

        object Card {
            object SubTitle {
                var background = Tertiary.background
                var text = Tertiary.content
            }
        }

        object OutlinedTextField {
            val focusedBorderColor = R.Color.Primary.background
            val focusedLabelColor = R.Color.Primary.background
        }
    }
}