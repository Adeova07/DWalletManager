package com.genus.backenddannis.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.genus.backenddannis.data.entity.Transfer
import java.util.Date

@Dao
interface TransferDao {
    @Query("SELECT * FROM transfer_table WHERE strftime('%m', tanggalTransfer) = :bulan AND strftime('%Y', tanggalTransfer) = :tahun")
    fun getAllByMonthAndYear(bulan: String, tahun: String): List<Transfer>

    @Query("SELECT * FROM transfer_table")
    fun getAll(): List<Transfer>

    @Query("SELECT * FROM transfer_table WHERE tanggalTransfer BETWEEN :startDate AND :endDate")
    fun getAllByDateRange(startDate: Date, endDate: Date): List<Transfer>

    @Query("SELECT * FROM transfer_table WHERE kategoriDari = :kategori OR kategoriKepada = :kategori")
    fun getAllByKategori(kategori: String): List<Transfer>

    @Insert
    fun insert(vararg transfer: Transfer)

    @Delete
    fun delete(transfer: Transfer)

    @Update
    fun updateTransfer(transfer: Transfer)

    @Query("SELECT * FROM transfer_table")
    fun getAllTransfers(): List<Transfer>
}
