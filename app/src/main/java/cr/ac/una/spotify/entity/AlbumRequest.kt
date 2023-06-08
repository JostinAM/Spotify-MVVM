package cr.ac.una.spotify.entity

data class AlbumRequest(
    val name: String,
    val images: ArrayList<Image>,
    val artists: ArrayList<Artist>,
    val release_date: String,
    val genres: ArrayList<String> = ArrayList(),
    val tracks: Tracks
)