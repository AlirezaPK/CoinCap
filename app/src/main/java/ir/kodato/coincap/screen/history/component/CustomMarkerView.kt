package ir.kodato.coincap.screen.history.component

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import ir.kodato.coincap.R
import ir.kodato.coincap.util.calculateRsiAtTime

class CustomMarkerView(
    context: Context,
    layout: Int,
    private val y: List<Float>,
    private val x: List<String>,
) : MarkerView(context, layout) {

    private var txtViewData: TextView? = null

    init {
        txtViewData = findViewById(R.id.txtViewData)
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        try {
            val xAxis = e?.x?.toInt() ?: 0
            val rsi = calculateRsiAtTime(y, xAxis)
            val text = if (rsi != null) {
                "${y[xAxis]} - ${x[xAxis]}\nRSI: $rsi"
            } else {
                "${y[xAxis]} - ${x[xAxis]}"
            }
            txtViewData?.text = text
        } catch (e: IndexOutOfBoundsException) {
        }

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2f), -height.toFloat())
    }
}