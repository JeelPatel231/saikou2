package ani.saikou2.data

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.net.Uri
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import java.io.IOException
import androidx.test.ext.junit.runners.AndroidJUnit4
import ani.saikou2.data.database.AppDatabase
import ani.saikou2.data.plugins.Dependency
import ani.saikou2.data.plugins.DependencyDao
import ani.saikou2.data.plugins.Extractor
import ani.saikou2.data.plugins.ExtractorDao
import ani.saikou2.data.plugins.PackageDao
import ani.saikou2.data.plugins.ParserDao
import ani.saikou2.data.tracker.TrackerDao
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date
import ani.saikou2.data.plugins.Package
import ani.saikou2.data.plugins.Parser
import ani.saikou2.data.tracker.TrackerData
import kotlinx.coroutines.flow.first


@RunWith(AndroidJUnit4::class)
class DatabaseUnitTest {
    private lateinit var packageDao: PackageDao
    private lateinit var extractorDao: ExtractorDao
    private lateinit var parserDao: ParserDao
    private lateinit var dependencyDao: DependencyDao
    private lateinit var trackerDao: TrackerDao
    private lateinit var db: AppDatabase

    private val samplePluginPackage = Package(
        packageClassName = "com.example.parser",
        packageName = "Jeel's Weeb Parser",
        downloadUrl = ""
    )

    private val samplePluginPackage2 = Package(
            packageClassName = "com.example.extractor",
            packageName = "Jeel's Weeb Extractor",
            downloadUrl = ""
        )

    private val sampleParser = Parser(
           "com.example.ServiceName",
           samplePluginPackage.packageClassName,
           "https://example.com/plugin/dexjar.jar",
           1 ,
           "https://icons.com/1.jpeg"
        )

    private val sampleExtractor = Extractor(
           "com.example.Extractor",
           samplePluginPackage2.packageClassName,
           "https://example.com/plugin/dexjar.jar",
           1 ,
           "https://icons.com/1.jpeg"
        )

    // parser depends on extractor
    private val sampleDependency = Dependency(
        parserName = sampleParser.className,
        extractorName = sampleExtractor.className,
    )


    @Before
    fun createDb() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()

        packageDao = db.packageDao()
        dependencyDao = db.dependencyDao()
        extractorDao = db.extractorDao()
        parserDao = db.parserDao()
        trackerDao = db.trackerDao()

    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun addPluginWithoutPackage() {

        // throws foreign key exception because package doesn't exist, so plugin must not be added
        val exception = assertThrows(SQLiteConstraintException::class.java) {
            parserDao.insert(sampleParser)
        }
        // matches the localised message because SQLiteConstraints is a wide exception
        assertEquals("FOREIGN KEY constraint failed (code 787 SQLITE_CONSTRAINT_FOREIGNKEY)", exception.localizedMessage)
    }

    @Test
    fun addPluginAgain() {
        packageDao.insert(samplePluginPackage2)
        extractorDao.insert(sampleExtractor)

        // throws foreign key exception because package doesn't exist, so plugin must not be added
        val exception = assertThrows(SQLiteConstraintException::class.java) {
            extractorDao.insert(sampleExtractor)
        }
        // matches the localised message because SQLiteConstraints is a wide exception
        assertEquals("UNIQUE constraint failed: extractors.className (code 1555 SQLITE_CONSTRAINT_PRIMARYKEY)", exception.localizedMessage)
    }

    @Test
    fun addPluginPackage() {
        // added the plugin package, now its child plugin should enter db
        db.runInTransaction {
            packageDao.insert(samplePluginPackage)
            parserDao.insert(sampleParser)
        }
    }

    @Test
    fun deletePackageAndCheckPlugin(){
        db.runInTransaction {
            packageDao.insert(samplePluginPackage)
            parserDao.insert(sampleParser)
        }

        // only 1 row must be affected, ALSO delete any plugin related to the package
        assertEquals(1, packageDao.delete(samplePluginPackage))

        // the plugin must not exist in db after deleting its package
        assertEquals(null, parserDao.getByClassName(sampleParser.className))
    }

    @Test
    fun addPluginWithUnmetDependency(){
        val exception = assertThrows(SQLiteConstraintException::class.java) {
            db.runInTransaction {
                // success
                packageDao.insert(samplePluginPackage)
                // success
                parserDao.insert(sampleParser)
                // FAIL, because the dependency is unmet, the extractor doesn't exist
                dependencyDao.insert(sampleDependency)
            }
        }

        // matches the localised message because SQLiteConstraints is a wide exception
        assertEquals("FOREIGN KEY constraint failed (code 787 SQLITE_CONSTRAINT_FOREIGNKEY)", exception.localizedMessage)
    }

    @Test
    fun removeExtractorHavingADependees() {
         db.runInTransaction {
             packageDao.insert(samplePluginPackage)
             parserDao.insert(sampleParser)
             packageDao.insert(samplePluginPackage2)
             extractorDao.insert(sampleExtractor)
             dependencyDao.insert(sampleDependency)
        }

        // remove an single extractor having dependees
        val exception = assertThrows(SQLiteConstraintException::class.java){
            extractorDao.delete(sampleExtractor)
        }

        assertEquals(exception.localizedMessage, "FOREIGN KEY constraint failed (code 1811 SQLITE_CONSTRAINT_TRIGGER)")

        // remove whole package having dependees
        val exception2 = assertThrows(SQLiteConstraintException::class.java){
            packageDao.delete(samplePluginPackage2)
        }

        assertEquals(exception2.localizedMessage, "FOREIGN KEY constraint failed (code 1811 SQLITE_CONSTRAINT_TRIGGER)")
    }


    @Test
    fun addPackageFully() {
        db.runInTransaction {
            // install parser package having dependencies
            packageDao.insert(samplePluginPackage)
            parserDao.insert(sampleParser)
            // install the depending package, parser of package1 depends on extractor of package2
            packageDao.insert(samplePluginPackage2)
            extractorDao.insert(sampleExtractor)
            // add the dependency relation in database
            dependencyDao.insert(sampleDependency)
        }
        // ^ we confirm until here that database entry works as expected and relationships hold good

        // remove the PARSER package first, because it has a dependency on EXTRACTOR
        assertEquals(1, packageDao.delete(samplePluginPackage))
        // now we can safely remove extractor because its holding no dependees
        assertEquals(1, packageDao.delete(samplePluginPackage2))

        // assert that neither parser nor extractor or their depending relation remain if we remove the whole package
        assertEquals(null, parserDao.getByClassName(sampleParser.className))
        assertEquals(null, extractorDao.getByClassName(sampleExtractor.className))
        assertEquals(null, dependencyDao.getDependency(sampleExtractor.className, sampleParser.className))
    }

    // TRACKER TESTS
    @Test
    fun makeAnilistTracker() = runBlocking {
        val anilistTracker = TrackerData("auth token", "refresh token", Date().time)
        trackerDao.upsertTracker(anilistTracker)

        // successfully inserted
        assertNotEquals(null, trackerDao.getTrackerData())

        trackerDao.upsertTracker(anilistTracker.copy(accessToken = "auth token 2"))
        // a new entry isnt created, tracker name is primary key
        assertEquals("auth token 2", trackerDao.getTrackerData().first()!!.accessToken)
    }

    @Test
    fun testUri() {
        val uri = Uri.parse("saikou://logintracker/anilist?access_token=mybitchasstoken&token_type=balls")
        println(uri.authority)
        println(uri.path)
        println(uri.getQueryParameter("access_token"))
        println(uri.getQueryParameter("token_type"))
    }

}
