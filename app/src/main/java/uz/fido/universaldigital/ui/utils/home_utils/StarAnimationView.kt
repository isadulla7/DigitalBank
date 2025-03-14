package uz.fido.universaldigital.ui.utils.home_utils

import android.animation.TimeAnimator
import android.animation.TimeAnimator.TimeListener
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import uz.fido.universaldigital.R
import java.util.*

/**
 * Continuous animation where stars slide from the top to the bottom
 */
class StarAnimationView : View {
    /**
     * Class representing the state of a star
     */
    private class Star {
        var x = 0f
        var y = 0f
        var scale = 0f
        var alpha = 0f
        var speed = 0f
    }

    private val mStars = arrayOfNulls<Star>(COUNT)
    private val mRnd = Random(SEED.toLong())
    private var mTimeAnimator: TimeAnimator? = null
    private var mDrawable: Drawable? = null
    private var mBaseSpeed = 0f
    private var mBaseSize = 0f
    private var mCurrentPlayTime: Long = 0

    /** @see View.View
     */
    constructor(context: Context?) : super(context) {
        init()
    }

    /** @see View.View
     */
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    /** @see View.View
     */
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    private fun init() {
        mDrawable = ContextCompat.getDrawable(context, R.drawable.ic_group_1)
        mBaseSize = mDrawable!!.intrinsicWidth.coerceAtLeast(mDrawable!!.intrinsicHeight) / 2f
        mBaseSpeed = BASE_SPEED_DP_PER_S * resources.displayMetrics.density
    }

    override fun onSizeChanged(width: Int, height: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(width, height, oldw, oldh)

        // The starting position is dependent on the size of the view,
        // which is why the model is initialized here, when the view is measured.
        for (i in mStars.indices) {
            val star = Star()
            initializeStar(star, width, height)
            mStars[i] = star
        }
    }

    override fun onDraw(canvas: Canvas) {
        val viewHeight = height
        for (star in mStars) {
            // Ignore the star if it's outside of the view bounds
            val starSize = star!!.scale * mBaseSize
            if (star.y + starSize < 0 || star.y - starSize > viewHeight) {
                continue
            }

            // Save the current canvas state
            val save = canvas.save()

            // Move the canvas to the center of the star
            canvas.translate(star.x, star.y)

            // Rotate the canvas based on how far the star has moved
            val progress = (star.y + starSize) / viewHeight
            canvas.rotate(360 * progress)

            // Prepare the size and alpha of the drawable
            val size = Math.round(starSize)
            mDrawable!!.setBounds(-size, -size, size, size)
            mDrawable!!.alpha = Math.round(255 * star.alpha)

            // Draw the star to the canvas
            mDrawable!!.draw(canvas)

            // Restore the canvas to it's previous position and rotation
            canvas.restoreToCount(save)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mTimeAnimator = TimeAnimator()
        mTimeAnimator!!.setTimeListener(TimeListener { animation, totalTime, deltaTime ->
            if (!isLaidOut) {
                // Ignore all calls before the view has been measured and laid out.
                return@TimeListener
            }
            updateState(deltaTime.toFloat())
            invalidate()
        })
        mTimeAnimator!!.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mTimeAnimator!!.cancel()
        mTimeAnimator!!.setTimeListener(null)
        mTimeAnimator!!.removeAllListeners()
        mTimeAnimator = null
    }

    /**
     * Pause the animation if it's running
     */
    fun pause() {
        if (mTimeAnimator != null && mTimeAnimator!!.isRunning) {
            // Store the current play time for later.
            mCurrentPlayTime = mTimeAnimator!!.currentPlayTime
            mTimeAnimator!!.pause()
        }
    }

    /**
     * Resume the animation if not already running
     */
    fun resume() {
        if (mTimeAnimator != null && mTimeAnimator!!.isPaused) {
            mTimeAnimator!!.start()
            // Why set the current play time?
            // TimeAnimator uses timestamps internally to determine the delta given
            // in the TimeListener. When resumed, the next delta received will the whole
            // pause duration, which might cause a huge jank in the animation.
            // By setting the current play time, it will pick of where it left off.
            mTimeAnimator!!.currentPlayTime = mCurrentPlayTime
        }
    }

    /**
     * Progress the animation by moving the stars based on the elapsed time
     * @param deltaMs time delta since the last frame, in millis
     */
    private fun updateState(deltaMs: Float) {
        // Converting to seconds since PX/S constants are easier to understand
        val deltaSeconds = deltaMs / 1000f
        val viewWidth = width
        val viewHeight = height
        for (star in mStars) {
            // Move the star based on the elapsed time and it's speed
            star!!.y += star!!.speed * deltaSeconds

            // If the star is completely outside of the view bounds after
            // updating it's position, recycle it.
            val size = star.scale * mBaseSize
            if (star.y + size > viewHeight) {
                Log.d("ddd", "init")
                initializeStar(star, viewWidth, viewHeight)
            }
        }
    }

    /**
     * Initialize the given star by randomizing it's position, scale and alpha
     * @param star the star to initialize
     * @param viewWidth the view width
     * @param viewHeight the view height
     */
    private fun initializeStar(star: Star?, viewWidth: Int, viewHeight: Int) {
        // Set the scale based on a min value and a random multiplier
        star!!.scale = SCALE_MIN_PART + SCALE_RANDOM_PART * mRnd.nextFloat()

        // Set X to a random value within the width of the view
        star.x = viewWidth * mRnd.nextFloat()

        // Set the Y position
        // Start at the bottom of the view
        star.y = 0f
        // The Y value is in the center of the star, add the size
        // to make sure it starts outside of the view bound
        star.y += star.scale * mBaseSize
        // Add a random offset to create a small delay before the
        // star appears again.
        star.y += viewHeight * mRnd.nextFloat() / 4f

        // The alpha is determined by the scale of the star and a random multiplier.
        star.alpha = ALPHA_SCALE_PART * star.scale + ALPHA_RANDOM_PART * mRnd.nextFloat()
        // The bigger and brighter a star is, the faster it moves
        star.speed = mBaseSpeed * star.alpha * star.scale
    }

    companion object {
        private const val BASE_SPEED_DP_PER_S = 200
        private const val COUNT = 32
        private const val SEED = 1337

        /** The minimum scale of a star  */
        private const val SCALE_MIN_PART = 0.45f

        /** How much of the scale that's based on randomness  */
        private const val SCALE_RANDOM_PART = 0.55f

        /** How much of the alpha that's based on the scale of the star  */
        private const val ALPHA_SCALE_PART = 0.5f

        /** How much of the alpha that's based on randomness  */
        private const val ALPHA_RANDOM_PART = 0.5f
    }
}