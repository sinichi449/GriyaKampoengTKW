package net.bagusekasaputra.griyakampoengtkw

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.databinding.DialogPilihTahapanBinding
import net.bagusekasaputra.griyakampoengtkw.databinding.RecyclerItemTahapanBinding
import net.bagusekasaputra.griyakampoengtkw.model.Tahapan

@AndroidEntryPoint
class PilihTahapanBottomSheetDialog(
    private val onItemSelected: (dialog: DialogFragment, tahapan: Tahapan) -> Unit,
    private val onFailure: () -> Unit,
): BottomSheetDialogFragment() {

    private lateinit var binding: DialogPilihTahapanBinding
    private val viewModel by activityViewModels<AppViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogPilihTahapanBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = false

        with(binding) {
            layoutLoadingTahapan.visibility = View.VISIBLE
            layoutFailure.visibility = View.GONE

            viewModel.getAllTahapan(
                onProgress = {
                    layoutLoadingTahapan.visibility = View.VISIBLE
                    layoutPilihTahapan.visibility = View.GONE
                },
                onSuccess = {
                    layoutLoadingTahapan.visibility = View.GONE
                    layoutPilihTahapan.visibility = View.VISIBLE
                },
                onFailure = { failmsg ->
                    layoutLoadingTahapan.visibility = View.GONE
                    layoutPilihTahapan.visibility = View.GONE
                    layoutFailure.visibility = View.VISIBLE

                    tvFailureMessage.text = failmsg
                    btnKeluar.setOnClickListener {
                        onFailure()
                    }
                }
            )
        }

        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel.tahapanList.observe(requireActivity()) {
            with(binding) {
                if (!it.isNullOrEmpty()) {
                    tvTidakAdaTahapan.visibility = View.GONE
                    recyclerViewTahapan.visibility = View.VISIBLE

                    recyclerViewTahapan.bindAdapter(requireContext(), it) {
                        onItemSelected(this@PilihTahapanBottomSheetDialog, it)
                    }
                } else {
                    tvTidakAdaTahapan.visibility = View.VISIBLE
                    recyclerViewTahapan.visibility = View.GONE
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun RecyclerView.bindAdapter(
        context: Context,
        tahapanList: List<Tahapan>,
        onTahapanItemClick: (tahapan: Tahapan) -> Unit
    ) {
        val tahapanRecyclerAdapter = TahapanRecyclerAdapter(tahapanList, onTahapanItemClick)

        adapter = tahapanRecyclerAdapter
        layoutManager = LinearLayoutManager(context)

        tahapanRecyclerAdapter.notifyDataSetChanged()
    }
}

class TahapanRecyclerAdapter(
    private val tahapanList: List<Tahapan>,
    private val onTahapanItemClick: (tahapan: Tahapan) -> Unit,
): RecyclerView.Adapter<TahapanRecyclerAdapter.TahapanViewHolder>() {

    class TahapanViewHolder(private val itemBinding: RecyclerItemTahapanBinding)
        : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(tahapan: Tahapan, onBtnItemClickListener: () -> Unit) {
            tahapan.also {
                itemBinding.btnItemTahapan.text = tahapan.nama
                itemBinding.btnItemTahapan.setOnClickListener { onBtnItemClickListener() }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TahapanViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = RecyclerItemTahapanBinding.inflate(inflater, parent, false)

        return TahapanViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return tahapanList.size
    }

    override fun onBindViewHolder(holder: TahapanViewHolder, position: Int) {
        val tahapan = tahapanList[position]

        holder.bind(tahapan) {
            onTahapanItemClick(tahapan)
        }
    }
}