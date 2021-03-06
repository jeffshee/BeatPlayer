package com.crrl.beatplayer.ui.transformers

import android.view.View
import com.crrl.beatplayer.ui.transformers.base.BaseTransformer
import kotlin.math.abs

class DepthPageTransformer : BaseTransformer() {

    private val MIN_SCALE = 0.75f

    override fun onTransform(view: View, position: Float) {
        if (position <= 0f) {
            view.translationX = 0f
            view.scaleX = 1f
            view.scaleY = 1f
        } else if (position <= 1f) {
            val scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - abs(position))
            view.alpha = 1 - position
            view.pivotY = 0.5f * view.height
            view.translationX = view.width * -position
            view.scaleX = scaleFactor
            view.scaleY = scaleFactor
        }

    }

    override fun isPagingEnabled() = true
}