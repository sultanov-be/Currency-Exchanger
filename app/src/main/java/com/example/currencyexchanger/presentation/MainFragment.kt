package com.example.currencyexchanger.presentation

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyexchanger.adapter.CurrencyClickHandler
import com.example.currencyexchanger.adapter.SheetAdapter
import com.example.currencyexchanger.data.model.CurrencyResponse
import com.example.currencyexchanger.databinding.FragmentMainBinding
import com.example.currencyexchanger.databinding.SheetLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment(), CurrencyClickHandler {
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

            currencyFrom.setOnClickListener {
                showDialog()
            }

            lifecycleScopeObserver()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel
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

    private fun showDialog() {
        val binding = SheetLayoutBinding.inflate(LayoutInflater.from(context))
        val dialog = BottomSheetDialog(requireContext())
        val list: List<Pair<String, String>> = listOf(Pair("TEST", "test"))
        val adapter = SheetAdapter(list)

        binding.recycler.adapter = adapter
        adapter.setInterface(this@MainFragment)
        binding.recycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        dialog.setContentView(binding.root)
        dialog.show()

        binding.imageView.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun clickedCategory(currency: String) {
        Toast.makeText(requireContext(), currency, Toast.LENGTH_SHORT).show()
    }
}