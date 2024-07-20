package com.genus.backenddannis.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Date

@Entity(tableName = "transfer_table")
data class Transfer(
    @PrimaryKey(autoGenerate = true) var tid: Int? = null,
    @ColumnInfo(name = "tanggalTransfer") var tanggalT: Date?,
    @ColumnInfo(name = "jumlahTransfer") val jumlahT: Double,
    @ColumnInfo(name = "deskripsiTransfer") val deskripsiT: String,
    @ColumnInfo(name = "kategoriDari") val kategoriDari: String,
    @ColumnInfo(name = "kategoriKepada") val kategoriKepada: String
) : Serializable
