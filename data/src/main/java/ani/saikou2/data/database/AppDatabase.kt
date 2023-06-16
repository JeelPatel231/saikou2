package ani.saikou2.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ani.saikou2.data.plugins.Package
import ani.saikou2.data.plugins.Dependency
import ani.saikou2.data.plugins.DependencyDao
import ani.saikou2.data.plugins.Extractor
import ani.saikou2.data.plugins.ExtractorDao
import ani.saikou2.data.plugins.PackageDao
import ani.saikou2.data.plugins.Parser
import ani.saikou2.data.plugins.ParserDao
import ani.saikou2.data.tracker.TrackerDao
import ani.saikou2.data.tracker.TrackerData

@Database(entities = [
    Package::class,
    Extractor::class,
    Parser::class,
    Dependency::class,
    TrackerData::class
],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun packageDao(): PackageDao
    abstract fun extractorDao(): ExtractorDao
    abstract fun parserDao(): ParserDao
    abstract fun dependencyDao() : DependencyDao
    abstract fun trackerDao() : TrackerDao
}