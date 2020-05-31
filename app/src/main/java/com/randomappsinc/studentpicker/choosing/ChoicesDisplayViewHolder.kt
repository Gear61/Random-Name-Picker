package com.randomappsinc.studentpicker.choosing

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.randomappsinc.studentpicker.R
import com.randomappsinc.studentpicker.models.ListInfo

class ChoicesDisplayViewHolder(
        view: View,
        private val listInfo: ListInfo
) {

    @BindView(R.id.names_list) lateinit var namesList: RecyclerView

    var chooseNameDialogAdapter: ChooseNameDialogAdapter

    init {
        ButterKnife.bind(this, view)

        chooseNameDialogAdapter = ChooseNameDialogAdapter(ArrayList(), listInfo)
        namesList.adapter = chooseNameDialogAdapter
    }

    fun showChosenNames(chosenNames: MutableList<String>, showAsList: Boolean) {
        chooseNameDialogAdapter.showAsList = showAsList
        chooseNameDialogAdapter.chosenNames = chosenNames
    }
}