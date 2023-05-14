package net.bagusekasaputra.griyakampoengtkw.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.evrencoskun.tableview.listener.ITableViewListener
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DatabaseUser
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentDatabaseUserBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.DatabaseUserTableWrapper
import net.bagusekasaputra.griyakampoengtkw.presentation.util.UiUtils
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.DatabaseUserViewModel

@AndroidEntryPoint
class DatabaseUserFragment : Fragment() {

    private lateinit var binding: FragmentDatabaseUserBinding
    private val databaseUserViewModel: DatabaseUserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDatabaseUserBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()

        binding.swipeRefreshCalonPembeli.setOnRefreshListener {
            sync()
        }

        binding.fabAddUser.setOnClickListener {
            // TODO
        }
    }

    private fun setupViewModel() {
        databaseUserViewModel.listDatabaseUserLive.observe(requireActivity()) {
            it?.also { calonPembelis ->
                setupTableView(calonPembelis)
            }
        }
    }

    private fun setupTableView(listDatabaseUser: List<DatabaseUser>) {
        val tableListener = object : ITableViewListener {
            override fun onCellClicked(cellView: RecyclerView.ViewHolder, column: Int, row: Int) {
                // Open whatsapp on the one of "No Hp" column cell
                if (column == 1) {
                    val noHp = listDatabaseUser[row].noHp
                    UiUtils.openWhatsapp(requireContext(), noHp)
                }
            }

            override fun onCellDoubleClicked(
                cellView: RecyclerView.ViewHolder,
                column: Int,
                row: Int
            ) {

            }

            override fun onCellLongPressed(
                cellView: RecyclerView.ViewHolder,
                column: Int,
                row: Int
            ) {

            }

            override fun onColumnHeaderClicked(
                columnHeaderView: RecyclerView.ViewHolder,
                column: Int
            ) {

            }

            override fun onColumnHeaderDoubleClicked(
                columnHeaderView: RecyclerView.ViewHolder,
                column: Int
            ) {

            }

            override fun onColumnHeaderLongPressed(
                columnHeaderView: RecyclerView.ViewHolder,
                column: Int
            ) {

            }

            override fun onRowHeaderClicked(rowHeaderView: RecyclerView.ViewHolder, row: Int) {

            }

            override fun onRowHeaderDoubleClicked(
                rowHeaderView: RecyclerView.ViewHolder,
                row: Int
            ) {

            }

            override fun onRowHeaderLongPressed(rowHeaderView: RecyclerView.ViewHolder, row: Int) {

            }

        }

        DatabaseUserTableWrapper(binding.tableViewCalonPembeli, listDatabaseUser)
            .setTableListener(tableListener)
            .createTable()
    }

    private fun sync() {
        databaseUserViewModel.getListDatabaseUser(
            onComplete = {
                binding.swipeRefreshCalonPembeli.isRefreshing = false
            },
            onFailure = {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        )
    }
}