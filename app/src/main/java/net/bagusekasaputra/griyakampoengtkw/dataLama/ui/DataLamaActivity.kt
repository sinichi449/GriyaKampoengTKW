package net.bagusekasaputra.griyakampoengtkw.dataLama.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.atwa.filepicker.core.FilePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.dataLama.AbstractDataLamaManager
import net.bagusekasaputra.griyakampoengtkw.dataLama.DataLamaModel
import net.bagusekasaputra.griyakampoengtkw.dataLama.DefaultDataLamaManager
import net.bagusekasaputra.griyakampoengtkw.databinding.ActivityDataLamaBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.MainActivity

@Suppress("DEPRECATION")
@AndroidEntryPoint
class DataLamaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDataLamaBinding

    private val PICK_DATA_LAMA_REQUEST = 2
    private val READ_STORAGE_REQUEST = 3

    private val dataLamaManager: AbstractDataLamaManager by lazy {
        DefaultDataLamaManager(this)
    }
    private val filePicker = FilePicker.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataLamaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Pilih Data"

        requestReadExternalStorage()

        dataLamaManager.createDataLamaFolderIfNotExist()

        binding.swipeRefreshDataLama.setOnRefreshListener {
            refreshData()
            binding.swipeRefreshDataLama.isRefreshing = false
        }

        binding.fabTambahData.setOnClickListener {
            pickFileAndExtract()
        }
    }

    override fun onResume() {
        super.onResume()

        refreshData()
    }

    private fun setupRecyclerView(listDataLamaModel: List<DataLamaModel>) {
        if (listDataLamaModel.isEmpty()) {
            binding.tvInfoTidakAdaData.visibility = View.VISIBLE
            binding.recyclerviewDataLama.visibility = View.GONE
        } else {
            binding.tvInfoTidakAdaData.visibility = View.GONE
            binding.recyclerviewDataLama.visibility = View.VISIBLE

            val adapter = DataLamaRecyclerAdapter(
                listDataLamaModel = listDataLamaModel,
                onItemClick = {
                    goToMainActivity(listDataLamaModel[it].path)
                },
                onDeleteAction = {
                    val model = listDataLamaModel[it]

                    MaterialAlertDialogBuilder(this)
                        .setTitle("Hapus ${model.name}?")
                        .setMessage("Apakah Anda yakin ingin menghapus data ini?")
                        .setPositiveButton("Ya") { dialog, _ ->
                            dataLamaManager.delete(
                                dataLamaModel = model,
                                onFinish = {
                                    refreshData()
                                    dialog.dismiss()
                                }
                            )
                        }
                        .setNegativeButton("Tidak") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                }
            )

            binding.recyclerviewDataLama.adapter = adapter
            binding.recyclerviewDataLama.layoutManager = LinearLayoutManager(this)
        }
    }

    private fun refreshData() {
        dataLamaManager.getFiles().let {
            setupRecyclerView(it)
        }
    }

    private fun goToMainActivity(dataLamaPath: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("dataLamaPath", dataLamaPath)

        startActivity(intent)
    }

    private fun pickFileAndExtract() {
        filePicker.pickFile { meta ->
            val file = meta?.file

            if (file != null) {
                dataLamaManager.extract(
                    pathToFile = file.path,
                    onFinish = {
                        refreshData()
                    }
                )
            } else {
                Toast.makeText(this, "ERROR: File tidak ditemukan", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun requestReadExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))

                startActivityForResult(intent, READ_STORAGE_REQUEST)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(intent, READ_STORAGE_REQUEST);
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                READ_STORAGE_REQUEST,
            )
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        when (requestCode) {
//            PICK_DATA_LAMA_REQUEST -> {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    if (Environment.isExternalStorageManager()) {
//                        extractBackupFilesFiles(data)
//                    } else {
//                        Toast.makeText(this, "Akses penyimpanan telah ditolak!", Toast.LENGTH_LONG)
//                            .show()
//                    }
//                } else {
//                    if (resultCode == RESULT_OK) {
//                        extractBackupFilesFiles(data)
//                    } else {
//                        Toast.makeText(this, "Akses penyimpanan telah ditolak!", Toast.LENGTH_LONG)
//                            .show()
//                    }
//                }
//            }
//        }
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == READ_STORAGE_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("DEBUG_ME", "Read external storage permission granted.")
            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d("DEBUG_ME", "Write external storage permission granted.")
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}