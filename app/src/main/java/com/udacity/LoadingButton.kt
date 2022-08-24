package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.properties.Delegates
import androidx.core.animation.doOnEnd
import androidx.core.content.withStyledAttributes

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0f
    private var heightSize = 0f
    private var progress = 0f
    private var valueAnimator = ValueAnimator()
    private var backColor = 0

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private val circlePaint = Paint(Paint.DITHER_FLAG).apply {
        style = Paint.Style.FILL
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Loading -> {
                this.isEnabled = false
                valueAnimator.start()
            }
            ButtonState.Completed -> {
                this.isEnabled = true
                valueAnimator.cancel()
            }

            ButtonState.Clicked -> {
                this.isEnabled = false
                valueAnimator.start()
            }
        }
    }

    private val fillPaint = Paint(Paint.DITHER_FLAG).apply {
        style = Paint.Style.FILL
    }

    init {
        context.withStyledAttributes(attrs,R.styleable.LoadingButton){
            backColor = getColor(R.styleable.LoadingButton_backColor,0)
            textPaint.color = getColor(R.styleable.LoadingButton_textColor,0)
            circlePaint.color = getColor(R.styleable.LoadingButton_circleColor,0)
            fillPaint.color = getColor(R.styleable.LoadingButton_fillColor,0)
        }

        this.isClickable = true
        valueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2000
            addUpdateListener {
                progress = it.animatedValue as Float
                valueAnimator.repeatCount = 0
                valueAnimator.repeatMode = ValueAnimator.REVERSE
                valueAnimator.interpolator = LinearInterpolator()
                invalidate()
            }
        }

        valueAnimator.doOnEnd {
            progress = 0f
            buttonState = ButtonState.Completed
            invalidate()
        }

    }

    fun onComplete() {
        buttonState = ButtonState.Completed
    }

    override fun performClick(): Boolean {
        buttonState = ButtonState.Loading
        super.performClick()
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.drawColor(backColor)

        if (buttonState == ButtonState.Loading) {
            canvas.drawRect(0f, 0f, progress * widthSize, heightSize, fillPaint)
            val textEnd = drawCenteredText(
                canvas,
                "We are downloading"
            ) + 16f // set a space of 16 between the text and the circle

            canvas.drawArc(
                textEnd,
                heightSize * 0.3f,
                textEnd + heightSize * 0.4f,
                heightSize * 0.7f,
                0f,
                360 * progress,
                true,
                circlePaint
            )
        } else {
            drawCenteredText(canvas, context.getString(R.string.download))
        }
    }


    private fun drawCenteredText(
        canvas: Canvas,
        text: String
    ): Float { //return the width. It is needed to position the circle

        textPaint.textAlign = Paint.Align.LEFT
        val textRect = Rect()
        textPaint.getTextBounds(text, 0, text.length, textRect)
        val x: Float = canvas.clipBounds.width() / 2f - textRect.width() / 2f - textRect.left
        val y: Float = canvas.clipBounds.height() / 2f + textRect.height() / 2f - textRect.bottom
        canvas.drawText(text, x, y, textPaint)
        return x + textRect.width()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w.toFloat()
        heightSize = h.toFloat()
        setMeasuredDimension(w, h)
    }




}