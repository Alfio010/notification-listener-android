package com.android.whatsappbackup.activities.home

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginTop
import com.android.whatsappbackup.R
import com.android.whatsappbackup.utils.DBUtils
import com.android.whatsappbackup.utils.Utils
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import java.util.Random


class PieGraphActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        runOnUiThread {
            setContentView(R.layout.activity_pie_graph)
            Utils.uiDefaultSettings(this)
        }

        val llGraphActivity: LinearLayout by lazy { findViewById(R.id.llGraphActivity) }
        val lvOthers: ListView by lazy { findViewById(R.id.lvOthers) }

        val pieChart: PieChart = findViewById(R.id.pieChart)

        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

        pieChart.isDrawHoleEnabled = false

        pieChart.isRotationEnabled = true
        pieChart.animateY(999, Easing.EaseInOutQuart)

        pieChart.legend.isEnabled = false

        val percentageMap = DBUtils.getPercentNotifications(this)
        val others: MutableList<String> = mutableListOf()

        val entries: ArrayList<PieEntry> = ArrayList()
        percentageMap.forEach {
            if (it.value >= 5f || it.key == this.getString(R.string.others)) {
                entries.add(PieEntry(it.value, it.key))
            } else {
                others.add("${it.key} ${"%.1f".format(it.value)}%")
            }
        }

        if (others.isNotEmpty()) {
            val adapter: ArrayAdapter<String> =
                ArrayAdapter(this, android.R.layout.simple_list_item_1, others.toTypedArray())
            runOnUiThread {
                lvOthers.adapter = adapter
                llGraphActivity.visibility = View.VISIBLE
            }
        }

        val dataSet = PieDataSet(entries, "Notifications")

        dataSet.setDrawIcons(true)
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        dataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE

        val colors: ArrayList<Int> = ArrayList()
        val rnd = Random()
        percentageMap.forEach { _ ->
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

        if (Utils.isDarkThemeOn(this)) {
            pieChart.setEntryLabelColor(Color.WHITE)
            data.setValueTextColor(Color.WHITE)
        } else {
            pieChart.setEntryLabelColor(Color.BLACK)
            data.setValueTextColor(Color.BLACK)
        }

        pieChart.data = data

        runOnUiThread { pieChart.invalidate() }
    }
}