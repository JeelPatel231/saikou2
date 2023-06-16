package ani.saikou2.data.tracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.TypeConverter
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import java.util.Date

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }
}

// this makes the primary key concrete and allows only 1 entry per name
enum class TrackingService {
    Anilist,
//    MAL,
//    Kitsu,
    // extras idk
}

@Entity
data class TrackerData(
    val accessToken: String,
    val refreshToken: String?,
    val expiration: Long,
){
    @PrimaryKey
    var name = TrackingService.Anilist
        // override setting of the variable, MUST NOT CHANGE
        set(_) = Unit
}

@Dao
interface TrackerDao {
    @Upsert
    fun upsertTracker(tracker: TrackerData)

    @Query("SELECT * FROM trackerdata LIMIT 1")
    fun getTrackerData(): Flow<TrackerData?>

    @Delete
    fun deleteTracker(tracker: TrackerData)
}
