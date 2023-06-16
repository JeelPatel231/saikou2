package ani.saikou2.data.plugins

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import ani.saikou2.data.database.AppDatabase
import ani.saikou2.reference.asyncMap
import ani.saikou2.reference.client
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okio.buffer
import okio.sink
import java.io.File


sealed class PluginException : Exception() {
    class UnmetDependencies(val packageName: String, val dependencies: List<String>) : PluginException()
    class HasDependees(val packageName: String, val dependees: List<String>) : PluginException()
}

class PluginManager(
    private val database: AppDatabase,
    private val httpClient: OkHttpClient,
    @ApplicationContext private val appContext: Context,
){
    private val packageDao = database.packageDao()
    private val extractorDao = database.extractorDao()
    private val dependencyDao = database.dependencyDao()
    private val parserDao = database.parserDao()

    val allPackages = packageDao.listAllasFlow()

    fun deletePackage(packageWithPlugins: PackageWithPlugins) =
        packageDao.delete(packageWithPlugins.packageItem)

    suspend fun reinstallPackage(packageWithPlugins: PackageWithPlugins) {
        packageDao.delete(packageWithPlugins.packageItem)
        startDownload(packageWithPlugins.packageItem.downloadUrl)
    }

    private fun insertParser(parser: Parser, dependencies: List<Dependency>) {
        try {
            database.runInTransaction {
                parserDao.insert(parser)
                dependencyDao.insertMultiple(dependencies)
            }
        } catch (exception : SQLiteConstraintException) {
            if (exception.localizedMessage == "FOREIGN KEY constraint failed (code 787 SQLITE_CONSTRAINT_FOREIGNKEY)")
                throw PluginException.UnmetDependencies(
                    parser.className,
                    dependencies.map { it.extractorName })

            throw exception
        }
    }

    private fun removeExtractor(extractor: Extractor){
        try {
            extractorDao.delete(extractor)
        } catch (exception : SQLiteConstraintException) {
            val dependees = dependencyDao.getDependeesOnExtractor(extractor.className)
            if (exception.localizedMessage == "FOREIGN KEY constraint failed (code 1811 SQLITE_CONSTRAINT_FOREIGNKEY)")
                throw PluginException.HasDependees(
                    extractor.className,
                    dependees.map { it.parserName })

            throw exception
        }
    }

    // download and install package
    suspend fun startDownload(url: String) {
        val jsonBody = client.get(url).parsed<PackageJsonResponse>()
        val multiExceptions = mutableListOf<Exception>()
        // starts a transaction for adding PACKAGE FIRST
        // and then the plugins after the JAR is downloaded
        // only commits to DB if no exceptions are thrown
        packageDao.insert(
            Package(
                packageName = jsonBody.packageName,
                packageClassName = jsonBody.packageClassName,
                downloadUrl = url
            )
        )

        jsonBody.extractors.asyncMap {
            val folder = File(appContext.getExternalFilesDir(null)!!.absolutePath)

            val response = client.get(it.downloadUrl)

            if (!folder.exists()) folder.mkdirs()

            File("${folder.path}/${it.className}.jar")
                .sink().buffer().apply {
                    writeAll(response.body.source())
                    close()
                }
            extractorDao.insert(it.toExtractor(jsonBody.packageClassName))
        }

        jsonBody.parsers.asyncMap { parser ->
            val folder = File(appContext.getExternalFilesDir(null)!!.absolutePath)

            val response = client.get(parser.downloadUrl)

            if (!folder.exists()) folder.mkdirs()

            File("${folder.path}/${parser.className}.jar")
                .sink().buffer().apply {
                    writeAll(response.body.source())
                    close()
                }

            insertParser(
                parser.toParser(jsonBody.packageClassName),
                parser.dependencies.map {
                    Dependency(parserName = parser.className, extractorName = it)
                }
            )
        }
    }
}