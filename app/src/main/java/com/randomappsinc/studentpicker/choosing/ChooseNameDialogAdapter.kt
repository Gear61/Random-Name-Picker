package com.randomappsinc.studentpicker.choosing

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.randomappsinc.studentpicker.R
import com.randomappsinc.studentpicker.extentions.maybeVisible
import com.randomappsinc.studentpicker.models.ListInfo
import com.randomappsinc.studentpicker.models.NameDO
import com.randomappsinc.studentpicker.utils.NameUtils
import com.squareup.picasso.Picasso

class ChooseNameDialogAdapter(
        chosenNames: MutableList<String>,
        private val currentState: ListInfo
) : RecyclerView.Adapter<ChooseNameDialogAdapter.ViewHolder>() {

    var chosenNames: MutableList<String> = chosenNames
        set(chosenNames) {
            field.clear()
            field.addAll(chosenNames)
            notifyDataSetChanged()
        }

    var showAsList = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.choose_name_dialog_cell, parent, false))
    }

    override fun getItemCount(): Int {
        return chosenNames.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.loadName(position)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        @BindView(R.id.person_number) lateinit var nameNumber: TextView
        @BindView(R.id.person_image) lateinit var personImageView: ImageView
        @BindView(R.id.person_name) lateinit var name: TextView

        init {
            ButterKnife.bind(this, view)
        }

        @SuppressLint("SetTextI18n")
        fun loadName(position: Int) {
            val nameDO: NameDO = currentState.getNameDO(chosenNames[position])
            nameNumber.maybeVisible(showAsList)
            if (showAsList) {
                nameNumber.text = NameUtils.getPrefix(position)
                name.text = NameUtils.getDisplayTextForName(nameDO)
            } else {
                name.text = NameUtils.getDisplayTextForName(nameDO)
            }
            val photoUri = nameDO.photoUri
            val nameHasPhoto = !TextUtils.isEmpty(photoUri)
            personImageView.visibility = if (nameHasPhoto) View.VISIBLE else View.GONE
            if (nameHasPhoto) {
                Picasso.get()
                        .load(photoUri)
                        .fit()
                        .centerCrop()
                        .into(personImageView)
            }
        }
    }
}