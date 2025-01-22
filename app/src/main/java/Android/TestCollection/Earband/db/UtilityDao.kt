package Android.TestCollection.Earband.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface UtilityDao {

    @Upsert
    fun upsertUtility(utilEntity: UtilityEntity)

    @Query("SELECT * FROM UtilityEntity WHERE id = 1 LIMIT 1")
    fun getUtilityEntity(): UtilityEntity?

}