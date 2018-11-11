package com.staa.games.orien.adapters

import android.databinding.DataBindingUtil
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import android.databinding.ViewDataBinding
import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Spinner
import com.staa.games.orien.BR
import com.staa.games.orien.R
import com.staa.games.orien.databinding.LocalGroupListItemBinding
import com.staa.games.orien.engine.game.hor
import com.staa.games.orien.engine.game.lft
import com.staa.games.orien.engine.game.players.Player
import com.staa.games.orien.engine.game.rgt
import com.staa.games.orien.engine.game.ver
import com.staa.games.orien.listeners.TokenChoiceSpinnerItemSelected
import com.staa.games.orien.viewholders.AppViewHolder

abstract class AppRecyclerViewAdapter<ItemType : Any, BindingType : ViewDataBinding>
(private val items: ObservableList<ItemType>, @IdRes private val variableId: Int, private val layoutId: Int)
    : Adapter<AppViewHolder<BindingType>>()
{
    companion object
    {
        fun localGroupRecyclerViewAdapter(items: ObservableList<Player>)
                : AppRecyclerViewAdapter<Player, LocalGroupListItemBinding> = object :
                AppRecyclerViewAdapter<Player, LocalGroupListItemBinding>
                (items,
                 BR.localGroupListItemPlayer,
                 R.layout.local_group_list_item)
        {
            private val tokenChoices = ObservableArrayList<Int>()
            private var s1: Spinner? = null

            init
            {
                tokenChoices.add(hor)
                tokenChoices.add(ver)
                tokenChoices.add(rgt)
                tokenChoices.add(lft)


                items.addOnListChangedCallback(object :
                                                       ObservableList.OnListChangedCallback<ObservableList<Player>>()
                                               {
                                                   override fun onChanged(sender: ObservableList<Player>?)
                                                   {
                                                       s1?.setSelection(
                                                               s1?.selectedItemPosition ?: 0)
                                                   }

                                                   override fun onItemRangeRemoved(sender: ObservableList<Player>?, positionStart: Int, itemCount: Int)
                                                   {
                                                       s1?.setSelection(
                                                               s1?.selectedItemPosition ?: 0)
                                                   }

                                                   override fun onItemRangeMoved(sender: ObservableList<Player>?, fromPosition: Int, toPosition: Int, itemCount: Int)
                                                   {
                                                       s1?.setSelection(
                                                               s1?.selectedItemPosition ?: 0)
                                                   }

                                                   override fun onItemRangeInserted(sender: ObservableList<Player>?, positionStart: Int, itemCount: Int)
                                                   {
                                                       s1?.setSelection(
                                                               s1?.selectedItemPosition ?: 0)
                                                   }

                                                   override fun onItemRangeChanged(sender: ObservableList<Player>?, positionStart: Int, itemCount: Int)
                                                   {
                                                       s1?.setSelection(
                                                               s1?.selectedItemPosition ?: 0)
                                                   }
                                               })
            }


            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder<LocalGroupListItemBinding>
            {
                val binding = getBinding(parent, viewType)
                binding.tokenChoiceSpinner.adapter = TokenSpinnerAdapter(parent.context,
                                                                         tokenChoices)

                if (s1 == null)
                    s1 = binding.tokenChoiceSpinner

                return AppViewHolder(binding)
            }

            override fun onBindViewHolder(holder: AppViewHolder<LocalGroupListItemBinding>, position: Int)
            {
                super.onBindViewHolder(holder, position)
                holder.bind(BR.localGroupListItemPlayerIndex, position)

                val binding = holder.binding
                binding.tokenChoiceSpinner.onItemSelectedListener = TokenChoiceSpinnerItemSelected(
                        binding.tokenChoiceSpinner, position, items[position],
                        tokenChoices)
            }
        }
    }

    override fun getItemCount() = items.size
    override fun getItemViewType(position: Int) = layoutId


    protected fun getBinding(parent: ViewGroup, viewType: Int): BindingType
    {
        val inflater = LayoutInflater.from(parent.context)
        return DataBindingUtil.inflate(inflater, viewType, parent, false)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder<BindingType>
    {
        return AppViewHolder(getBinding(parent, viewType))
    }


    override fun onBindViewHolder(holder: AppViewHolder<BindingType>, position: Int)
    {
        holder.bind(variableId, items[position])
    }
}