package net.bagusekasaputra.griyakampoengtkw.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.R
import net.bagusekasaputra.griyakampoengtkw.databinding.*
import net.bagusekasaputra.griyakampoengtkw.domain.entity.AppUpdate
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.ui.detail.DetailActivity
import net.bagusekasaputra.griyakampoengtkw.ui.main.adapter.BlockRecyclerAdapter
import net.bagusekasaputra.griyakampoengtkw.ui.main.adapter.KavlingRecyclerAdapter
import net.bagusekasaputra.griyakampoengtkw.util.DialogUtil
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

        // Setup toolbar
        setSupportActionBar(binding.toolbarMain)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.settings_24px)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Check connectivity
        val deviceOnline = intent.getBooleanExtra(GriyaNodes.INTENT_IS_ONLINE, false)

        if (!deviceOnline) {
            Toast.makeText(this, "Device terdeteksi offline, data tidak akan tersinkronisasi!", Toast.LENGTH_LONG).show()
        }

        // Syncing data, if offline it will pull from local database
        syncData()

        viewModel.checkUpdates({
            showUpdateDialog(it)
        }, { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        })

        setupViewModel()

        setupFloatingButtons()

        binding.fabAddKavling.setOnClickListener {
            showAddKavlingDialog()
        }

        binding.fabAddBlock.setOnClickListener {
            showAddBlockDialog()
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
            getKavlings(it)
        }
    }

    private fun setupFloatingButtons() {
        binding.fabAddKavling.visibility = View.GONE
        binding.fabAddBlock.visibility = View.GONE

        binding.fabActions.shrink()

        var isAllFabsVisible = false

        binding.fabActions.setOnClickListener {
            if (isAllFabsVisible) {
                binding.fabActions.shrink()

                binding.fabAddKavling.hide()
                binding.fabAddBlock.hide()

                isAllFabsVisible = false
            } else {
                binding.fabActions.extend()

                binding.fabAddKavling.show()
                binding.fabAddBlock.show()

                isAllFabsVisible = true
            }
        }
    }

    private fun setupBlockRecyclerview(blocks: List<Block>) {
        val adapter = BlockRecyclerAdapter(blocks) { position ->
            viewModel.currentBlock.value = blocks[position].kode
            viewModel.currentBlock.value?.let {
                getKavlings(it)
            }
        }

        binding.recyclerBlocks.adapter = adapter
        binding.recyclerBlocks.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setupKavlingRecyclerView(kavlings: List<Kavling>) {
        val adapter = KavlingRecyclerAdapter(this, kavlings, {
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

    private fun showAddBlockDialog() {
        val dialogBinding = DialogAddBlockBinding.inflate(layoutInflater)
        val dialogView = AlertDialog.Builder(this).apply {
            setView(dialogBinding.root)
        }.create()

        DialogUtil.additionalDialogSetting(this, dialogView)

        dialogView.show()

        dialogBinding.btnPilihWarna.setOnClickListener {
            MaterialColorPickerDialog.Builder(this)
                .setTitle("Pilih Warna")
                .setColorShape(ColorShape.CIRCLE)
                .setColorSwatch(ColorSwatch._500)
                .setDefaultColor(R.color.abang)
                .setColorListener { color, colorHex ->
                    val removeHash = colorHex.substring(1)
                    dialogBinding.edtWarna.setText(removeHash)
                }
                .show()
        }

        dialogBinding.btnTambahkan.setOnClickListener {
            dialogBinding.btnTambahkan.isEnabled = false
            dialogBinding.btnTambahkan.text = "Menyimpan data ..."

            val isInvalidEdt = InputUtil.isNullOrEmptyEditTexts(
                dialogBinding.edtKode, dialogBinding.edtWarna
            )

            if (!isInvalidEdt) {
                val kode = dialogBinding.edtKode.text.toString()
                val warna = "#${dialogBinding.edtWarna.text.toString()}"

                val block = Block(kode, warna)

                viewModel.addNewBlock(block)

                viewModel.operationResult.observe(this) {
                    it?.let {
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
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

    private fun showAddKavlingDialog() {
        val dialogBinding = DialogAddKavlingBinding.inflate(layoutInflater)
        val dialogView = AlertDialog.Builder(this).apply {
            setView(dialogBinding.root)
        }.create()

        DialogUtil.additionalDialogSetting(this, dialogView)

        val blockLists = ArrayList<String>()
        viewModel.blocks.value?.forEach {
            blockLists.add(it.kode)
        }
        val spinnerAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, blockLists)
        dialogBinding.spinnerBlocks.adapter = spinnerAdapter

        dialogView.show()

        dialogBinding.btnTambahkan.setOnClickListener {
            val isInValidEdt = InputUtil.isNullOrEmptyEditTexts(
                dialogBinding.edtNoKavling, dialogBinding.edtPanjang, dialogBinding.edtLebar, dialogBinding.edtTipeRumah)

            if (!isInValidEdt) {
                val spinnerPosition = dialogBinding.spinnerBlocks.selectedItemPosition
                val kode = blockLists[spinnerPosition]
                val warna = viewModel.blocks.value!![spinnerPosition].warna
                val noKavling = dialogBinding.edtNoKavling.text.toString()
                val panjang = dialogBinding.edtPanjang.text.toString()
                val lebar = dialogBinding.edtLebar.text.toString()
                val ukuran = panjang + "x" + lebar
                val type = dialogBinding.edtTipeRumah.text.toString()

                val kavling = Kavling(kode + noKavling, true, warna, ukuran, type)

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

        DialogUtil.additionalDialogSetting(this, dialogView)

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

            val alertDialogHapus = MaterialAlertDialogBuilder(this).apply {
                setTitle("Hapus Kavling")
                setMessage("Apakah Anda yakin menghapus kavling ${kavling.kode}?")
                setPositiveButton("Ya") { dialog, _ ->
                    val blockKode = viewModel.currentBlock.value!!
                    val kavlingKode = kavling.kode

                    viewModel.removeKavling(blockKode, kavlingKode)

                    viewModel.operationResult.observe(this@MainActivity) {
                        it?.let { operation ->
                            Toast.makeText(this@MainActivity, operation.message?: "Null", Toast.LENGTH_SHORT).show()
                            syncData()
                            dialog.dismiss()
                        }
                    }
                }
                setNegativeButton("Tidak") { dialog, _ -> dialog.dismiss() }
            }.create()

            alertDialogHapus.show()
        }
    }

    private fun showEditKavlingDialog(kavling: Kavling) {
        val dialogBinding = DialogEditKavlingBinding.inflate(layoutInflater)
        val dialogView = AlertDialog.Builder(this).apply {
            setView(dialogBinding.root)
        }.create()

        DialogUtil.additionalDialogSetting(this, dialogView)

        dialogView.show()

        val oldUkuran = kavling.ukuran.split("x")
        dialogBinding.edtPanjang.setText(oldUkuran[0])
        dialogBinding.edtLebar.setText(oldUkuran[1])
        dialogBinding.edtTipeRumah.setText(kavling.type)

        dialogBinding.btnSimpan.setOnClickListener {
            dialogBinding.btnSimpan.isEnabled = false
            dialogBinding.btnSimpan.text = "Menyimpan data ..."

            val isInvalidEdt = InputUtil.isNullOrEmptyEditTexts(
                dialogBinding.edtPanjang, dialogBinding.edtLebar, dialogBinding.edtTipeRumah)

            if (!isInvalidEdt) {
                val panjang = dialogBinding.edtPanjang.text.toString()
                val lebar = dialogBinding.edtLebar.text.toString()
                val newUkuran = panjang + "x" + lebar
                val tipeRumah = dialogBinding.edtTipeRumah.text.toString()
                val newKavling = Kavling(kavling.kode, kavling.isActive, kavling.warna, newUkuran, tipeRumah)
                val blockCode = viewModel.currentBlock.value!!

                viewModel.editKavling(blockCode, kavling, newKavling)
                viewModel.operationResult.observe(this) {
                    it?.let {
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
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

    private fun showUpdateDialog(appUpdate: AppUpdate) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Update Tersedia!")
            .setMessage(
                appUpdate.releaseNotes.let { notes ->
                    val result = StringBuilder()

                    notes.forEach { result.append("- ").append(it).append("\n") }

                    return@let result.toString()
                }
            )
            .setPositiveButton("Update") { dialog, _ ->
                openBrowser(Uri.parse(appUpdate.url))
            }
            .create()
            .show()
    }

    private fun openBrowser(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = uri
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun getKavlings(blockKode: String) {
        viewModel.getKavlings(blockKode) { failMsg ->
            Snackbar.make(binding.root, failMsg, Snackbar.LENGTH_SHORT).show()
        }
    }
}