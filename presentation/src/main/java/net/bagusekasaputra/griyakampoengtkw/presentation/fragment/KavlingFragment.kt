package net.bagusekasaputra.griyakampoengtkw.presentation.fragment

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.activities.DetailActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.activities.MainActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.adapter.recyclerview.BlockRecyclerAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.adapter.recyclerview.KavlingRecyclerAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.*
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DialogUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.FabHelper
import net.bagusekasaputra.griyakampoengtkw.presentation.util.InputUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.MainViewModel

@AndroidEntryPoint
class KavlingFragment : Fragment() {

    private lateinit var binding: FragmentKavlingBinding
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var fabActions: ExtendedFloatingActionButton
    private lateinit var fabAddKavling: FloatingActionButton
    private lateinit var fabAddBlock: FloatingActionButton

    private var isAllFabsVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentKavlingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabActions = requireActivity().findViewById(R.id.fab_actions)
        fabAddKavling = requireActivity().findViewById(R.id.fab_add_kavling)
        fabAddBlock = requireActivity().findViewById(R.id.fab_add_block)

        setupViewModel()

//        setupFloatingButtons()
        val fabHelper = FabHelper(
            fabAction = fabActions,
            fabs = arrayOf(fabAddKavling, fabAddBlock),
        )
        fabHelper.setupFabs()

        // Hide Fab Action on Kavling RecyclerView scrolling down
        hideFabActionsOnKavlingScroll()

        val offlineMode = viewModel.offlineMode
        if (offlineMode)
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

            syncData()
        }
    }

    override fun onResume() {
        super.onResume()

        syncData()
    }

    private fun setupViewModel() {
        viewModel.blocksLive.observe(requireActivity()) {
            it?.let {
                setupBlockRecyclerview(it)
            }
        }

        viewModel.kavlings.observe(requireActivity()) {
            it?.let {
                setupKavlingRecyclerView(it)
            }
        }

        viewModel.isFinishOperation.observe(requireActivity()) {
            it?.let { finish ->
                binding.swipeRefreshMain.isRefreshing = !finish
            }
        }
    }

    private fun syncData() {
        viewModel.getAllBlocks { failMsg ->
            Snackbar.make(binding.root, failMsg, Snackbar.LENGTH_LONG).show()
        }

        val currentBlock = viewModel.currentBlock.value
        if (currentBlock != null)
            viewModel.getKavlings(currentBlock) { failMsg ->
                Snackbar.make(binding.root, failMsg, Snackbar.LENGTH_LONG).show()
            }
    }

    private fun setupFloatingButtons() {
        fabAddKavling.visibility = View.GONE
        fabAddBlock.visibility = View.GONE

        fabActions.shrink()

        fabActions.setOnClickListener {
            if (isAllFabsVisible) {
                hideFabs()
            } else {
                showFabs()
            }
        }
    }

    private fun hideFabs() {
        fabActions.shrink()

        fabAddKavling.hide()
        fabAddBlock.hide()

        isAllFabsVisible = false
    }

    private fun showFabs() {
        fabActions.extend()

        fabAddKavling.show()
        fabAddBlock.show()

        isAllFabsVisible = true
    }

    private fun setupBlockRecyclerview(blocks: List<Block>) {
        val adapter = BlockRecyclerAdapter(blocks) { position ->
            val selectedBlock = blocks[position].kode

            // Update the selected block in the viewModel
            viewModel.currentBlock.value = selectedBlock

            viewModel.getKavlings(
                blockKode = selectedBlock,
                onFailure = { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
            )
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

    private fun setupKavlingRecyclerView(kavlings: List<Kavling>) {
        val adapter = KavlingRecyclerAdapter(requireContext(), kavlings,
            onRecyclerItemClick = {
                val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                    putExtra(MainActivity.INTENT_KAVLING_KODE, kavlings[it].kode)
                }
                startActivity(intent)
            },
            onRecyclerItemHold = { anchor, position ->
                showActionKavlingDialog(kavlings[position])
                showPopupActionKalvingDialog(anchor, kavlings[position])
            }
        )

        val customAdapter = ScaleInAnimationAdapter(adapter)

        binding.recyclerKavlings.adapter = customAdapter

        // If screen is in Landscape mode, I want to show more spans number in the kavling
        val screenOrientation = resources.configuration.orientation
        val spansCount = if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) 5 else 3
        binding.recyclerKavlings.layoutManager = GridLayoutManager(requireContext(), spansCount)
    }

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
                        syncData()
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
                    syncData()
                    addKavlingDialog.dismiss()
                    Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                }

            }
        }

        dialogBinding.btnBatal.setOnClickListener {
            addKavlingDialog.dismiss()
        }
    }

    private fun showActionKavlingDialog(kavling: Kavling) {
        val dialogBinding = DialogActionKavlingBinding.inflate(layoutInflater)
//        val actionKavlingDialog = AlertDialog.Builder(this).apply {
//            setView(dialogBinding.root)
//        }.create()
        val actionKavlingDialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setView(dialogBinding.root)
        }.create()

        DialogUtil.additionalDialogSetting(requireContext(), actionKavlingDialog)

        actionKavlingDialog.show()

        // Need to be separated like this ...
        val text = "Kavling ${kavling.kode}"
        dialogBinding.tvKavlingKode.text = text



        dialogBinding.btnEdit.setOnClickListener {
            actionKavlingDialog.dismiss()
            showEditKavlingDialog(kavling)
        }

        dialogBinding.btnBatal.setOnClickListener {
            actionKavlingDialog.dismiss()
        }

        dialogBinding.btnHapusKavling.setOnClickListener {
            actionKavlingDialog.dismiss()

            MaterialAlertDialogBuilder(requireContext()).apply {
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
                                    syncData()
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
                        syncData()
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

    private fun onOfflineState() {
        hideFabs()
        fabActions.hide()
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
}