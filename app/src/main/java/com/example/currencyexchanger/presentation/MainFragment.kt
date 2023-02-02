package com.example.currencyexchanger.presentation

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.currencyexchanger.R
import com.example.currencyexchanger.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {
    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(layoutInflater)

        binding.btnConvert.setOnClickListener {
            viewModel.convert(
                binding.etFrom.text.toString(),
                binding.spFromCurrency.selectedItem.toString(),
                binding.spToCurrency.selectedItem.toString(),
            )
            Toast.makeText(context, binding.spFromCurrency.selectedItem.toString(), Toast.LENGTH_SHORT).show()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.conversion.collect { event ->
                when (event) {
                    is MainViewModel.CurrencyEvent.Success -> {
                        binding.progressBar.isVisible = false
                        binding.etTo.setTextColor(Color.BLACK)
                        binding.etTo.setText(event.result)
                    }
                    is MainViewModel.CurrencyEvent.Failure -> {
                        binding.progressBar.isVisible = false
                        binding.etTo.setTextColor(Color.RED)
                        binding.etFrom.setText(event.error)
                    }
                    is MainViewModel.CurrencyEvent.Loading -> {
                        binding.progressBar.isVisible = true
                    }
                    else -> Unit
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel
    }
}