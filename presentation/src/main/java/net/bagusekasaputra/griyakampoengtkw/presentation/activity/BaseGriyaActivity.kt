package net.bagusekasaputra.griyakampoengtkw.presentation.activity

import android.app.ActivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.preference.PreferenceManager
import net.bagusekasaputra.griyakampoengtkw.presentation.R

open class BaseGriyaActivity: AppCompatActivity() {

    companion object {
        const val SELECTED_BRANCH = "SELECTED_BRANCH"
        const val BRANCH_GKT1 = "GKT1"
        const val BRANCH_GKT2 = "GKT2"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        applyBranchTheme()
    }

    private fun applyBranchTheme() {
        // 1. Get Preference
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val selectedBranch = sharedPrefs.getString(SELECTED_BRANCH, BRANCH_GKT1)

        // 2. Check Context
        val secondaryColor: Int
        val secondaryDarkColor: Int
        val taskDescriptionLabelBranch: String

        if (selectedBranch == BRANCH_GKT2) {
            secondaryColor = ContextCompat.getColor(this, R.color.gkt2_primary) // Define this in colors.xml
            secondaryDarkColor = ContextCompat.getColor(this, R.color.gkt2_primary_dark)
            taskDescriptionLabelBranch = "GKT 2"
        } else {
            secondaryColor = ContextCompat.getColor(this, R.color.gkt1_primary) // Define this in colors.xml
            secondaryDarkColor = ContextCompat.getColor(this, R.color.gkt1_primary_dark)
            taskDescriptionLabelBranch = "GKT 1"
        }

        // A. Color the Status Bar (The System Bar at the top)
        window.statusBarColor = secondaryDarkColor

        // B. Color the Action Bar (Toolbar)
        supportActionBar?.setBackgroundDrawable(secondaryColor.toDrawable())

        // C. Update the App Switcher (Recents) Color
        // This ensures even when minimized, the user sees the "Orange" card
        val taskDescription = ActivityManager.TaskDescription(
            "$taskDescriptionLabelBranch - $title",
            null,
            secondaryColor
        )
        setTaskDescription(taskDescription)
    }
}