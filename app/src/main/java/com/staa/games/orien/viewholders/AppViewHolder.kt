package com.staa.games.orien.viewholders

import android.databinding.ViewDataBinding
import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView.ViewHolder


class AppViewHolder<BindingType : ViewDataBinding>(val binding: BindingType) :
        ViewHolder(binding.root)
{
    fun bind(@IdRes variableId: Int, item: Any)
    {
        binding.setVariable(variableId, item)
        binding.executePendingBindings()
    }
}