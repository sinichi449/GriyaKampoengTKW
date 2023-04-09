package net.bagusekasaputra.griyakampoengtkw.presentation.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogCreateBackupBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.SettingsActivityBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DialogUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.InputUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.SettingsViewModel
import java.util.*

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: SettingsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setSupportActionBar(binding.toolbarSetting)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
    }

    @AndroidEntryPoint
    class SettingsFragment : PreferenceFragmentCompat() {
        private val viewModel: SettingsViewModel by viewModels()
        private val progressDialog by lazy {
            ProgressDialog(requireContext()).apply {
                isIndeterminate = false
            }
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val backupData = findPreference<Preference>("backup_data")
            val restoreData = findPreference<Preference>("restore_data")

            backupData?.setOnPreferenceClickListener {
                progressDialog.setTitle("Membackup Data")

                showSetNamaBackupDialog {
                    viewModel.createBackup(
                        backupName = it,
                        onFailure = { failReason ->
                            // TODO: On failure
                            Toast.makeText(
                                requireContext(),
                                "Gagal membuat backup: $failReason",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                }

                true
            }

            restoreData?.setOnPreferenceClickListener {
                // TODO
                Toast.makeText(requireContext(), "Ping pong!", Toast.LENGTH_SHORT).show()

                true
            }

            setupViewModel()
        }

        private fun setupViewModel() {
            viewModel.isBackupComplete.observe(this) { isComplete ->
                isComplete?.let {
                    if (it) progressDialog.dismiss() else progressDialog.show()
                }
            }
            viewModel.backupRestoreProgress.observe(this) { progress ->
                progress?.let {
                    progressDialog.progress = it.progress
                    progressDialog.setMessage(it.message)
                }
            }
        }

        private fun showSetNamaBackupDialog(onBtnOkClick: (namaBackup: String) -> Unit) {
            val dialogBinding = DialogCreateBackupBinding.inflate(layoutInflater)
            val dialogView = MaterialAlertDialogBuilder(requireContext()).apply {
                setView(dialogBinding.root)
            }.create()

            DialogUtil.additionalDialogSetting(requireContext(), dialogView)

            dialogBinding.edtNamaBackup.setText(getDefaultBackupName())

            dialogView.show()

            dialogBinding.btnBuatBackup.setOnClickListener {
                val isInvalidEdt = InputUtil.isNullOrEmptyEditTexts(dialogBinding.edtNamaBackup)

                if (!isInvalidEdt) {
                    val namaBackup = dialogBinding.edtNamaBackup.text.toString()

                    onBtnOkClick(namaBackup)

                    dialogView.dismiss()
                }
            }

            dialogBinding.btnBatal.setOnClickListener {
                dialogView.dismiss()
            }

        }

        private fun getDefaultBackupName(): String {
            val calendar = Calendar.getInstance()

            val padWithZero = { num: Int ->
                String.format("%02d", num)
            }

            val tahun = calendar.get(Calendar.YEAR)
            val bulan = padWithZero(calendar.get(Calendar.MONTH) + 1)
            val tanggal = padWithZero(calendar.get(Calendar.DAY_OF_MONTH))
            val jam = padWithZero(calendar.get(Calendar.HOUR_OF_DAY))
            val menit = padWithZero(calendar.get(Calendar.MINUTE))

            return "backup_$tahun$bulan${tanggal}_$jam$menit"
        }
    }

    private fun goMainActivity() {
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        startActivity(mainActivityIntent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        goMainActivity()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                goMainActivity()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}