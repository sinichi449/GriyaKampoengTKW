package net.bagusekasaputra.griyakampoengtkw.ui.custom

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import com.github.amlcurran.showcaseview.ShowcaseView
import com.github.amlcurran.showcaseview.targets.Target
import net.bagusekasaputra.griyakampoengtkw.R
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes

class NewFeatureShowCase(private val activity: Activity) {

    private val ctx = activity.applicationContext

    data class Feature(
        val view: View,
        val featureName: String,
        val title: String,
        val content: String,
    )

    private val featureList = ArrayList<Feature>()
    private val seenFeatures = ArrayList<Feature>()
    private val notSeenFeatures = ArrayList<Feature>()

    private val sharedPrefs = ctx.getSharedPreferences(GriyaNodes.SHARED_PREFS_NEW_FEATURE, Context.MODE_PRIVATE)


    fun show() {
        collectFeatures()

        Handler(Looper.myLooper()!!).postDelayed({
            notSeenFeatures.forEach {
                createShowCaseView(it.view, it.title, it.content).show()
                saveSharedPrefsBakdaDitunjukkanFeaturenya()
            }
        }, 500)
    }

    fun addFeature(feature: Feature) {
        featureList.add(feature)
    }

    private fun collectFeatures() {
        featureList.forEach {
            val isSeen = sharedPrefs.getBoolean(it.featureName, false)
            if (isSeen) seenFeatures.add(it) else notSeenFeatures.add(it)
        }
    }

    private fun saveSharedPrefsBakdaDitunjukkanFeaturenya() {
        notSeenFeatures.forEach {
            sharedPrefs.edit().apply {
                putBoolean(it.featureName, true)
            }.apply()
        }
    }

    private fun createShowCaseView(targetView: View, title: String, content: String): ShowcaseView {
        return ShowcaseView.Builder(activity)
            .setTarget(getTarget(targetView))
            .setContentTitle(title)
            .setContentText(content)
            .hideOnTouchOutside()
            .replaceEndButton(Button(ctx).apply { visibility = View.GONE })
            .setStyle(R.style.ShowcaseView_GriyaStyle)
            .build()
    }

    private fun getTarget(targetView: View): Target {
        return Target {
            Point(targetView.absX() + 70, targetView.absY() + 70)
        }
    }

    private fun View.absX(): Int {
        val location = IntArray(2)
        this.getLocationOnScreen(location)
        return location[0]
    }

    private fun View.absY(): Int {
        val location = IntArray(2)
        this.getLocationOnScreen(location)
        return location[1]
    }

}