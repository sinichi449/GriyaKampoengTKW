package net.bagusekasaputra.griyakampoengtkw.presentation.main

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
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.*
import net.bagusekasaputra.griyakampoengtkw.presentation.detail.DetailActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.main.adapter.BlockRecyclerAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.main.adapter.KavlingRecyclerAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DialogUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
import net.bagusekasaputra.griyakampoengtkw.presentation.util.InputUtil

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        const val INTENT_KAVLING_KODE = "kavling_kode"
    }

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
//    private lateinit var connectivityAnimation: ConnectivityAnimation

    private var isAllFabsVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        setSupportActionBar(binding.toolbarMain)

        // Check connectivity
//        connectivityAnimation = ConnectivityAnimation(this, binding.root, binding.connectivityStatus)

        val deviceOnline = intent.getBooleanExtra(GriyaNodes.INTENT_IS_ONLINE, false)

        if (!deviceOnline) {
            Toast.makeText(this, "Device terdeteksi offline, data tidak akan tersinkronisasi!", Toast.LENGTH_LONG).show()
//            connectivityAnimation.onOfflineAnimation()
//            onOfflineState()
        }

        // Getting BuildConfig from Splash Activity, and check available update.
        val appVersionName = intent.getStringExtra("versionName") ?: ""
        val appVersionCode = intent.getIntExtra("versionCode", 0)
        if ((appVersionName != "") and (appVersionCode != 0)) {
            viewModel.checkUpdates(
                versionName = appVersionName,
                versionCode = appVersionCode,
                onAvailable = {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Update Tersedia!")
                        .setMessage(
                            it.releaseNotes.let { notes ->
                                val result = StringBuilder()

                                notes.forEach { text ->
                                    result.append("- ")
                                        .append(text)
                                        .append("\n")
                                }

                                return@let result.toString()
                            }
                        )
                        .setPositiveButton("Update") { _, _ ->
                            openBrowser(Uri.parse(it.url))
                        }
                        .create()
                        .show()
                },
                onFailure = {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                }
            )
        }


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

//        setupInternetMonitoring()

    }

    override fun onResume() {
        super.onResume()

        syncData()
    }

//    private fun setupInternetMonitoring() {
//        val networkStatusHelper = NetworkStatusHelper(this)
//
//        networkStatusHelper.observe(this) { status ->
//            status?.let {
//                if (it == NetworkStatus.Available) {
//                    connectivityAnimation.onOnlineAnimation()
//                    onOnlineState()
//                } else if (it == NetworkStatus.Unavailable) {
//                    connectivityAnimation.onOfflineAnimation()
//                    onOfflineState()
//                }
//            }
//        }
//    }

    private fun setupViewModel() {
        viewModel.blocksLive.observe(this) {
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
        getBlocks()

        viewModel.currentBlock.value?.let {
            getKavlings(it)
        }
    }

    private fun setupFloatingButtons() {
        binding.fabAddKavling.visibility = View.GONE
        binding.fabAddBlock.visibility = View.GONE

        binding.fabActions.shrink()

        binding.fabActions.setOnClickListener {
            if (isAllFabsVisible) {
                hideFabs()
            } else {
                showFabs()
            }
        }
    }

    private fun hideFabs() {
        binding.fabActions.shrink()

        binding.fabAddKavling.hide()
        binding.fabAddBlock.hide()

        isAllFabsVisible = false
    }

    private fun showFabs() {
        binding.fabActions.extend()

        binding.fabAddKavling.show()
        binding.fabAddBlock.show()

        isAllFabsVisible = true
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
        val adapter = KavlingRecyclerAdapter(this, kavlings,
            onRecyclerItemClick = {
                val intent = Intent(this, DetailActivity::class.java).apply {
                    putExtra(INTENT_KAVLING_KODE, kavlings[it].kode)
                }
                startActivity(intent)
            },
            onRecyclerItemHold = {
                showActionKavlingDialog(kavlings[it])
            }
        )

        val customAdapter = ScaleInAnimationAdapter(adapter)

        binding.recyclerKavlings.adapter = customAdapter
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
                val warna = dialogBinding.edtWarna.text.toString()

                viewModel.addNewBlock(
                    kode = kode,
                    warna = warna,
                    onComplete = { msg ->
                        syncData()
                        dialogView.dismiss()
                        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT)
                            .show()
                    }
                )
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

        // Setting up spinner which shows a list of available Blocks
        val blockLists = ArrayList<String>()
        viewModel.blocksLive.value?.forEach {
            blockLists.add(it.kode)
        }
        val spinnerAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, blockLists)
        dialogBinding.spinnerBlocks.adapter = spinnerAdapter

        dialogView.show()



        dialogBinding.btnTambahkan.setOnClickListener {
            val isInValidEdt = InputUtil.isNullOrEmptyEditTexts(
                dialogBinding.edtNoKavling, dialogBinding.edtPanjang, dialogBinding.edtLebar, dialogBinding.edtTipeRumah)

            if (!isInValidEdt) {
                dialogBinding.btnTambahkan.isEnabled = false
                dialogBinding.btnTambahkan.text = "Menyimpan data ..."

                // Getting blockKode from spinner
                val spinnerPosition = dialogBinding.spinnerBlocks.selectedItemPosition
                val blockKode = blockLists[spinnerPosition]

                val warna = viewModel.blocksLive.value!![spinnerPosition].warna
                val noKavling = dialogBinding.edtNoKavling.text.toString()
                val panjang = dialogBinding.edtPanjang.text.toString()
                val lebar = dialogBinding.edtLebar.text.toString()
                val type = dialogBinding.edtTipeRumah.text.toString()

                viewModel.addKavling(
                    blockKode = blockKode,
                    noKavling = noKavling, // Beware with this. It is just the number, not the kavlingKode.
                    warna = warna,
                    type = type,
                    panjang = panjang,
                    lebar = lebar
                ) { msg ->
                    syncData()
                    dialogView.dismiss()
                    Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
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

        // Need to be separated like this ...
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

            MaterialAlertDialogBuilder(this).apply {
                setTitle("Hapus Kavling")
                setMessage("Apakah Anda yakin menghapus kavling ${kavling.kode}?")
                setPositiveButton("Ya") { dialog, _ ->
                    val blockKode = viewModel.currentBlock.value!!

                    viewModel.removeKavling(
                        blockKode = blockKode,
                        kavlingKode = kavling.kode,
                        onComplete = { msg ->
                            syncData()
                            dialog.dismiss()
                            Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                        }
                    )

                }
                setNegativeButton("Tidak") { dialog, _ -> dialog.dismiss() }
            }.create()
                .show()
        }
    }

    private fun showEditKavlingDialog(kavling: Kavling) {
        val dialogBinding = DialogEditKavlingBinding.inflate(layoutInflater)
        val dialogView = AlertDialog.Builder(this).apply {
            setView(dialogBinding.root)
        }.create()

        DialogUtil.additionalDialogSetting(this, dialogView)

        dialogView.show()

        dialogBinding.edtPanjang.setText(kavling.getPanjang())
        dialogBinding.edtLebar.setText(kavling.getLebar())
        dialogBinding.edtTipeRumah.setText(kavling.type)

        dialogBinding.btnSimpan.setOnClickListener {
            dialogBinding.btnSimpan.isEnabled = false
            dialogBinding.btnSimpan.text = "Menyimpan data ..."

            val isInvalidEdt = InputUtil.isNullOrEmptyEditTexts(
                dialogBinding.edtPanjang, dialogBinding.edtLebar, dialogBinding.edtTipeRumah)

            if (!isInvalidEdt) {
                val newPanjang = dialogBinding.edtPanjang.text.toString()
                val newLebar = dialogBinding.edtLebar.text.toString()
                val newType = dialogBinding.edtTipeRumah.text.toString()
                val blockCode = viewModel.currentBlock.value!!

                viewModel.editKavling(
                    blockKode = blockCode,
                    oldKavling = kavling,
                    newPanjang = newPanjang,
                    newLebar = newLebar,
                    newType = newType,
                    onComplete = { msg ->
                        syncData()
                        dialogView.dismiss()
                        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                    }
                )
            }
        }

        dialogBinding.btnBatal.setOnClickListener {
            dialogView.dismiss()
        }
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

    private fun getBlocks() {
        viewModel.getAllBlocks { failMsg ->
            Snackbar.make(binding.root, failMsg, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun onOfflineState() {
        hideFabs()
        binding.fabActions.hide()
    }

    private fun onOnlineState() {
        binding.fabActions.show()
    }
}