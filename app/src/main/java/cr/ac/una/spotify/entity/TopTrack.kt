package cr.ac.una.spotify.entity

data class TopTrack(
    val album: Album,
    val name: String,
    val popularity: Int,
    val preview_url: String = "",
)