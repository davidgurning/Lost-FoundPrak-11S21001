package com.ifs21001.lostandfound.presentation.lostfound

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ifs21001.lostandfound.R
import com.ifs21001.lostandfound.data.local.entity.LostFoundEntity
import com.ifs21001.lostandfound.data.model.LostfoundTodo
import com.ifs21001.lostandfound.data.remote.MyResult
import com.ifs21001.lostandfound.data.remote.response.LostFound
import com.ifs21001.lostandfound.databinding.ActivityLostfoundDetailBinding
import com.ifs21001.lostandfound.helper.Utils.Companion.observeOnce
import com.ifs21001.lostandfound.presentation.ViewModelFactory

class LostFoundDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLostfoundDetailBinding
    private val viewModel by viewModels<LostFoundViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var isFavorite: Boolean = false

    private var delcomTodo: LostFoundEntity? = null


    private var isChanged: Boolean = false
    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == LostFoundManageActivity.RESULT_CODE) {
            recreate()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLostfoundDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupAction()
    }
    private fun setupView() {
        showComponent(false)
        showLoading(false)
    }
    private fun setupAction() {
        val lostFoundId = intent.getIntExtra(KEY_LOSTFOUND_ID, 0)
        if (lostFoundId== 0) {
            finish()
            return
        }
        observeGetLostFound(lostFoundId)
        binding.appbarTodoDetail.setNavigationOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra(KEY_IS_CHANGED, isChanged)
            setResult(RESULT_CODE, resultIntent)
            finishAfterTransition()
        }
    }
    private fun observeGetLostFound(lostFoundId: Int) {
        viewModel.getLostFound(lostFoundId).observeOnce { result ->
            when (result) {
                is MyResult.Loading -> {
                    showLoading(true)
                }
                is MyResult.Success -> {
                    showLoading(false)
                    loadLostFound(result.data.data.lostFound)
                }
                is MyResult.Error -> {
                    Toast.makeText(
                        this@LostFoundDetailActivity,
                        result.error,
                        Toast.LENGTH_SHORT
                    ).show()
                    showLoading(false)
                    finishAfterTransition()
                }
            }
        }
    }
    private fun loadLostFound(lostfound: LostFound) {
        showComponent(true)
        binding.apply {
            tvTodoDetailTitle.text = lostfound.title
            tvTodoDetailDate.text = "Dibuat pada: ${lostfound.createdAt}"
            tvTodoDetailDesc.text = lostfound.description
            tvTodoDetailStatus.text = lostfound.status


            viewModel.getLocalTodo(lostfound.id).observeOnce {
                if(it != null){
                    delcomTodo = it
                    setFavorite(true)
                }else{
                    setFavorite(false)
                }
            }
            cbTodoDetailIsFinished.isChecked = lostfound.isCompleted == 1

            cbTodoDetailIsFinished.setOnCheckedChangeListener { _, isChecked ->
                viewModel.putlostFound(
                    lostfound.id,
                    lostfound.title,
                    lostfound.description,
                    lostfound.status,
                    lostfound.isCompleted == 1,
                ).observeOnce {
                    when (it) {
                        is MyResult.Error -> {
                            if (isChecked) {
                                Toast.makeText(
                                    this@LostFoundDetailActivity,
                                    "Gagal menyelesaikan todo: " + lostfound.title,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@LostFoundDetailActivity,
                                    "Gagal batal menyelesaikan todo: " + lostfound.title,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        is MyResult.Success -> {
                            if (isChecked) {
                                Toast.makeText(
                                    this@LostFoundDetailActivity,
                                    "Berhasil menyelesaikan todo: " + lostfound.title,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@LostFoundDetailActivity,
                                    "Berhasil batal menyelesaikan todo: " + lostfound.title,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            if ((lostfound.isCompleted == 1) != isChecked) {
                                isChanged = true
                            }
                        }
                        else -> {}
                    }
                }
            }

            ivTodoDetailActionFavorite.setOnClickListener {
                if(isFavorite){
                    setFavorite(false)
                    if(delcomTodo != null){
                        viewModel.deleteLocalTodo(delcomTodo!!)
                    }

                    Toast.makeText(
                        this@LostFoundDetailActivity,
                        "Lost and found berhasil dihapus dari daftar favorite",
                        Toast.LENGTH_SHORT
                    ).show()
                }else{
                    delcomTodo = LostFoundEntity(
                        id = lostfound.id,
                        title = lostfound.title,
                        status = lostfound.status,
                        description = lostfound.description,
                        user_id = lostfound.userId,
                        isCompleted = lostfound.isCompleted,
                        cover = lostfound.cover,
                        createdAt = lostfound.createdAt,
                        updatedAt = lostfound.updatedAt,
                    )

                    setFavorite(true)
                    viewModel.insertLocalTodo(delcomTodo!!)

                    Toast.makeText(
                        this@LostFoundDetailActivity,
                        "Todo berhasil ditambahkan ke daftar favorite",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            ivTodoDetailActionDelete.setOnClickListener {
                val builder = AlertDialog.Builder(this@LostFoundDetailActivity)
                builder.setTitle("Konfirmasi Hapus Todo")
                    .setMessage("Anda yakin ingin menghapus todo ini?")
                builder.setPositiveButton("Ya") { _, _ ->
                    observeDeletelostFound(lostfound.id)
                }
                builder.setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss() // Menutup dialog
                }
                val dialog = builder.create()
                dialog.show()
            }
            ivTodoDetailActionEdit.setOnClickListener {
                val lostfoundTodo = LostfoundTodo(
                    lostfound.id,
                    lostfound.userId,
                    lostfound.title,
                    lostfound.description,
                    lostfound.status,
                    lostfound.isCompleted == 1,
                    lostfound.cover
                )
                val intent = Intent(
                    this@LostFoundDetailActivity,
                    LostFoundManageActivity::class.java
                )
                intent.putExtra(LostFoundManageActivity.KEY_IS_ADD, false)
                intent.putExtra(LostFoundManageActivity.KEY_LOSTFOUND, lostfoundTodo)
                launcher.launch(intent)
            }
        }
    }

    private fun setFavorite(status: Boolean){
        isFavorite = status
        if(status){
            binding.ivTodoDetailActionFavorite
                .setImageResource(R.drawable.ic_favorite_24)
        }else{
            binding.ivTodoDetailActionFavorite
                .setImageResource(R.drawable.ic_favorite_border_24)
        }
    }

    private fun observeDeletelostFound(lostFoundId: Int) {
        showComponent(false)
        showLoading(true)
        viewModel.deletelostFound(lostFoundId).observeOnce {
            when (it) {
                is MyResult.Error -> {
                    showComponent(true)
                    showLoading(false)
                    Toast.makeText(
                        this@LostFoundDetailActivity,
                        "Gagal menghapus todo: ${it.error}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is MyResult.Success -> {
                    showLoading(false)
                    Toast.makeText(
                        this@LostFoundDetailActivity,
                        "Berhasil menghapus todo",
                        Toast.LENGTH_SHORT
                    ).show()
                    val resultIntent = Intent()
                    resultIntent.putExtra(KEY_IS_CHANGED, true)
                    setResult(RESULT_CODE, resultIntent)
                    finishAfterTransition()
                }
                else -> {}
            }
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.pbTodoDetail.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }
    private fun showComponent(status: Boolean) {
        binding.llTodoDetail.visibility =
            if (status) View.VISIBLE else View.GONE
    }
    companion object {
        const val KEY_LOSTFOUND_ID = "todo_id"
        const val KEY_IS_CHANGED = "is_changed"
        const val RESULT_CODE = 1001
    }
}

