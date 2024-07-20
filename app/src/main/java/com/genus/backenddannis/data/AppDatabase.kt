package com.genus.backenddannis.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.genus.backenddannis.data.dao.*
import com.genus.backenddannis.data.entity.*

@Database(entities = [User::class, Pendapatan::class, Pengeluaran::class, Kategori::class, Transaksi::class, Transfer::class], version = 7)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun pendapatanDao(): PendapatanDao
    abstract fun pengeluaranDao(): PengeluaranDao
    abstract fun kategoriDao(): KategoriDao
    abstract fun transaksiDao(): TransaksiDao
    abstract fun transferDao(): TransferDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_6_7)
                    .fallbackToDestructiveMigrationOnDowngrade() // Opsional, menghindari penurunan versi yang tidak aman
                    .allowMainThreadQueries() // Mengizinkan query di main thread (sebaiknya dihindari di produksi)
                    .build()
                INSTANCE = instance
                return instance
            }
        }

        // Migrasi dari versi 6 ke versi 7
        private val MIGRATION_6_7: Migration = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Menambahkan tabel transfer_table pada migrasi
                database.execSQL("CREATE TABLE IF NOT EXISTS `transfer_table` (`tid` INTEGER PRIMARY KEY AUTOINCREMENT, `tanggalTransfer` INTEGER, `jumlahTransfer` REAL NOT NULL, `deskripsiTransfer` TEXT NOT NULL, `kategoriTransfer` TEXT NOT NULL, `kategoriDompet` TEXT NOT NULL)")
            }
        }
    }
}
