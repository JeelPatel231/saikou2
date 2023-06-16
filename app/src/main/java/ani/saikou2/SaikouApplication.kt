package ani.saikou2

import android.app.Application
import ani.saikou2.data.plugins.PluginsDI
import ani.saikou2.di.Injection
import ani.saikou2.reference.ExtractorMap
import ani.saikou2.reference.ParserMap
import ani.saikou2.reference.initializeNetwork
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidApp
class SaikouApplication: Application() {

    @Inject lateinit var parserMap: Injection.ParserProxy
    @Inject lateinit var extractorMap: Injection.ExtractorProxy

    override fun onCreate() {
        super.onCreate()

        // initialize everything
        initializeNetwork()
        // safe to use injected variables
        ParserMap = parserMap.data
        ExtractorMap = extractorMap.data

        // Apply dynamic color
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}