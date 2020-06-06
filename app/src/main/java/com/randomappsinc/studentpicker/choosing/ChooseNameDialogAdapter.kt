package com.randomappsinc.studentpicker.choosing

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.randomappsinc.studentpicker.databinding.ChooseNameDialogCellBinding
import com.randomappsinc.studentpicker.models.NameDO
import com.randomappsinc.studentpicker.utils.NameUtils
import com.squareup.picasso.Picasso

class ChooseNameDialogAdapter : RecyclerView.Adapter<ChooseNameDialogAdapter.ViewHolder>() {

    var chosenNames = ArrayList<NameDO>()
    var showAsList = false

    fun setChosenNames(chosenNames: List<NameDO>, showAsList: Boolean) {
        this.showAsList = showAsList
        this.chosenNames.clear()
        this.chosenNames.addAll(chosenNames)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                ChooseNameDialogCellBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return chosenNames.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.loadName(position)
    }

    inner class ViewHolder(itemBinding: ChooseNameDialogCellBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        private val nameNumber: TextView = itemBinding.personNumber
        private val personImageView: ImageView = itemBinding.personImage
        private val name: TextView = itemBinding.personName

        fun loadName(position: Int) {
            val nameDO: NameDO = chosenNames[position]
            if (showAsList) {
                nameNumber.visibility = View.VISIBLE
                nameNumber.text = NameUtils.getPrefix(position)
            } else {
                nameNumber.visibility = View.GONE
                nameNumber.text = ""
            }
            name.text = nameDO.name
            val photoUri = nameDO.photoUri
            val nameHasPhoto = !TextUtils.isEmpty(photoUri)
            if (nameHasPhoto) {
                Picasso.get()
                        .load(photoUri)
                        .fit()
                        .centerCrop()
                        .into(personImageView)
            }
            personImageView.visibility = if (nameHasPhoto) View.VISIBLE else View.GONE
        }
    }
}
