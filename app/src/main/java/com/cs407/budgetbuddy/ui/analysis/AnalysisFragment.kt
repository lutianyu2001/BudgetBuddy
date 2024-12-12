package com.cs407.budgetbuddy.ui.analysis

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs407.budgetbuddy.R
import com.cs407.budgetbuddy.adapter.ChartLegendAdapter
import com.cs407.budgetbuddy.databinding.FragmentAnalysisBinding
import com.cs407.budgetbuddy.model.ChartTimeFrame
import com.cs407.budgetbuddy.model.LineChartData
import com.cs407.budgetbuddy.model.PieChartData
import com.cs407.budgetbuddy.ui.common.ViewModelFactory
import com.cs407.budgetbuddy.util.NumberFormatter
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class AnalysisFragment : Fragment() {

    private var _binding: FragmentAnalysisBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AnalysisViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    private val legendAdapter = ChartLegendAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        // Setup RecyclerView for chart legend
        binding.rvLegend.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = legendAdapter
            setHasFixedSize(true)
        }

        // Setup time frame selection buttons
        binding.apply {
            lineWeek.setOnClickListener {
                viewModel.setTimeFrame(ChartTimeFrame.WEEK)
                showLineChart()
            }
            lineMonth.setOnClickListener {
                viewModel.setTimeFrame(ChartTimeFrame.MONTH)
                showPieChart()
            }
            lineYear.setOnClickListener {
                viewModel.setTimeFrame(ChartTimeFrame.YEAR)
                showPieChart()
            }
        }

        setupLineChart()
        setupPieChart()
    }

    private fun setupLineChart() {
        binding.lineChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(true)
            setScaleEnabled(false)
            setPinchZoom(false)
            setDrawGridBackground(false)
            setNoDataText("No data available")

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                labelRotationAngle = -45f
                valueFormatter = IndexAxisValueFormatter()
            }

            axisLeft.apply {
                setDrawGridLines(true)
                axisMinimum = 0f
                valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "$${value.toInt()}"
                    }
                }
            }

            axisRight.isEnabled = false
        }
    }

    private fun setupPieChart() {
        binding.pieChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setUsePercentValues(true)
            setDrawEntryLabels(false)
            isRotationEnabled = false
            isHighlightPerTapEnabled = true
            setHoleColor(Color.TRANSPARENT)
            holeRadius = 58f
            transparentCircleRadius = 61f
            setDrawCenterText(true)
            centerText = "Expenses"
        }
    }

    private fun observeViewModel() {
        viewModel.lineChartData.observe(viewLifecycleOwner) { data ->
            updateLineChart(data)
        }

        viewModel.pieChartData.observe(viewLifecycleOwner) { data ->
            updatePieChart(data)
            updateLegend(data)
        }

        viewModel.summary.observe(viewLifecycleOwner) { summary ->
            binding.apply {
                textBalance.text = "$${NumberFormatter.formatCurrency(summary.balance)}"
                textIncome.text = "$${NumberFormatter.formatCurrency(summary.totalIncome)}"
                textExpenses.text = "$${NumberFormatter.formatCurrency(summary.totalExpenses)}"
            }
        }

        viewModel.selectedTimeFrame.observe(viewLifecycleOwner) { timeFrame ->
            updateSelectedTimeFrameUI(timeFrame)
            when (timeFrame) {
                ChartTimeFrame.WEEK -> showLineChart()
                else -> showPieChart()
            }
        }

//        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
//            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
//        }
    }

    private fun updateLineChart(data: LineChartData) {
        val entries = data.values.mapIndexed { index, value ->
            Entry(index.toFloat(), value.toFloat())
        }

        val dataSet = LineDataSet(entries, "Expenses").apply {
            color = ContextCompat.getColor(requireContext(), R.color.purple_500)
            setCircleColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
            setDrawCircleHole(false)
            lineWidth = 2f
            circleRadius = 4f
            valueTextSize = 10f
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawValues(true)
            valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "$${value.toInt()}"
                }
            }
        }

        binding.lineChart.apply {
            this.data = LineData(dataSet)
            xAxis.valueFormatter = IndexAxisValueFormatter(data.labels)
            animateX(1000, Easing.EaseInOutQuart)
            invalidate()
        }
    }

    private fun updatePieChart(data: PieChartData) {
        val entries = data.values.mapIndexed { index, value ->
            PieEntry(value.toFloat(), data.categories[index])
        }

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                ContextCompat.getColor(requireContext(), R.color.pie_1),
                ContextCompat.getColor(requireContext(), R.color.pie_2),
                ContextCompat.getColor(requireContext(), R.color.pie_3),
                ContextCompat.getColor(requireContext(), R.color.pie_4),
                ContextCompat.getColor(requireContext(), R.color.pie_5),
                ContextCompat.getColor(requireContext(), R.color.pie_6)
            )
            setDrawValues(false)
            sliceSpace = 2f
        }

        binding.pieChart.apply {
            this.data = PieData(dataSet)
            animateY(1000, Easing.EaseInOutQuad)
            invalidate()
        }
    }

    private fun updateLegend(data: PieChartData) {
        legendAdapter.setData(
            categories = data.categories,
            amounts = data.values,
            percentages = data.percentages
        )
    }

    private fun updateSelectedTimeFrameUI(timeFrame: ChartTimeFrame) {
        val selectedColor = ContextCompat.getColor(requireContext(), R.color.purple_500)
        val unselectedColor = ContextCompat.getColor(requireContext(), R.color.gray)

        binding.apply {
            textWeek.setTextColor(if (timeFrame == ChartTimeFrame.WEEK) selectedColor else unselectedColor)
            textMonth.setTextColor(if (timeFrame == ChartTimeFrame.MONTH) selectedColor else unselectedColor)
            textYear.setTextColor(if (timeFrame == ChartTimeFrame.YEAR) selectedColor else unselectedColor)
        }
    }

    private fun showLineChart() {
        binding.apply {
            lineChart.visibility = View.VISIBLE
            pieChart.visibility = View.GONE
            rvLegend.visibility = View.GONE
        }
    }

    private fun showPieChart() {
        binding.apply {
            lineChart.visibility = View.GONE
            pieChart.visibility = View.VISIBLE
            rvLegend.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = AnalysisFragment()
    }
}