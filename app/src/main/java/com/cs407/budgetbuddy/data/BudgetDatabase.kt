package com.cs407.budgetbuddy.data
import androidx.room.RoomDatabase
import android.content.ClipData
import android.content.Context
import androidx.core.graphics.toColorLong
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.Transaction
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import androidx.room.Upsert
import java.util.Date
import androidx.room.Database
import androidx.room.OnConflictStrategy



@Entity(
    tableName = "User",
    indices = [Index(value = ["username"])]
)
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Int = 0,
    @ColumnInfo(name = "username") val username:String?
)

@Entity(
    tableName = "BudgetTransaction"
)
data class BudgetTransaction (
    @PrimaryKey(autoGenerate = true) val transactionId: Int = 0,
    @ColumnInfo(name = "name") val name:Int?,
    @ColumnInfo(name = "date") val date:Date?,
    @ColumnInfo(name = "amount") val amount:Double?,
    @ColumnInfo(name = "category") val category:String?,
    @ColumnInfo(name = "merchant") val merchant:String?
)

@Entity(
    tableName = "UserTransaction",
    primaryKeys = ["userId", "transactionId"],
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["userId"], childColumns = ["userId"]),
        ForeignKey(entity = Transaction::class, parentColumns = ["transactionId"], childColumns = ["transactionId"])
    ]
)
data class UserTransaction (
    @ColumnInfo(name = "userId") val userId:Int,
    @ColumnInfo(name = "transactionId") val transactionId:Int
)

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Query("SELECT userId FROM User WHERE username = :username")
    suspend fun getUserId(username: String): Long
}

@Dao
interface TransactionDao {

}

@Database(entities = [User::class, BudgetTransaction::class, UserTransaction::class], version = 1, exportSchema = false)
abstract class BudgetDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: BudgetDatabase? = null
        fun getDatabase(context: Context): BudgetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BudgetDatabase::class.java,
                    "budget_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}