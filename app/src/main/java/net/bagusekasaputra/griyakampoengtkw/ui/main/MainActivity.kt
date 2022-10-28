package net.bagusekasaputra.griyakampoengtkw.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.databinding.ActivityMainBinding
import net.bagusekasaputra.griyakampoengtkw.databinding.DialogNewBlockBinding
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.ui.detail.DetailActivity

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        const val INTENT_KAVLING_KODE = "kavling_kode"
    }

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()

        binding.fabAddKavling.setOnClickListener {
            showNewBlockDialog()
        }
    }

    private fun setupViewModel() {
        viewModel.blocks.observe(this) {
            it?.let {
                setupBlockRecyclerview(it)
            }
        }

        viewModel.kavlings.observe(this) {
            it?.let {
                setupKavlingRecyclerView(it)
            }
        }
    }

    private fun setupBlockRecyclerview(blocks: List<Block>) {
        val adapter = BlockRecyclerAdapter(blocks) { position ->
            viewModel.currentBlock.value = blocks[position].kode
            viewModel.currentBlock.value?.let {
                viewModel.getKavlings(Block(kode = it))
            }
        }

        binding.recyclerBlocks.adapter = adapter
        binding.recyclerBlocks.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setupKavlingRecyclerView(kavlings: List<Kavling>) {
        val adapter = KavlingRecyclerAdapter(kavlings) {
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra(INTENT_KAVLING_KODE, kavlings[it].kode)
            }
            startActivity(intent)
        }
        binding.recyclerKavlings.adapter = adapter
        binding.recyclerKavlings.layoutManager = GridLayoutManager(this, 3)
    }

    private fun showNewBlockDialog() {
        val dialogBinding = DialogNewBlockBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(this).apply {
            setView(dialogBinding.root)
        }.create()

        alertDialog.show()

        dialogBinding.btnTambahkan.setOnClickListener {
            // TODO
            alertDialog.dismiss()
            Snackbar.make(this, binding.root, "Berhasil ditambahkan! (fake)", Snackbar.LENGTH_SHORT).show()
        }

        dialogBinding.btnBatal.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.getAllBlocks()

        viewModel.currentBlock.value?.let {
            val block = Block(kode = it)
            viewModel.getKavlings(block)
        }
    }
}