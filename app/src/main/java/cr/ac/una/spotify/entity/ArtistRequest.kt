package cr.ac.una.spotify.entity

data class ArtistRequest(
    val id: String,
    val images: ArrayList<Image>,
    val name: String
)