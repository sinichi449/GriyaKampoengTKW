package net.bagusekasaputra.griyakampoengtkw.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.R
import net.bagusekasaputra.griyakampoengtkw.databinding.*
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.ui.detail.DetailActivity
import net.bagusekasaputra.griyakampoengtkw.util.InputUtil

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

        setSupportActionBar(binding.toolbarMain)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.settings_24px)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupViewModel()

        binding.fabAddKavling.setOnClickListener {
            showAddKavlingDialog()
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

        viewModel.isFinishOperation.observe(this) {
            it?.let { finish ->
                binding.swipeRefreshMain.isRefreshing = !it
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
        val adapter = KavlingRecyclerAdapter(kavlings, {
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra(INTENT_KAVLING_KODE, kavlings[it].kode)
            }
            startActivity(intent)
        }, {
            showActionKavlingDialog(kavlings[it])
        })
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

    private fun showAddKavlingDialog() {
        val dialogBinding = DialogAddKavlingBinding.inflate(layoutInflater)
        val dialogView = AlertDialog.Builder(this).apply {
            setView(dialogBinding.root)
        }.create()

        dialogView.show()

        dialogBinding.btnTambahkan.setOnClickListener {
            val isEmptyEdt = InputUtil.isNullOrEmptyEditTexts(dialogBinding.edtKode)
            if (!isEmptyEdt) {
                val kode = dialogBinding.edtKode.text.toString()
                val block = Block(kode[0].toString())

                viewModel.addNewBlock(block)

                dialogBinding.btnTambahkan.isEnabled = false

                viewModel.isFinishOperation.observe(this) {
                    it?.let {
                        dialogBinding.btnTambahkan.isEnabled = true

                        if (it) {
                            Toast.makeText(this, "Sukses menambahkan kavling", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(this, "Gagal menambahkan kavling", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }

        dialogBinding.btnBatal.setOnClickListener {
            dialogView.dismiss()
        }
    }

    private fun showActionKavlingDialog(kavling: Kavling) {
        val dialogBinding = DialogActionKavlingBinding.inflate(layoutInflater)
        val dialogView = AlertDialog.Builder(this).apply {
            setView(dialogBinding.root)
        }.create()

        dialogView.show()

        val text = "Kavling ${kavling.kode}"
        dialogBinding.tvKavlingKode.text = text
        dialogBinding.btnEdit.setOnClickListener {
            dialogView.dismiss()
            showEditKavlingDialog(kavling)
        }
        dialogBinding.btnBatal.setOnClickListener {
            dialogView.dismiss()
        }
        dialogBinding.btnHapusKavling.setOnClickListener {
            dialogView.dismiss()
            val dialogHapus = AlertDialog.Builder(this).apply {
                setTitle("Hapus Kavling")
                setMessage("Apakah Anda yakin menhapus kavling ${kavling.kode}?")
                setPositiveButton("Yes") { dialog, which ->
                    // TODO: Hapus kavling
                    Toast.makeText(this@MainActivity, "Kavling ${kavling.kode} berhasil dihapus! (fake)", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()

                }
                setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
            }.create()

            dialogHapus.show()
        }
    }

    private fun showEditKavlingDialog(kavling: Kavling) {
        val dialogBinding = DialogEditKavlingBinding.inflate(layoutInflater)
        val dialogView = AlertDialog.Builder(this).apply {
            setView(dialogBinding.root)
        }.create()

        val ukuran = kavling.ukuran.split("x")
        dialogBinding.edtPanjang.setText(ukuran[0])
        dialogBinding.edtLebar.setText(ukuran[1])
        dialogBinding.edtTipeRumah.setText(kavling.type)

        dialogView.show()

        dialogBinding.btnSimpan.setOnClickListener {
            // TODO: Save data
            dialogView.dismiss()
            Toast.makeText(this, "Data tersimpan! (fake)", Toast.LENGTH_SHORT).show()
        }

        dialogBinding.btnBatal.setOnClickListener {
            dialogView.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.getAllBlocks()

//        viewModel.currentBlock.value?.let {
//            val block = Block(kode = it)
//            viewModel.getKavlings(block)
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }
}