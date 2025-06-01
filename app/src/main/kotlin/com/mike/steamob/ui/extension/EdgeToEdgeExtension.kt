package com.mike.steamob.ui.extension

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun adaptEdgeToEdge(rootView: View) {
    ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, windowInsets ->
        val insets = windowInsets.getInsets(
            WindowInsetsCompat.Type.systemBars()
                    or WindowInsetsCompat.Type.displayCutout()
        )
        v.setPadding(insets.left, insets.top, insets.right, insets.bottom)

        WindowInsetsCompat.CONSUMED
    }
}
