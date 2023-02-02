package com.example.currencyexchanger.presentation

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.currencyexchanger.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {
    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater)

        with(binding) {

            mainLayout.visibility = INVISIBLE
            inputTextFrom.setText("1")
            viewModel.convert("1", "USD", "RUB")

            convertBtn.setOnClickListener {
                viewModel.convert(
                    inputTextFrom.text.toString(),
                    currencyFrom.text.toString(),
                    currencyTo.text.toString()
                    )
            }

            lifecycleScopeObserver()
        }
        return binding.root
    }

    private fun lifecycleScopeObserver() = with(binding) {
        lifecycleScope.launchWhenStarted {
            viewModel.conversion.collect { event ->
                when (event) {
                    is MainViewModel.CurrencyEvent.Success -> {
                        loadingBar(false)
                        progressBar.isVisible = false
                        inputTextTo.setTextColor(Color.BLACK)
                        inputTextTo.setText(event.result)
                    }
                    is MainViewModel.CurrencyEvent.Failure -> {
                        binding.progressBar.isVisible = false
                        inputTextTo.setTextColor(Color.RED)
                        inputTextTo.setText(event.error)
                    }
                    is MainViewModel.CurrencyEvent.Loading -> {
                        loadingBar(true)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun loadingBar(isLoading: Boolean) = with(binding) {
        if (isLoading) {
            progressBar.visibility = VISIBLE
            mainLayout.visibility = INVISIBLE
        } else {
            mainLayout.visibility = VISIBLE
            progressBar.visibility = INVISIBLE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel
    }
}