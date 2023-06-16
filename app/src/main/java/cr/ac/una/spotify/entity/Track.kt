package cr.ac.una.spotify.entity

data class Track(
    val name: String,
    val album: Album,
    val uri: String,
    val artists: ArrayList<Artist>,
    val preview_url: String?,
)