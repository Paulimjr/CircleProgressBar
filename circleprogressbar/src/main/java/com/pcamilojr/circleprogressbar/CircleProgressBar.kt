package com.pcamilojr.circleprogressbar

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.math.roundToInt

/**
 * Circle Progressbar is a subclass of {@link android.view.View}
 *          class for creates a circular progressbar with animation and color.
 *
 * Created by Paulo Cesar Camilo on 2020-01-02.
 *
 */
class CircleProgressBar(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var min = 0 // Sets the minimum value of the progress
    private var max = 100 //Sets the maximum value of the progress
    private var strokeWidth: Float = 8.5f // Sets the stroke width of the progress
    private var startAngle = -90
    private var progress: Float = 0f
    private lateinit var mRectF: RectF
    private var color = Color.GRAY // Sets the color of the progress
    private var TAG = CircleProgressBar::class.java.simpleName
    private lateinit var mCirclePaint: Paint
    private var mListener: CircleProgressBarCallback? = null
    private lateinit var progressIdentifier: String

    companion object {
        var ALPHA_DEFAULT = 255
        var ALPHA_DISABLED = 45
    }
    private var mCurrentProgress = "0%"
    private var mIsActive = false

    init {
        initCircleProgressBar(context, attrs)

            .apply {
                isActive(isActive = true)
            }
    }

    /**
     * Initialize all values for the Circle Progressbar
     */
    private fun initCircleProgressBar(context: Context?, attrs: AttributeSet?) {
        mRectF = RectF()

        attrs?.let {
            val typedArray = context?.theme?.obtainStyledAttributes(
                attrs,
                R.styleable.CircleProgressBar,
                0, 0
            )

            typedArray?.let {
                try {
                    progress = it.getFloat(R.styleable.CircleProgressBar_progress, progress)
                    color = it.getInt(R.styleable.CircleProgressBar_progressColor, color)
                    max = it.getInt(R.styleable.CircleProgressBar_maxProgress, max)
                    min = it.getInt(R.styleable.CircleProgressBar_minProgress, min)
                    strokeWidth =
                        it.getInt(R.styleable.CircleProgressBar_strokeWidth, strokeWidth.toInt()).toFloat()
                    progressIdentifier = it.getString(R.styleable.CircleProgressBar_progressIdentifier).toString()

                    //Created the paint for ProgressBar
                    this.createdCirclePaint()
                } catch (e: Exception) {
                    Log.e(TAG, "Error: ${e.message}")
                } finally {
                    it.recycle()
                }
            }
        }
    }

    fun getIdentifier() = this.progressIdentifier

    /**
     * Create a Circle Paint
     */
    private fun createdCirclePaint() {
        mCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mCirclePaint.color = color
        mCirclePaint.style = Paint.Style.STROKE
        mCirclePaint.isAntiAlias = true
        mCirclePaint.strokeCap = Paint.Cap.ROUND
        mCirclePaint.strokeWidth = strokeWidth
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val angle = 360 * progress / max
        canvas?.drawArc(mRectF, startAngle.toFloat(), angle, false, mCirclePaint)
        //Returning the progress
        mCurrentProgress = "${calcProgressFromAngle(angle)}%"

        if (mIsActive) {
            mListener?.onProgressValue("${calcProgressFromAngle(angle)}%", mCirclePaint.color)
        } else {
            mCirclePaint.alpha = ALPHA_DISABLED
        }
    }

    /**
     * Run progress returns current value of progress
     *      Ex: 1, 4, 20%
     */
    fun runProgress(listener: CircleProgressBarCallback) {
        this.mListener = listener
    }

    private fun calcProgressFromAngle(angleProgress: Float) : Int {
        return ((angleProgress * 100) / 360).roundToInt()
    }

    /**
     * Active or Inactive progress bar to change the color
     */
    fun isActive(isActive: Boolean) {
        if (isActive) {
            mCirclePaint.color = color
            mCirclePaint.alpha = ALPHA_DEFAULT
        } else {
            mCirclePaint.alpha = ALPHA_DISABLED
        }

        mIsActive = isActive
        invalidate()
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val width = getDefaultSize(suggestedMinimumWidth, heightMeasureSpec)
        val min = width.coerceAtMost(height)
        setMeasuredDimension(min, min)
        this.mRectF.set(
            0 + strokeWidth / 2,
            0 + strokeWidth / 2,
            min - strokeWidth / 2,
            min - strokeWidth / 2
        )
    }

    /**
     * Set the progress with an animation.
     * Note that the {@link android.animation.ObjectAnimator} Class automatically set the progress
     *
     * @param progress the progress it should animate to it.
     * @param startDelay the start animation with a delay
     */
    fun setProgressAnimation(progress: Float, startDelay: Int = 0) {
        val objectAnimator = ObjectAnimator.ofFloat(this, "progress", progress)
        val decelerateInterpolator = DecelerateInterpolator()
        objectAnimator.startDelay = startDelay.toLong()
        objectAnimator.duration = 1000
        objectAnimator.interpolator = decelerateInterpolator
        objectAnimator.start()

        objectAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                mListener?.onProgressEnd()
            }

            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

        })
    }

    /**
     * Set the current progress of circle
     */
    fun setProgress(progress: Float) {
        this.progress = progress
        invalidate()
    }

    /**
     * Set the min value of the progress
     */
    fun setMinProgress(min: Int) {
        this.min = min
        invalidate()
    }

    /**
     * Set the max value of the progress
     */
    fun setMaxProgress(max: Int) {
        this.max = max
        invalidate()
    }

    /**
     * Set the color of the progress
     */
    fun setColor(color: Int) {
        this.color = color
        mCirclePaint.color = color
        invalidate()
        requestLayout()
    }

    /**
     * Interface to manage the callback when progress changed
     */
    interface CircleProgressBarCallback {

        /**
         * Get the current value of the progress and color
         */
        fun onProgressValue(progress: String, textColor: Int)

        /**
         * Callback when the progress is finished
         */
        fun onProgressEnd()
    }
}