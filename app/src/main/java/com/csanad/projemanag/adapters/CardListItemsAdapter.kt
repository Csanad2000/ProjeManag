package com.csanad.projemanag.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csanad.projemanag.R
import com.csanad.projemanag.activities.TaskListActivity
import com.csanad.projemanag.models.Card
import com.csanad.projemanag.models.SelectedMembers
import kotlinx.android.synthetic.main.item_card.view.*

open class CardListItemsAdapter (private val context: Context,private var list: ArrayList<Card>):RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var onClickListener: OnClickListener?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return myViewHolder(LayoutInflater.from(context).inflate(R.layout.item_card,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model =list[position]
        if (holder is myViewHolder){
            if (model.labelColor.isNotEmpty()){
                holder.itemView.view_label_color.visibility=View.VISIBLE
                holder.itemView.view_label_color.setBackgroundColor(Color.parseColor(model.labelColor))
            }else{
                holder.itemView.view_label_color.visibility=View.GONE
            }
            holder.itemView.tvCardName.text=model.name
            if ((context as TaskListActivity).mAssignedMembersDetailList.size>0){
                val selectedMembersList:ArrayList<SelectedMembers> =ArrayList()
                for (i in context.mAssignedMembersDetailList.indices){
                    for (j in model.assignedTo){
                        if (context.mAssignedMembersDetailList[i].id==j){
                            val selectedMember=SelectedMembers(context.mAssignedMembersDetailList[i].id,context.mAssignedMembersDetailList[i].image)
                            selectedMembersList.add(selectedMember)
                        }
                    }
                }
                if (selectedMembersList.size>0){
                    if (selectedMembersList.size==1&&selectedMembersList[0].id==model.createdBy){
                        holder.itemView.rv_card_selected_members_list.visibility=View.GONE
                    }else{
                        holder.itemView.rv_card_selected_members_list.visibility=View.VISIBLE
                        holder.itemView.rv_card_selected_members_list.layoutManager=GridLayoutManager(context,4)
                        val adapter=CardMemberListItemsAdapter(context,selectedMembersList,false)
                        holder.itemView.rv_card_selected_members_list.adapter=adapter
                        adapter.setOnClickListener(object: CardMemberListItemsAdapter.OnClickListener{
                            override fun onClick(){
                                if (onClickListener!=null){
                                    onClickListener!!.onClick(position)
                                }
                            }
                        })
                    }
                }else {
                    holder.itemView.rv_card_selected_members_list.visibility = View.GONE
                }
            }
            holder.itemView.setOnClickListener{
                if (onClickListener != null) {
                    onClickListener!!.onClick(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClickListener {
        fun onClick(position: Int)
    }

    fun setOnClickListener(onClickListener:OnClickListener){
        this.onClickListener=onClickListener
    }



    class myViewHolder(view: View):RecyclerView.ViewHolder(view)
}