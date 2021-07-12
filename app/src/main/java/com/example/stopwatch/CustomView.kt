package com.example.stopwatch

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes


class CustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
    ): View(context, attrs, defStyleAttr) {

        private var periodMs = 0L
        private var currentMs = 0L
        private var color = 0
        private var style = FILL
        private val paint = Paint()
        private var state = true

    init {

        if (attrs != null){

            val styleAttrs = context.theme.obtainStyledAttributes(
                attrs, R.styleable.CustomView, defStyleAttr, 0
            )

            color = styleAttrs.getColor(R.styleable.CustomView_custom_color, Color.CYAN)
            style = styleAttrs.getInt(R.styleable.CustomView_custom_style, FILL)
            styleAttrs.recycle()

        }

        paint.color = color
        paint.style = if (style == FILL) Paint.Style.FILL else Paint.Style.STROKE
        paint.strokeWidth = 0.1F

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (periodMs == 0L || currentMs == 0L) return
        val startAngel = (((currentMs % periodMs).toFloat() / periodMs) * 360)

        canvas.drawArc(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            -90f,
            -startAngel,
            true,
            paint
        )

        if (currentMs == periodMs){


            canvas.drawArc(
                0f,
                0f,
                width.toFloat(),
                height.toFloat(),
                -90f,
                360F,
                true,
                paint
            )

        }

    }

    fun setCurrent(current: Long, state: Boolean) {
        currentMs = current
        this.state = state
        invalidate()
    }

    fun setPeriod(period: Long) {
        periodMs = period
    }

    private companion object {
        private const val FILL = 0
    }


}