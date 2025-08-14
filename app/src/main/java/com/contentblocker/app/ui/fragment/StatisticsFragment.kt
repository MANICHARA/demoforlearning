package com.contentblocker.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.contentblocker.app.databinding.FragmentStatisticsBinding
import com.contentblocker.app.utils.PreferenceManager
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

class StatisticsFragment : Fragment() {
    
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var preferenceManager: PreferenceManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferenceManager = PreferenceManager(requireContext())
        setupUI()
        loadStatistics()
    }
    
    private fun setupUI() {
        // Set up chart
        binding.chartWeekly.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            legend.isEnabled = true
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
        }
        
        // Set up refresh button
        binding.btnRefresh.setOnClickListener {
            loadStatistics()
        }
        
        // Set up reset button
        binding.btnReset.setOnClickListener {
            showResetConfirmationDialog()
        }
    }
    
    private fun loadStatistics() {
        // Load daily statistics
        val blockedToday = preferenceManager.getBlockedCountToday()
        val timeSavedToday = preferenceManager.getTimeSavedToday()
        
        binding.tvBlockedToday.text = blockedToday.toString()
        binding.tvTimeSavedToday.text = preferenceManager.formatTimeSaved(timeSavedToday)
        
        // Load weekly statistics
        val weeklyStats = preferenceManager.getWeeklyStats()
        setupWeeklyChart(weeklyStats)
        
        // Load monthly statistics
        val monthlyStats = preferenceManager.getMonthlyStats()
        setupMonthlyChart(monthlyStats)
        
        // Load total statistics
        val totalBlocked = preferenceManager.getTotalBlockedCount()
        val totalTimeSaved = preferenceManager.getTotalTimeSaved()
        
        binding.tvTotalBlocked.text = totalBlocked.toString()
        binding.tvTotalTimeSaved.text = preferenceManager.formatTimeSaved(totalTimeSaved)
    }
    
    private fun setupWeeklyChart(weeklyStats: Map<String, Int>) {
        val entries = weeklyStats.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }
        
        val dataSet = BarDataSet(entries, "Blocked Content")
        dataSet.color = resources.getColor(R.color.primary, null)
        
        val barData = BarData(dataSet)
        binding.chartWeekly.data = barData
        binding.chartWeekly.invalidate()
    }
    
    private fun setupMonthlyChart(monthlyStats: Map<String, Int>) {
        val entries = monthlyStats.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }
        
        val dataSet = BarDataSet(entries, "Blocked Content")
        dataSet.color = resources.getColor(R.color.secondary, null)
        
        val barData = BarData(dataSet)
        binding.chartMonthly.data = barData
        binding.chartMonthly.invalidate()
    }
    
    private fun showResetConfirmationDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Reset Statistics")
            .setMessage("Are you sure you want to reset all statistics? This action cannot be undone.")
            .setPositiveButton("Reset") { _, _ ->
                preferenceManager.resetStatistics()
                loadStatistics()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}