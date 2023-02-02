package com.example.currencyexchanger.presentation

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyexchanger.R
import com.example.currencyexchanger.adapter.CurrencyClickHandler
import com.example.currencyexchanger.adapter.SheetAdapter
import com.example.currencyexchanger.databinding.FragmentMainBinding
import com.example.currencyexchanger.databinding.SheetLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment(), CurrencyClickHandler {
    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: FragmentMainBinding
    private var isFrom = false
    private var isTo= false
    lateinit var listCode: Array<String>
    lateinit var listName: Array<String>
    lateinit var dialog: BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater)

        listCode = resources.getStringArray(R.array.currency_codes)
        listName = resources.getStringArray(R.array.currency_names)

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
                showDialog(listCode, listName)
                isFrom = true
                isTo = false
            }

            currencyTo.setOnClickListener {
                showDialog(listCode, listName)
                isFrom = false
                isTo = true
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

    private fun showDialog(listCode: Array<String>, listName: Array<String>) {
        val binding = SheetLayoutBinding.inflate(LayoutInflater.from(context))
        var list: MutableList<Pair<String, String>> = mutableListOf()
        dialog = BottomSheetDialog(requireContext())

        for (i in listCode.indices) {
            list.add(Pair(listCode[i], listName[i]))
        }

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

    override fun clickedCategory(currency: String) = with(binding){
        if (isFrom && !isTo) {
            currencyFrom.text = currency
        }
        if (isTo && !isFrom) {
            currencyTo.text = currency
        }
        dialog.dismiss()
    }
}