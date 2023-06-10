package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.management

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.ProgressKavling
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.DetailActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.MainActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.adapter.recyclerview.BlockRecyclerAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.adapter.recyclerview.KavlingRecyclerAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.adapter.recyclerview.KavlingRecyclerAdapterLegacy
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogAddBlockBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogAddKavlingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogEditKavlingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentKavlingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.model.UiState
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DialogUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.FabHelper
import net.bagusekasaputra.griyakampoengtkw.presentation.util.InputUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.MainViewModel

@AndroidEntryPoint
class KavlingFragment : Fragment(), KavlingRecyclerAdapter.ItemListener {

    private lateinit var binding: FragmentKavlingBinding
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var fabActions: ExtendedFloatingActionButton
    private lateinit var fabAddKavling: FloatingActionButton
    private lateinit var fabAddBlock: FloatingActionButton

    private var kavlingRecyclerAdapter: KavlingRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("KAVLING_FRAGMENT", "onCreate()")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentKavlingBinding.inflate(inflater, container, false)

        Log.d("KAVLING_FRAGMENT", "onCreateView()")

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        Log.d("KAVLING_FRAGMENT", "onStart()")
    }

    override fun onResume() {
        super.onResume()

        Log.d("KAVLING_FRAGMENT", "onResume()")
    }

    override fun onPause() {
        Log.d("KAVLING_FRAGMENT", "onPause()")
        super.onPause()
    }

    override fun onStop() {
        Log.d("KAVLING_FRAGMENT", "onStop()")
        super.onStop()
    }

    override fun onDestroyView() {
        Log.d("KAVLING_FRAGMENT", "onDestroyView()")
        super.onDestroyView()
    }


    override fun onDestroy() {
        Log.d("KAVLING_FRAGMENT", "onDestroy()")
        super.onDestroy()
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("KAVLING_FRAGMENT", "onViewCreated()")

        fabActions = requireActivity().findViewById(R.id.fab_actions)
        fabAddKavling = requireActivity().findViewById(R.id.fab_add_kavling)
        fabAddBlock = requireActivity().findViewById(R.id.fab_add_block)

        // Init RecyclerKavlings
        with(binding.recyclerKavlings) {
            kavlingRecyclerAdapter = KavlingRecyclerAdapter(emptyList(), this@KavlingFragment)
            adapter = kavlingRecyclerAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }

        binding.setupWithViewModel()

//        setupFloatingButtons()
        val fabHelper = FabHelper(
            fabAction = fabActions,
            fabs = arrayOf(fabAddKavling, fabAddBlock),
        )
        fabHelper.setupFabs()

        // Hide Fab Action on Kavling RecyclerView scrolling down
        hideFabActionsOnKavlingScroll()

        val offlineMode = viewModel.offlineMode
        val dataMode = viewModel.dataMode
        if (offlineMode || dataMode != DataMode.ONLINE)
            // Enable offline mode means disabling the write operation on the data,
            // which is done, in this case, by the FABS. I've encapsulated the needed to disable
            // operation interface in this method.
//            onOfflineState()
            fabHelper.fabOnfflineState()


        fabAddKavling.setOnClickListener {
            showAddKavlingDialog()
        }

        fabAddBlock.setOnClickListener {
            showAddBlockDialog()
        }

        binding.swipeRefreshMain.setOnRefreshListener {
            // When user invokes refresh, we need to update the "xRefreshed" value in viewModel
            // to be FALSE.
            viewModel.blockRefreshed.value = false

            // Set the refreshed status of kavling with a corresponding
            // context Block's code
            val currentBlockKode = viewModel.currentBlock.value
            currentBlockKode?.let { viewModel.kavlingsRefreshed[it]?.value = false }

            syncBlocks()
        }

        syncBlocks()
    }

    private fun FragmentKavlingBinding.setupWithViewModel() {
        // Observe the blocksLive and if it not NULL, then call fetchKavlingFragmentUiState()
        viewModel.blocksLive.observe(requireActivity()) {
            it?.also { blockList ->
                if (blockList.isNotEmpty()) {
                    recyclerBlocks.setupBlocks(blockList)

                    val blockFirstItem = blockList.first()
//                    viewModel.fetchKavlingFragmentUiState(blockFirstItem.kode)
                    fetchKavlingList(blockFirstItem.kode)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.kavlingAndProgressList.collect {
                    kavlingRecyclerAdapter?.update(it)
                    binding.recyclerKavlings.adapter = kavlingRecyclerAdapter
                }
            }
        }
    }

    private fun syncBlocks() {
        viewModel.getAllBlocks {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    }

    private fun RecyclerView.setupBlocks(blockList: List<Block>) {
        binding.recyclerBlocks.adapter = BlockRecyclerAdapter(blockList) { position ->
            val selectedBlock = blockList[position].kode

            // Update the selected block in the viewModel
            viewModel.currentBlock.value = selectedBlock

//            viewModel.fetchKavlingFragmentUiState(selectedBlock)
            fetchKavlingList(selectedBlock)
        }

        // If screen orientation is Landscape, then set the
        // Block Recycler orientation to be Vertical instead, with a GridView
        val screenOrientation = resources.configuration.orientation
        binding.recyclerBlocks.layoutManager = if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE)
            GridLayoutManager(requireContext(), 2)
        else
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun fetchKavlingList(blok: String) {
        with(binding) {
            viewModel.fetchKavlingListOn(
                blockKode = blok,
                onLoading = {
                    swipeRefreshMain.isRefreshing = true
                },
                onComplete = {
                    swipeRefreshMain.isRefreshing = false
                },
                onFailure = {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    override fun onKavlingItemClick(kavlingView: MaterialCardView, position: Int) {
        val intent = Intent(requireContext(), DetailActivity::class.java).apply {
            val kavlingAndProgress = viewModel.kavlingAndProgressList.value
            val kavlingOnly = kavlingAndProgress[position].kavling

            putExtra(MainActivity.INTENT_KAVLING_KODE, kavlingOnly.kode)
        }
        startActivity(intent)
    }

    override fun onKavlingItemHold(kavlingView: MaterialCardView, anchor: View, position: Int): Boolean {
        // TODO
        return false
    }

    @Deprecated("Migrated to FragmentKavlingBinding.setupWithViewModel()")
    @SuppressLint("SetTextI18n")
    private fun setupViewModelLegacy() {
        viewModel.blocksLive.observe(requireActivity()) {
            it?.let {
                setupBlockRecyclerview(it)
            }
        }

        viewModel.kavlingAndProgress.observe(requireActivity()) {
            it?.also { kavlingAndProgress ->
                val kavlings = kavlingAndProgress.first
                val mapProgressKavling = kavlingAndProgress.second ?: emptyMap()

                if (mapProgressKavling.isNotEmpty()) {
                    Log.d("STATUS_PEMBAYARAN", "Success KavlingFragment not null!")

                    mapProgressKavling.keys.forEach { kavling ->
                        val persentase = mapProgressKavling[kavling]?.persentaseBulanIni()

                        Log.d("STATUS_PEMBAYARAN", "${kavling}: ${persentase}%")
                    }
                } else {
                    Log.d("STATUS_PEMBAYARAN", "KavlingFragment got NULL Progress")
                }

                if (!kavlings.isNullOrEmpty()) {
                    setupKavlingRecyclerView(kavlings, mapProgressKavling)
                }
            }
        }

        viewModel.isFinishOperation.observe(requireActivity()) {
            it?.let { finish ->
                binding.swipeRefreshMain.isRefreshing = !finish
            }
        }

        viewModel.currentBlock.observe(requireActivity()) { blockKode ->
            val infoBlokText = "Blok"
            if (blockKode != null) {
                val keteranganBlok = when (blockKode) {
                    "A" -> "A Lantai 1"
                    "B" -> "B Lantai 2"
                    "C" -> "C Lantai 1 (Type Custom)"
                    else -> "D (Developer)"
                }

                binding.tvInfoBlock?.text = "$infoBlokText $keteranganBlok"
            } else {
                binding.tvInfoBlock?.text = infoBlokText
            }
        }

    }

    @Deprecated("Migrated to sync()")
    private fun syncDataLegacy() {
        viewModel.getAllBlocks { failMsg ->
            Snackbar.make(binding.root, failMsg, Snackbar.LENGTH_LONG).show()
        }

        val currentBlock = viewModel.currentBlock.value
        if (currentBlock != null) {
            viewModel.getKavlings(currentBlock) { failMsg ->
                Snackbar.make(binding.root, failMsg, Snackbar.LENGTH_LONG).show()
            }

            viewModel.getProgressAllKavling(currentBlock)
        }
    }

    @Deprecated("Migrated to RecyclerView.setupBlocks()")
    private fun setupBlockRecyclerview(blocks: List<Block>) {
        val adapter = BlockRecyclerAdapter(blocks) { position ->
            val selectedBlock = blocks[position].kode

            // Update the selected block in the viewModel
            viewModel.currentBlock.value = selectedBlock

            viewModel.getKavlings(
                blockKode = selectedBlock,
                onFailure = { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
            )

            // Get Progress Kavling
            viewModel.getProgressAllKavling(selectedBlock)
        }

        binding.recyclerBlocks.adapter = adapter

        // If screen orientation is Landscape, then set the
        // Block Recycler orientation to be Vertical instead, with a GridView
        val screenOrientation = resources.configuration.orientation
        binding.recyclerBlocks.layoutManager = if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE)
            GridLayoutManager(requireContext(), 2)
        else
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    @Deprecated("")
    private fun setupKavlingRecyclerView(kavlings: List<Kavling>, mapProgressKavling: Map<String, ProgressKavling>) {
        val adapter = KavlingRecyclerAdapterLegacy(kavlings, mapProgressKavling,
            onRecyclerItemClick = {
                val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                    putExtra(MainActivity.INTENT_KAVLING_KODE, kavlings[it].kode)
                }
                startActivity(intent)
            },
            onRecyclerItemHold = { anchor, position ->
                showPopupActionKalvingDialog(anchor, kavlings[position])
            }
        )

//        val customAdapter = ScaleInAnimationAdapter(adapter)

//        kavlingRecyclerView.adapter = customAdapter

        binding.recyclerKavlings.adapter = adapter

        // If screen is in Landscape mode, I want to show more spans number in the kavling
        val screenOrientation = resources.configuration.orientation
        val spansCount = if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) 5 else 3
        binding.recyclerKavlings.layoutManager = GridLayoutManager(requireContext(), spansCount)


//        val kavlingRecyclerState = viewModel.kavlingRecyclerState
//        if (kavlingRecyclerState != null) {
//            kavlingRecyclerView.layoutManager?.onRestoreInstanceState(kavlingRecyclerState)
//        }
        binding.recyclerKavlings.setHasFixedSize(true)
    }

    @SuppressLint("SetTextI18n")
    private fun showAddBlockDialog() {
        val dialogBinding = DialogAddBlockBinding.inflate(layoutInflater)
//        val addBlockDialog = AlertDialog.Builder(requireContext()).apply {
//            setView(dialogBinding.root)
//        }.create()
        val addBlockDialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setView(dialogBinding.root)
        }.create()

        DialogUtil.additionalDialogSetting(requireContext(), addBlockDialog)

        addBlockDialog.show()

        dialogBinding.btnPilihWarna.setOnClickListener {
            MaterialColorPickerDialog.Builder(requireContext())
                .setTitle("Pilih Warna")
                .setColorShape(ColorShape.CIRCLE)
                .setColorSwatch(ColorSwatch._500)
                .setDefaultColor(R.color.abang)
                .setColorListener { _, colorHex ->
                    val removeHash = colorHex.substring(1)
                    dialogBinding.edtWarna.setText(removeHash)
                }
                .show()
        }

        dialogBinding.btnTambahkan.setOnClickListener {
            val isInvalidEdt = InputUtil.isNullOrEmptyEditTexts(
                dialogBinding.edtKode, dialogBinding.edtWarna
            )

            if (!isInvalidEdt) {
                dialogBinding.btnTambahkan.isEnabled = false
                dialogBinding.btnTambahkan.text = "Menyimpan data ..."

                val kode = dialogBinding.edtKode.text.toString()
                val warna = dialogBinding.edtWarna.text.toString()

                viewModel.addNewBlock(
                    kode = kode,
                    warna = warna,
                    onComplete = { msg ->
//                        syncDataLegacy()
                        syncBlocks()
                        addBlockDialog.dismiss()
                        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT)
                            .show()
                    }
                )
            }
        }

        dialogBinding.btnBatal.setOnClickListener {
            addBlockDialog.dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showAddKavlingDialog() {
        val dialogBinding = DialogAddKavlingBinding.inflate(layoutInflater)
//        val addKavlingDialog = AlertDialog.Builder(this).apply {
//            setView(dialogBinding.root)
//        }.create()
        val addKavlingDialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setView(dialogBinding.root)
        }.create()

        DialogUtil.additionalDialogSetting(requireContext(), addKavlingDialog)

        // Setting up spinner which shows a list of available Blocks
        val blockLists = ArrayList<String>()
        viewModel.blocksLive.value?.forEach {
            blockLists.add(it.kode)
        }
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, blockLists)
        dialogBinding.spinnerBlocks.adapter = spinnerAdapter

        addKavlingDialog.show()



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
                    syncBlocks()
                    addKavlingDialog.dismiss()
                    Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                }

            }
        }

        dialogBinding.btnBatal.setOnClickListener {
            addKavlingDialog.dismiss()
        }
    }

    private fun showPopupActionKalvingDialog(anchor: View, kavling: Kavling) {
        val popupMenu = PopupMenu(requireContext(), anchor)
        popupMenu.menuInflater.inflate(R.menu.menu_actions_kavling, popupMenu.menu)

        popupMenu.show()

        // On Click menu
        popupMenu.setOnMenuItemClickListener {
            popupMenu.dismiss()

            when (it.itemId) {
                R.id.popup_kavling_edit -> {

                    showEditKavlingDialog(kavling)

                    true
                }
                R.id.popup_kalving_hapus -> {
                    // Show delete kavling confirmation
                    MaterialAlertDialogBuilder(requireContext()).apply {
                        setTitle("Hapus Kavling")
                        setMessage("Apakah Anda yakin menghapus kavling ${kavling.kode}?")
                        setPositiveButton("Ya") { dialog, _ ->
                            val blockKode = viewModel.currentBlock.value!!

                            viewModel.removeKavling(
                                blockKode = blockKode,
                                kavlingKode = kavling.kode,
                                onComplete = { msg ->
                                    syncBlocks()
                                    dialog.dismiss()
                                    Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                                }
                            )

                        }
                        setNegativeButton("Tidak") { dialog, _ -> dialog.dismiss() }
                    }.create()
                        .show()

                    true
                }
                else -> false
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showEditKavlingDialog(kavling: Kavling) {
        val dialogBinding = DialogEditKavlingBinding.inflate(layoutInflater)
//        val editKavlingDialog = AlertDialog.Builder(requireContext()).apply {
//            setView(dialogBinding.root)
//        }.create()
        val editKavlingDialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setView(dialogBinding.root)
        }.create()

        DialogUtil.additionalDialogSetting(requireContext(), editKavlingDialog)

        editKavlingDialog.show()

        // Setup according to available data
        dialogBinding.edtPanjang.setText(kavling.getPanjang())
        dialogBinding.edtLebar.setText(kavling.getLebar())
        dialogBinding.edtTipeRumah.setText(kavling.type)


        dialogBinding.btnSimpan.setOnClickListener {
            val isInvalidEdt = InputUtil.isNullOrEmptyEditTexts(
                dialogBinding.edtPanjang, dialogBinding.edtLebar, dialogBinding.edtTipeRumah)

            if (!isInvalidEdt) {
                dialogBinding.btnSimpan.isEnabled = false
                dialogBinding.btnSimpan.text = "Menyimpan data ..."

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
                        syncBlocks()
                        editKavlingDialog.dismiss()
                        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                    }
                )
            }
        }

        dialogBinding.btnBatal.setOnClickListener {
            editKavlingDialog.dismiss()
        }
    }

    private fun hideFabActionsOnKavlingScroll() {
        binding.recyclerKavlings.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val scrollDown = dy > 0

                if (scrollDown) {
                    // show fab action
                    fabActions.hide()
                } else {
                    // hide fab action
                    fabActions.show()
                }
            }
        })
    }

    @Deprecated("")
    private fun observeKavlingListLegacy() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.kavlingFragmentUiState.collect {
                    it?.also {  uiState ->
                        with(binding) {
                            when (uiState) {
                                is UiState.Loading -> {
                                    swipeRefreshMain.isRefreshing = true
                                }
                                is UiState.Success -> {
                                    swipeRefreshMain.isRefreshing = false

                                    uiState.data?.also { kavlingUiState ->
                                        setupKavlingRecyclerView(
                                            kavlings = kavlingUiState.kavlingList,
                                            mapProgressKavling = kavlingUiState.progressKavlingMap,
                                        )
                                    }
                                }
                                is UiState.Failure -> {
                                    Toast.makeText(requireContext(), uiState.failMsg, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}