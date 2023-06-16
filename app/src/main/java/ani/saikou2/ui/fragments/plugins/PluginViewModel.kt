package ani.saikou2.ui.fragments.plugins

import androidx.lifecycle.ViewModel
import ani.saikou2.data.plugins.PackageWithPlugins
import ani.saikou2.data.plugins.PluginManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PluginViewModel @Inject constructor(
    private val pluginManager: PluginManager
) : ViewModel() {

    val packagesInDatabase = pluginManager.allPackages

    fun deletePackage(pluginPackage: PackageWithPlugins)
            = pluginManager.deletePackage(pluginPackage)

    suspend fun startDownload(url: String)
            = pluginManager.startDownload(url)

    suspend fun reinstallPackage(packageWithPlugins: PackageWithPlugins)
            = pluginManager.reinstallPackage(packageWithPlugins)
}