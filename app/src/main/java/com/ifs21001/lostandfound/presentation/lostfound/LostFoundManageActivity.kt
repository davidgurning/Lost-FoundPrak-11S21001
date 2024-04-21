package com.ifs21001.lostandfound.presentation.lostfound

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ifs21001.lostandfound.data.model.LostfoundTodo
import com.ifs21001.lostandfound.data.remote.MyResult
import com.ifs21001.lostandfound.databinding.ActivityTodoManageBinding
import com.ifs21001.lostandfound.helper.Utils.Companion.observeOnce
import com.ifs21001.lostandfound.presentation.ViewModelFactory

class LostFoundManageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTodoManageBinding
    private val viewModel by viewModels<LostFoundViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoManageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupAtion()
    }
    private fun setupView() {
        showLoading(false)
    }
    private fun setupAtion() {
        val isAddTodo = intent.getBooleanExtra(KEY_IS_ADD, true)
        if (isAddTodo) {
            manageAddTodo()
        } else {
            val delcomTodo = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    intent.getParcelableExtra(KEY_LOSTFOUND, LostfoundTodo::class.java)
                }
                else -> {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra<LostfoundTodo>(KEY_LOSTFOUND)
                }
            }
            if (delcomTodo == null) {
                finishAfterTransition()
                return
            }
            manageEditTodo(delcomTodo)
        }
        binding.appbarTodoManage.setNavigationOnClickListener {
            finishAfterTransition()
        }
    }
    private fun manageAddTodo() {
        binding.apply {
            appbarTodoManage.title = "Tambah Todo"
            btnTodoManageSave.setOnClickListener {
                val title = etTodoManageTitle.text.toString()
                val description = etTodoManageDesc.text.toString()
                val status = etTodoManagestatus.text.toString()

                if (title.isEmpty() || description.isEmpty() || status.isEmpty()) {
                    AlertDialog.Builder(this@LostFoundManageActivity).apply {
                        setTitle("Oh No!")
                        setMessage("Tidak boleh ada data yang kosong!")
                        setPositiveButton("Oke") { _, _ -> }
                        create()
                        show()
                    }
                    return@setOnClickListener
                }
                observePostTodo(title, description, status)
            }
        }
    }
    private fun observePostTodo(title: String, description: String, status: String) {
        viewModel.postlostFound(title, description, status).observeOnce { result ->
            when (result) {
                is MyResult.Loading -> {
                    showLoading(true)
                }
                is MyResult.Success -> {
                    showLoading(false)
                    val resultIntent = Intent()
                    setResult(RESULT_CODE, resultIntent)
                    finishAfterTransition()
                }
                is MyResult.Error -> {
                    AlertDialog.Builder(this@LostFoundManageActivity).apply {
                        setTitle("Oh No!")
                        setMessage(result.error)
                        setPositiveButton("Oke") { _, _ -> }
                        create()
                        show()
                    }
                    showLoading(false)
                }
            }
        }
    }
    private fun manageEditTodo(todo: LostfoundTodo) {
        binding.apply {
            appbarTodoManage.title = "Ubah Todo"
            etTodoManageTitle.setText(todo.title)
            etTodoManageDesc.setText(todo.description)
            etTodoManagestatus.setText(todo.status)


            btnTodoManageSave.setOnClickListener {
                val title = etTodoManageTitle.text.toString()
                val description = etTodoManageDesc.text.toString()
                val status = etTodoManagestatus.text.toString()

                if (title.isEmpty() || description.isEmpty() || status.isEmpty()) {
                    AlertDialog.Builder(this@LostFoundManageActivity).apply {
                        setTitle("Oh No!")
                        setMessage("Tidak boleh ada data yang kosong!")
                        setPositiveButton("Oke") { _, _ -> }
                        create()
                        show()
                    }
                    return@setOnClickListener
                }
                observePutTodo(todo.id, title, description, status, todo.isCompleted)
            }
        }
    }
    private fun observePutTodo(
        todoId: Int,
        title: String,
        description: String,
        status: String,
        isCompleted: Boolean,
    ) {
        viewModel.putlostFound(
            todoId,
            title,
            description,
            status,
            isCompleted
        ).observeOnce { result ->
            when (result) {
                is MyResult.Loading -> {
                    showLoading(true)
                }
                is MyResult.Success -> {
                    showLoading(false)
                    val resultIntent = Intent()
                    setResult(RESULT_CODE, resultIntent)
                    finishAfterTransition()
                }
                is MyResult.Error -> {
                    AlertDialog.Builder(this@LostFoundManageActivity).apply {
                        setTitle("Oh No!")
                        setMessage(result.error)
                        setPositiveButton("Oke") { _, _ -> }
                        create()
                        show()
                    }
                    showLoading(false)
                }
            }
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.pbTodoManage.visibility =
            if (isLoading) View.VISIBLE else View.GONE

        binding.btnTodoManageSave.isActivated = !isLoading

        binding.btnTodoManageSave.text =
            if (isLoading) "" else "Simpan"
    }
    companion object {
        const val KEY_IS_ADD = "is_add"
        const val KEY_LOSTFOUND = "todo"
        const val RESULT_CODE = 1002
    }
}