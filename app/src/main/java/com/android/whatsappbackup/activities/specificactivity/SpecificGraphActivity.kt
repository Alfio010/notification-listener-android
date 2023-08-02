package com.android.whatsappbackup.activities.specificactivity

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.android.whatsappbackup.R
import com.android.whatsappbackup.utils.DBUtils.getSpecificNotificationsForGraph
import com.android.whatsappbackup.utils.UiUtils
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import java.util.Random

class SpecificGraphActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runOnUiThread {
            setContentView(R.layout.activity_specific_graph)
            UiUtils.uiDefaultSettings(this, true)
        }

        val appLabel = intent.extras!!.getString("appLabel", String())

        val pieChart: PieChart = findViewById(R.id.specificPieGraph)

        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

        pieChart.isDrawHoleEnabled = false

        pieChart.isRotationEnabled = true
        pieChart.animateY(999, Easing.EaseInOutQuart)

        pieChart.legend.isEnabled = false

        val specificNotifications = getSpecificNotificationsForGraph(appLabel)
        val entries: ArrayList<PieEntry> = ArrayList()
        specificNotifications.forEach {
            entries.add(PieEntry(it.value, it.key))
        }

        val dataSet = PieDataSet(entries, "Notifications")

        dataSet.setDrawIcons(true)
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        dataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE

        val colors: ArrayList<Int> = ArrayList()
        val rnd = Random()
        specificNotifications.forEach { _ ->
            colors.add(
                Color.argb(
                    255,
                    rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)
                )
            )
        }

        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(15f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)

        if (UiUtils.isDarkThemeOn(this)) {
            pieChart.setEntryLabelColor(Color.WHITE)
            data.setValueTextColor(Color.WHITE)
        } else {
            pieChart.setEntryLabelColor(Color.BLACK)
            data.setValueTextColor(Color.BLACK)
        }

        pieChart.data = data

        runOnUiThread { pieChart.invalidate() }

        onBackPressedDispatcher.addCallback {
            finishAndRemoveTask()
        }
    }
}