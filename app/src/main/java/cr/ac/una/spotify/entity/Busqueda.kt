package cr.ac.una.spotify.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Busqueda(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    val text:String,
    val date: Date,
)