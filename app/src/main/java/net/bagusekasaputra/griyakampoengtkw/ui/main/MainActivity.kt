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
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.R
import net.bagusekasaputra.griyakampoengtkw.databinding.ActivityMainBinding
import net.bagusekasaputra.griyakampoengtkw.databinding.DialogActionKavlingBinding
import net.bagusekasaputra.griyakampoengtkw.databinding.DialogAddKavlingBinding
import net.bagusekasaputra.griyakampoengtkw.databinding.DialogEditKavlingBinding
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.ui.detail.DetailActivity
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes
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

        val deviceOnline = intent.getBooleanExtra(GriyaNodes.INTENT_IS_ONLINE, false)
        if (!deviceOnline) {
            Toast.makeText(this, "Device terdeteksi offline, data tidak akan tersinkronisasi!", Toast.LENGTH_LONG).show()
        } else {
            syncData()
        }

        setupViewModel()

        binding.fabAddKavling.setOnClickListener {
            showAddKavlingDialog()
        }

        binding.swipeRefreshMain.setOnRefreshListener {
            syncData()
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
                binding.swipeRefreshMain.isRefreshing = !finish
            }
        }
    }

    private fun syncData() {
        viewModel.getAllBlocks()

        viewModel.currentBlock.value?.let {
            viewModel.getKavlings(it)
        }
    }

    private fun setupBlockRecyclerview(blocks: List<Block>) {
        val adapter = BlockRecyclerAdapter(blocks) { position ->
            viewModel.currentBlock.value = blocks[position].kode
            viewModel.currentBlock.value?.let {
                viewModel.getKavlings(it)
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

    private fun showAddKavlingDialog() {
        val dialogBinding = DialogAddKavlingBinding.inflate(layoutInflater)
        val dialogView = AlertDialog.Builder(this).apply {
            setView(dialogBinding.root)
        }.create()

        dialogView.show()

        dialogBinding.btnTambahkan.setOnClickListener {
            val isInValidEdt = InputUtil.isNullOrEmptyEditTexts(
                dialogBinding.edtBlock, dialogBinding.edtNoKavling, dialogBinding.edtWarna,
                dialogBinding.edtPanjang, dialogBinding.edtLebar, dialogBinding.edtTipeRumah
            ) && isValidHexWarna(dialogBinding.edtWarna)

            if (!isInValidEdt) {
                val kode = dialogBinding.edtBlock.text.toString()
                val noKavling = dialogBinding.edtNoKavling.text.toString()
                val warna = dialogBinding.edtWarna.text.toString()
                val panjang = dialogBinding.edtPanjang.text.toString()
                val lebar = dialogBinding.edtLebar.text.toString()
                val ukuran = panjang + "x" + lebar
                val type = dialogBinding.edtTipeRumah.text.toString()

                val block = Block(kode, warna)
                val kavling = Kavling(kode + noKavling, true, warna, ukuran, type)

                viewModel.addNewBlock(block)
                viewModel.addKavling(kode, kavling)

                dialogBinding.btnTambahkan.isEnabled = false
                dialogBinding.btnTambahkan.text = "Menyimpan data ..."

                viewModel.operationResult.observe(this) {
                    it?.let {
                        Snackbar.make(this, binding.root, it.message?: "Hasil tak diketahui", Snackbar.LENGTH_SHORT).show()
                        syncData()
                        dialogView.dismiss()
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

    private fun isValidHexWarna(edtWarna: TextInputEditText): Boolean {
        val firstChar = edtWarna.text.toString()[0]
        return if (firstChar == '#') {
            true
        } else {
            edtWarna.error = "Warna tidak valid!"
            false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }
}