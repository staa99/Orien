package com.staa.games.orien.adapters

import android.content.Context
import android.database.DataSetObserver
import android.databinding.ObservableList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SpinnerAdapter
import com.staa.games.orien.BR
import com.staa.games.orien.R
import com.staa.games.orien.databinding.TokenChoicesSpinnerItemBinding


class TokenSpinnerAdapter(private val context: Context, private val items: ObservableList<Int>) :
        AppRecyclerViewAdapter<Int, TokenChoicesSpinnerItemBinding>
        (
                items,
                BR.tokenType,
                R.layout.token_choices_spinner_item
        ), SpinnerAdapter
{
    override fun isEmpty() = items.isEmpty()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View
    {
        var v = convertView
        if (v == null)
        {
            val vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val b = TokenChoicesSpinnerItemBinding.inflate(vi)
            b.tokenType = items[position]
            v = b.root
        }

        return v
    }

    override fun registerDataSetObserver(observer: DataSetObserver?)
    {

    }

    override fun getItem(position: Int) = items[position]!!

    override fun getViewTypeCount() = 1

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View
    {
        var v = convertView
        if (v == null)
        {
            val vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val b = TokenChoicesSpinnerItemBinding.inflate(vi)
            b.tokenType = items[position]
            v = b.root
        }

        return v
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver?)
    {

    }

    override fun getCount() = items.size
}