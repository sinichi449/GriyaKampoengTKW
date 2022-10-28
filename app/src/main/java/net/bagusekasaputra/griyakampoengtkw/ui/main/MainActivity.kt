package net.bagusekasaputra.griyakampoengtkw.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.databinding.ActivityMainBinding
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

    }

    private fun setupViewModel() {
        viewModel.kavlings.observe(this) {
            it?.let {
                setupRecyclerView(it)
            }
        }
    }

    private fun setupRecyclerView(kavlings: List<Kavling>) {
        val adapter = MainAdapter(kavlings) {
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra(INTENT_KAVLING_KODE, kavlings[it].kode)
            }
            startActivity(intent)
        }
        binding.recyclerMain.adapter = adapter
        binding.recyclerMain.layoutManager = GridLayoutManager(this, 3)
    }

    override fun onResume() {
        super.onResume()

        viewModel.currentKode.value?.let {
            viewModel.getKavlings(it)
        }
    }
}