package br.com.renan.vinylcollection.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vinyl_records")
data class VinylRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val discogsId: Int?,
    val title: String,
    val artist: String,
    val coverUrl: String?,
    val barcode: String?,
    val condition: String?,
    val year: String?,
    val genre: String?
)