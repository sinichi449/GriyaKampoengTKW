package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.pembatalan

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.DetailActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.MainActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.adapter.recyclerview.BlockRecyclerAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.adapter.recyclerview.KavlingPembatalanRecyclerAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentPembatalanListBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.PembatalanViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [PembatalanListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class PembatalanListFragment : Fragment() {

    private lateinit var binding: FragmentPembatalanListBinding
    private val viewModel: PembatalanViewModel by activityViewModels()
    private var kavlingPembatalanRecyclerAdapter: KavlingPembatalanRecyclerAdapter? = null

    // Write to sharedPrefs to Pembatalan Node to retrieve from Firebase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sharedPrefs.edit(true) {
            putString("NODE_TYPE", "NODE_PEMBATALAN")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPembatalanListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init RecyclerKavlings
        with(binding.recyclerKavlings) {
            kavlingPembatalanRecyclerAdapter = KavlingPembatalanRecyclerAdapter(emptyList(), ::onKavlingItemClick)
            adapter = kavlingPembatalanRecyclerAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }

        setupViewModel()

        binding.swipeRefreshMain.setOnRefreshListener {
            syncBlocks()
        }

        syncBlocks()
    }

    private fun setupViewModel() {
        viewModel.blocksLive.observe(requireActivity()) {
            it?.also { blockList ->
                if (blockList.isNotEmpty()) {
                    setupBlocks(binding.recyclerBlocks, blockList)

                    // Fetch kavling list automatically for the first block
                    val firstBlock = blockList.first()
                    fetchKavlingList(firstBlock.kode)
                }
            }
        }

        viewModel.kavlingAndNamaLive.observe(requireActivity()) {
            it?.also { kavlingList ->
                if (kavlingList.isNotEmpty()) {
                    kavlingPembatalanRecyclerAdapter?.update(kavlingList)
                    kavlingPembatalanRecyclerAdapter?.notifyDataSetChanged()
                }
            }
        }
    }

    private fun onKavlingItemClick(v: MaterialCardView, position: Int) {
        val intent = Intent(requireContext(), DetailActivity::class.java).apply {
            val kavling = viewModel.kavlingAndNamaLive.value
                ?.get(position)?.kavlingKode
            putExtra(MainActivity.INTENT_KAVLING_KODE, kavling)
            putExtra(MainActivity.INTENT_PEMBATALAN, true)
        }

        startActivity(intent)
    }

    private fun setupBlocks(rv: RecyclerView, blocks: List<Block>) {
        rv.adapter = BlockRecyclerAdapter(blocks) { position: Int ->
            val selectedBlock = blocks[position].kode

            // Update selected block in the viewModel
            viewModel.currentBlock.value = selectedBlock

            fetchKavlingList(selectedBlock)
        }

        val screenOrientation = resources.configuration.orientation
        rv.layoutManager = if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE)
            GridLayoutManager(requireContext(), 2)
        else
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun fetchKavlingList(blok: String) {
        viewModel.getAllKavlings(
            blok = blok,
            onLoading = { binding.swipeRefreshMain.isRefreshing = true },
            onComplete =  { binding.swipeRefreshMain.isRefreshing = false },
            onFailure = { Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show() }
        )
    }

    private fun syncBlocks() {
        val snackBarLoading = Snackbar.make(binding.root, "Mendapatkan list Blok ...", Snackbar.LENGTH_INDEFINITE)
        viewModel.getAllBlocks(
            onLoading = { snackBarLoading.show() },
            onComplete = { snackBarLoading.dismiss() },
            onFailure = {
                snackBarLoading.dismiss()
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        )
    }

}