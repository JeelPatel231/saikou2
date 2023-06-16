package ani.saikou2.data.plugins

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

/*
* TWO MAIN TABLES : extractor and parser
* ONE more table to keep track of plugin in a package : package table
* this holds nothing other than the meta data of the package
*
* 1 Plugin belongs to package ( extractor/parser )
* extractor owner -> foreign key to package
* parser owner -> foreign key to package
*
*
* 1 plugin (single entity) can depend on another plugin, MUST BE AN EXTRACTOR DEPENDENCY
* 1 parser must not depend on another parser
*
* */

@Entity(tableName = "packages")
data class Package(
    @PrimaryKey
    val packageClassName: String,
    val packageName: String,
    val downloadUrl: String,
)

data class PackageWithPlugins(
    @Embedded val packageItem: Package,
    @Relation(
        parentColumn = "packageClassName",
        entityColumn = "packageName",
        entity = Extractor::class
    )
    val extractors: List<Extractor>,
    @Relation(
        parentColumn = "packageClassName",
        entityColumn = "packageName",
        entity = Parser::class
    )
    val parsers: List<Parser>
)

@Entity(tableName = "extractors", foreignKeys = [
    ForeignKey(
        entity = Package::class,
        parentColumns = ["packageClassName"],
        childColumns = ["packageName"],
        onDelete = ForeignKey.CASCADE
    )
])
data class Extractor(
    @PrimaryKey
    val className: String,
    val packageName: String,
    val downloadUrl: String,
    val versionNumber: Int,
    val iconUrl: String
)

@Entity(tableName = "parsers", foreignKeys = [
    ForeignKey(
        entity = Package::class,
        parentColumns = ["packageClassName"],
        childColumns = ["packageName"],
        onDelete = ForeignKey.CASCADE
    )
])
data class Parser(
    @PrimaryKey
    val className: String,
    val packageName: String,
    val downloadUrl: String,
    val versionNumber: Int,
    val iconUrl: String
)

@Entity(tableName = "dependencies",
    primaryKeys = ["parserName", "extractorName"],
    foreignKeys = [
        ForeignKey(
            entity = Parser::class,
            parentColumns = ["className"],
            childColumns = ["parserName"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Extractor::class,
            parentColumns = ["className"],
            childColumns = ["extractorName"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class Dependency(
    @ColumnInfo(index = true)
    val parserName: String,
    @ColumnInfo(index = true)
    val extractorName: String
)

@Serializable
data class PackageJsonResponse(
    val packageName: String,
    val packageClassName: String,
    val pluginsURL: String,
    val parsers: List<ParserJsonResponse>,
    val extractors: List<ExtractorJsonResponse>,
)

@Serializable
data class ExtractorJsonResponse(
    val className: String,
    val downloadUrl: String,
    val versionNumber: Int,
    val iconUrl: String
){
    fun toExtractor(packageName: String) =
        Extractor(
            className = className,
            packageName = packageName,
            downloadUrl = downloadUrl,
            versionNumber = versionNumber,
            iconUrl = iconUrl
        )
}

@Serializable
data class ParserJsonResponse(
    val className: String,
    val downloadUrl: String,
    val versionNumber: Int,
    val iconUrl: String,
    val dependencies: List<String>,
){
    fun toParser(packageName: String) =
        Parser(
            className = className,
            packageName = packageName,
            downloadUrl = downloadUrl,
            versionNumber = versionNumber,
            iconUrl = iconUrl
        )
}


@Dao
interface PackageDao {
    @Query("SELECT * FROM packages")
    fun listAllasFlow(): Flow<List<PackageWithPlugins>>

    @Insert
    fun insert(packageItem: Package)

    @Transaction
    @Query("SELECT * FROM packages WHERE packageClassName = :packageClassName")
    fun getPackageWithPlugins(packageClassName: String): PackageWithPlugins?

    @Delete
    fun delete(packageItem: Package): Int
}

@Dao
interface ExtractorDao {
    @Query("SELECT * FROM extractors")
    suspend fun listAll(): List<Extractor>

    @Insert
    fun insert(extractor: Extractor)

    @Insert
    fun insertAll(extractors: List<Extractor>)

    @Delete
    fun delete(extractor: Extractor) : Int

    @Query("SELECT * FROM extractors WHERE className = :className LIMIT 1")
    fun getByClassName(className : String) : Extractor?
}

@Dao
interface ParserDao {
    @Query("SELECT * FROM parsers")
    suspend fun listAll(): List<Parser>

    @Insert
    fun insert(parser: Parser)

    @Insert
    fun insertAll(parsers: List<Parser>)

    @Delete
    fun delete(parser: Parser) : Int

    @Query("SELECT * FROM parsers WHERE className = :className LIMIT 1")
    fun getByClassName(className : String) : Parser?
}

@Dao
interface DependencyDao {
    @Insert
    fun insert(dependency: Dependency)

    @Insert
    fun insertMultiple(dependencies: List<Dependency>)

    @Query("SELECT * FROM DEPENDENCIES where parserName = :parserClassName")
    fun getAllParserDependencies(parserClassName: String): List<Dependency>

    @Query("SELECT * FROM DEPENDENCIES where extractorName = :extractorClassName")
    fun getDependeesOnExtractor(extractorClassName: String): List<Dependency>

    @Query("SELECT * FROM DEPENDENCIES where extractorName = :extractorClassName AND parserName = :parserClassName")
    fun getDependency(extractorClassName: String, parserClassName: String): Dependency?

    @Delete
    fun delete(dependency: Dependency) : Int
}