package com.example.currencyexchanger.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchanger.databinding.ListItemLayoutBinding

class SheetAdapter internal constructor(
    var currencyRateList: List<Pair<String, String>>) :
    RecyclerView.Adapter<SheetAdapter.ViewHolder>()
{
    lateinit var cellClicked: CurrencyClickHandler

    class ViewHolder(val binding: ListItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currency = currencyRateList[position]

        with(holder.binding){
            currencyItem.text = currency.first
            currencyName.text = currency.second

            holder.itemView.setOnClickListener {
                cellClicked.clickedCategory(currencyItem.text.toString())
            }
        }
    }

    override fun getItemCount(): Int {
        return currencyRateList.size
    }

    fun setInterface(cellClicked: CurrencyClickHandler) {
        this.cellClicked = cellClicked
    }
}