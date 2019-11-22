package com.galou.watchmyback.addModifyCheckList

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.galou.watchmyback.R

class AddModifyCheckListActivity : AppCompatActivity() {

    private val viewModel: AddModifyCheckListViewModel by viewModel()
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_modify_check_list)
    }
}
