package net.bagusekasaputra.griyakampung.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampung.databinding.ActivityMainBinding
import net.bagusekasaputra.griyakampung.domain.entity.Kavling

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

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
            // TODO
            Toast.makeText(this, "${kavlings[it].kode} clicked!", Toast.LENGTH_SHORT).show()
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