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
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.SettingsActivityBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.SettingsViewModel

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
            ProgressDialog(requireContext())
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val backupData = findPreference<Preference>("backup_data")
            val restoreData = findPreference<Preference>("restore_data")

            backupData?.setOnPreferenceClickListener {
                progressDialog.setTitle("Membackup Data")

                viewModel.createBackup {
                    // TODO: On failure
                    Toast.makeText(
                        requireContext(),
                        "Gagal membuat backup: $it",
                        Toast.LENGTH_LONG
                    ).show()
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
            viewModel.progressLive.observe(this) { progress ->
                progress?.let {
                    progressDialog.setMessage(it)
                }
            }
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