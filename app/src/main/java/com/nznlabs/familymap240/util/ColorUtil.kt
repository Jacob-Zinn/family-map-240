package com.nznlabs.familymap240.util

import android.graphics.Color
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class ColorUtil {

    enum class EventColors(val color: Float) {
        BIRTH_COLOR(BitmapDescriptorFactory.HUE_GREEN),
        DEATH_COLOR(BitmapDescriptorFactory.HUE_VIOLET),
        MARRIAGE_COLOR(BitmapDescriptorFactory.HUE_ROSE),
        OTHER_1(BitmapDescriptorFactory.HUE_AZURE),
        OTHER_2(BitmapDescriptorFactory.HUE_CYAN),
        OTHER_3(BitmapDescriptorFactory.HUE_YELLOW),
        OTHER_4(BitmapDescriptorFactory.HUE_ORANGE),
        OTHER_5(BitmapDescriptorFactory.HUE_BLUE),
        OTHER_6(BitmapDescriptorFactory.HUE_RED),
        ERROR_COLOR(BitmapDescriptorFactory.HUE_ORANGE),
    }

    enum class LineColors(val color: Int) {
        SPOUSE(Color.MAGENTA),
        TREE(Color.BLUE),
        LIFE_STORY(Color.YELLOW),
        ERROR_COLOR(Color.BLACK)
    }

    companion object {
        fun selectColor(int: Int): Float {
            when (int) {
                0 -> {
                    return EventColors.BIRTH_COLOR.color
                }
                1 -> {
                    return EventColors.DEATH_COLOR.color
                }
                2 -> {
                    return EventColors.MARRIAGE_COLOR.color
                }
                3 -> {
                    return EventColors.OTHER_1.color
                }
                4 -> {
                    return EventColors.OTHER_2.color
                }
                5 -> {
                    return EventColors.OTHER_3.color
                }
                6 -> {
                    return EventColors.OTHER_4.color
                }
                7 -> {
                    return EventColors.OTHER_5.color
                }
                8 -> {
                    return EventColors.OTHER_6.color
                }
                else -> {
                    return EventColors.ERROR_COLOR.color
                }
            }
        }
    }

}