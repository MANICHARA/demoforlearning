package com.contentblocker.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.contentblocker.app.databinding.FragmentHomeBinding
import com.contentblocker.app.service.ContentBlockingService
import com.contentblocker.app.utils.PreferenceManager
import com.contentblocker.app.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var preferenceManager: PreferenceManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferenceManager = PreferenceManager(requireContext())
        setupUI()
        observeViewModel()
        updateStatistics()
    }
    
    private fun setupUI() {
        // Set up service status
        updateServiceStatus()
        
        // Set up statistics cards
        setupStatisticsCards()
        
        // Set up quick actions
        setupQuickActions()
    }
    
    private fun updateServiceStatus() {
        val isRunning = ContentBlockingService.isRunning
        binding.cardServiceStatus.apply {
            if (isRunning) {
                statusIcon.setImageResource(R.drawable.ic_check_circle)
                statusText.text = getString(R.string.status_active)
                statusText.setTextColor(resources.getColor(R.color.success, null))
                statusDescription.text = getString(R.string.service_running)
            } else {
                statusIcon.setImageResource(R.drawable.ic_cancel)
                statusText.text = getString(R.string.status_inactive)
                statusText.setTextColor(resources.getColor(R.color.error, null))
                statusDescription.text = getString(R.string.service_stopped)
            }
        }
    }
    
    private fun setupStatisticsCards() {
        // Blocked today card
        binding.cardBlockedToday.apply {
            val count = preferenceManager.getBlockedCountToday()
            statValue.text = count.toString()
            statLabel.text = getString(R.string.blocked_today)
        }
        
        // Total blocked card
        binding.cardTotalBlocked.apply {
            val count = preferenceManager.getTotalBlockedCount()
            statValue.text = count.toString()
            statLabel.text = getString(R.string.total_blocked)
        }
        
        // Time saved card
        binding.cardTimeSaved.apply {
            val timeSaved = preferenceManager.getTimeSavedToday()
            statValue.text = preferenceManager.formatTimeSaved(timeSaved)
            statLabel.text = getString(R.string.time_saved)
        }
    }
    
    private fun setupQuickActions() {
        // Toggle service button
        binding.btnToggleService.setOnClickListener {
            if (ContentBlockingService.isRunning) {
                viewModel.stopService()
            } else {
                viewModel.startService()
            }
        }
        
        // Settings button
        binding.btnSettings.setOnClickListener {
            // Navigate to settings
            viewModel.navigateToSettings()
        }
        
        // Statistics button
        binding.btnStatistics.setOnClickListener {
            // Navigate to statistics
            viewModel.navigateToStatistics()
        }
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.serviceStatus.collect { isRunning ->
                updateServiceStatus()
                updateStatistics()
            }
        }
        
        viewLifecycleOwner.lifecycleOwner.lifecycleScope.launch {
            viewModel.blockedCount.collect { count ->
                updateBlockedCount(count)
            }
        }
    }
    
    private fun updateStatistics() {
        setupStatisticsCards()
    }
    
    private fun updateBlockedCount(count: Int) {
        binding.cardBlockedToday.statValue.text = count.toString()
        binding.cardTotalBlocked.statValue.text = preferenceManager.getTotalBlockedCount().toString()
    }
    
    override fun onResume() {
        super.onResume()
        updateServiceStatus()
        updateStatistics()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}