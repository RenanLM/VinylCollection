package br.com.renan.vinylcollection.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = VinylRecord::class,
            parentColumns = ["id"],
            childColumns = ["vinylRecordId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("vinylRecordId")]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val vinylRecordId: Int,
    val description: String,
    val isCompleted: Boolean = false
)