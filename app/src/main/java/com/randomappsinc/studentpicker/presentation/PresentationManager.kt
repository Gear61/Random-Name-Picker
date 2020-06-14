package com.randomappsinc.studentpicker.presentation

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.randomappsinc.studentpicker.R
import com.randomappsinc.studentpicker.utils.UIUtils

class PresentationManager(
        private val listener: Listener,
        private val view: View) {

    interface Listener {
        fun speakNames()
    }

    private var player: MediaPlayer? = null
    private var handler: Handler? = null
    private val animateNamesTask = ::animateNames
    private var defaultAnimationLengthMs: Long = 0

    init {
        handler = Handler()
        player = MediaPlayer()
        defaultAnimationLengthMs = view.context.resources.getInteger(R.integer.default_anim_length).toLong()
    }

    fun playSoundOrAnimations() {
        view.clearAnimation()
        view.alpha = 0.0f
        if ((view.context.getSystemService(Context.AUDIO_SERVICE) as AudioManager)
                        .getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
            playNamesAnimation(defaultAnimationLengthMs)
        } else {
            playSound()
        }
    }

    private fun playSound() {
        try {
            val fileDescriptor: AssetFileDescriptor =
                    view.context.assets.openFd(PresentationActivity.DRUMROLL_FILE_NAME)
            player!!.reset()
            player!!.setDataSource(
                    fileDescriptor.fileDescriptor,
                    fileDescriptor.startOffset,
                    fileDescriptor.length)
            player!!.prepare()
            player!!.start()
        } catch (ex: Exception) {
            UIUtils.showLongToast(R.string.drumroll_error, view.context)
        }
        playNamesAnimation(2600)
    }

    private fun playNamesAnimation(delayMs: Long) {
        handler!!.removeCallbacks(animateNamesTask)
        handler!!.postDelayed(animateNamesTask, delayMs)
    }

    private fun animateNames() {
        val fadeIn: ObjectAnimator = ObjectAnimator
                .ofFloat(view, View.ALPHA, 1.0f)
                .setDuration(defaultAnimationLengthMs)
        fadeIn.interpolator = AccelerateInterpolator()
        val scaleSet = AnimatorSet()
        val scaleX: ObjectAnimator = ObjectAnimator
                .ofFloat(view, View.SCALE_X, 1.0f, 1.5f, 1.0f)
                .setDuration(defaultAnimationLengthMs)
        val scaleY: ObjectAnimator = ObjectAnimator
                .ofFloat(view, View.SCALE_Y, 1.0f, 1.5f, 1.0f)
                .setDuration(defaultAnimationLengthMs)
        scaleSet.interpolator = DecelerateInterpolator()
        scaleSet.playTogether(scaleX, scaleY)
        val fullSet = AnimatorSet()
        fullSet.playSequentially(fadeIn, scaleSet)
        fullSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}
            override fun onAnimationEnd(animator: Animator) {
                listener.speakNames()
            }

            override fun onAnimationCancel(animator: Animator) {}
            override fun onAnimationRepeat(animator: Animator) {}
        })
        fullSet.start()
    }

    fun stopPlayer() {
        player!!.stop()
    }
}