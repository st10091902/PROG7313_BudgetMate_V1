package com.marcomarais.budgetmate.ui.reports

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class BarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var data: List<Pair<String, Double>> = emptyList()

    private val barPaint = Paint().apply {
        color = Color.rgb(46, 125, 50)
        style = Paint.Style.FILL
    }

    private val textPaint = Paint().apply {
        color = Color.rgb(38, 50, 56)
        textSize = 32f
        isAntiAlias = true
    }

    private val axisPaint = Paint().apply {
        color = Color.GRAY
        strokeWidth = 3f
    }

    fun setData(newData: List<Pair<String, Double>>) {
        data = newData
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (data.isEmpty()) {
            canvas.drawText("No spending data to display", 40f, 80f, textPaint)
            return
        }

        val paddingLeft = 60f
        val paddingBottom = 80f
        val chartHeight = height - 140f
        val chartWidth = width - 100f

        val maxAmount = data.maxOf { it.second }.coerceAtLeast(1.0)
        val barWidth = chartWidth / (data.size * 2)

        // Draw x-axis
        canvas.drawLine(
            paddingLeft,
            height - paddingBottom,
            width - 40f,
            height - paddingBottom,
            axisPaint
        )

        data.forEachIndexed { index, item ->
            val categoryName = item.first
            val amount = item.second

            val left = paddingLeft + index * barWidth * 2 + barWidth / 2
            val right = left + barWidth
            val barHeight = ((amount / maxAmount) * chartHeight).toFloat()
            val top = height - paddingBottom - barHeight
            val bottom = height - paddingBottom

            canvas.drawRect(left, top, right, bottom, barPaint)

            canvas.drawText(
                "R%.0f".format(amount),
                left,
                top - 10f,
                textPaint
            )

            canvas.drawText(
                categoryName.take(8),
                left,
                height - 30f,
                textPaint
            )
        }
    }
}