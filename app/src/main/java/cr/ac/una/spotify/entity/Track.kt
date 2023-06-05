package cr.ac.una.spotify.entity

data class Track(
    val name: String,
    val album: Album,
    val uri: String,
    //
    val imageUrl: String,
    val artists: ArrayList<Artist>
)