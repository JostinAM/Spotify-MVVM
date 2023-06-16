package cr.ac.una.spotify.viewModel

import android.util.Base64
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cr.ac.una.spotify.entity.*
import cr.ac.una.spotify.service.SpotifyService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TrackViewModel : ViewModel() {
    private var _tracks: MutableLiveData<List<Track>> = MutableLiveData()
    var tracks: MutableLiveData<List<Track>> = _tracks
//    lateinit var apiService : TrackDAO

    private var _album: MutableLiveData<Album> = MutableLiveData()
    var album: MutableLiveData<Album> = _album

    private var _genres: MutableLiveData<List<String>> = MutableLiveData()
    var genres: MutableLiveData<List<String>> = _genres

    private var _tracksForAlbum: MutableLiveData<List<Track>> = MutableLiveData()
    var tracksForAlbum: MutableLiveData<List<Track>> = _tracksForAlbum

    private var _artist: MutableLiveData<ArtistRequest> = MutableLiveData()
    var artist: MutableLiveData<ArtistRequest> = _artist

    private var _topTracks: MutableLiveData<List<TopTrack>> = MutableLiveData()
    var topTracks: MutableLiveData<List<TopTrack>> = _topTracks

    private var _relatedArtists: MutableLiveData<List<RelatedArtist>> = MutableLiveData()
    var relatedArtists: MutableLiveData<List<RelatedArtist>> = _relatedArtists

    private val spotifyServiceToken: SpotifyService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://accounts.spotify.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(SpotifyService::class.java)
    }

    private val spotifyService: SpotifyService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.spotify.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(SpotifyService::class.java)
    }

    suspend fun startLoadingTracks(query: String) {

        _tracks.postValue(listOf())
        var lista = searchTracks(query)

        _tracks.postValue(lista)
    }

    fun startLoadingAlbum(id: String) {
        requestAlbum(id)
    }

    fun startLoadingArtist(id: String) {
        requestArtist(id)
    }

    fun startLoadingRelated(id: String){
        requestRelated(id)
    }


    private fun displayErrorMessage(s: String) {
        System.out.println("Error: " + s)
    }

    private fun searchTracks(query: String): MutableList<Track> {
        val clientId = "f13969da015a4f49bb1f1edef2185d4e"
        val clientSecret = "e3077426f4714315937111d5e82cd918"
        val base64Auth =
            Base64.encodeToString("$clientId:$clientSecret".toByteArray(), Base64.NO_WRAP)

        val tokenRequest = spotifyServiceToken.getAccessToken(
            "Basic $base64Auth",
            "client_credentials"
        )

        var tracks = mutableListOf<Track>()

        tokenRequest.enqueue(object : Callback<AccessTokenResponse> {
            override fun onResponse(
                call: Call<AccessTokenResponse>,
                response: Response<AccessTokenResponse>
            ) {


                if (response.isSuccessful) {


                    val accessTokenResponse = response.body()
                    val accessToken = accessTokenResponse?.accessToken

//                    println("Access Token: $accessToken")

                    if (accessToken != null) {

                        val searchRequest = spotifyService.searchTrack("Bearer $accessToken", query)
                        searchRequest.enqueue(object : Callback<TrackResponse> {
                            override fun onResponse(
                                call: Call<TrackResponse>,
                                response: Response<TrackResponse>
                            ) {
                                if (response.isSuccessful) {
                                    val trackResponse = response.body()

                                    if (trackResponse != null && trackResponse.tracks.items.isNotEmpty()) {
//                                        tracks = trackResponse.tracks.items as MutableList<Track>

                                        for (track in trackResponse!!.tracks.items) {
//                                            System.out.println(track.name + track.album.name)
//                                            Log.d("Track", track.name + track.album.name)
                                            val newTrack = Track(
                                                track.name,
                                                Album(
                                                    track.album.name,
                                                    track.album.images,
                                                    track.artists,
                                                    track.album.release_date,
                                                    ArrayList<String>(),
                                                    arrayListOf<Track>(),
                                                    track.album.id
                                                ),
                                                track.uri,
                                                track.artists,
                                                track.preview_url
                                            )

                                            tracks.add(newTrack)

                                        }

                                        System.out.println("First track: " + tracks[0])


                                    } else {
                                        displayErrorMessage("No se encontraron canciones.")
                                    }
                                } else {
                                    System.out.println("Mensaje:    " + response.raw())
                                    displayErrorMessage("Error en la respuesta del servidor.")
                                }
                            }

                            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                                displayErrorMessage("Error en la solicitud de búsqueda.")
                            }
                        })
                    } else {
                        displayErrorMessage("Error al obtener el accessToken.")
                    }
                } else {
                    System.out.println("Mensaje:    " + response.raw())
                    displayErrorMessage("Error en la respuesta del servidor.")
                }
            }

            override fun onFailure(call: Call<AccessTokenResponse>, t: Throwable) {
                displayErrorMessage("Error en la solicitud de accessToken.")
            }
        })

        return tracks
    }

    private fun requestAlbum(id: String) {
        val clientId = "f13969da015a4f49bb1f1edef2185d4e"
        val clientSecret = "e3077426f4714315937111d5e82cd918"
        val base64Auth =
            Base64.encodeToString("$clientId:$clientSecret".toByteArray(), Base64.NO_WRAP)

        val tokenRequest = spotifyServiceToken.getAccessToken(
            "Basic $base64Auth",
            "client_credentials"
        )

        var qAlbum = Album("", ArrayList(), ArrayList(), "", ArrayList(), ArrayList())

        tokenRequest.enqueue(object : Callback<AccessTokenResponse> {
            override fun onResponse(
                call: Call<AccessTokenResponse>,
                response: Response<AccessTokenResponse>
            ) {
                if (response.isSuccessful) {
                    val accessTokenResponse = response.body()
                    val accessToken = accessTokenResponse?.accessToken

                    if (accessToken != null) {

                        val searchRequest = spotifyService.getAlbum("Bearer $accessToken", id)

                        searchRequest.enqueue(object : Callback<AlbumRequest> {
                            override fun onResponse(
                                call: Call<AlbumRequest>,
                                response: Response<AlbumRequest>
                            ) {
                                if (response.isSuccessful) {
                                    val albumResponse = response.body()

                                    if (albumResponse != null) {
                                        var tracks = arrayListOf<Track>()


                                        for (track in albumResponse.tracks.items) {
                                            val newTrack = Track(
                                                track.name,
                                                qAlbum,
                                                track.uri,
                                                track.artists,
                                                track.preview_url

                                            )

                                            tracks.add(newTrack)

                                        }

                                        qAlbum = Album(
                                            albumResponse.name,
                                            albumResponse.images,
                                            albumResponse.artists,
                                            albumResponse.release_date,
                                            albumResponse.genres,
                                            tracks,
                                        )

                                        _album.postValue(qAlbum)
                                        _genres.postValue(albumResponse.genres)
                                        _tracksForAlbum.postValue(tracks)

                                        println("FINAL GENRES: " + albumResponse.genres)
                                        println("FINAL TRACKS: " + tracks)


                                    } else {
                                        displayErrorMessage("No se encontró el album.")
                                    }
                                } else {
                                    println("Mensaje:    " + response.raw())
                                    displayErrorMessage("Error en la respuesta del servidor.")
                                }
                            }

                            override fun onFailure(call: Call<AlbumRequest>, t: Throwable) {
                                displayErrorMessage("Error en la solicitud de búsqueda.")
                                println("Error: " + t.message)
                            }
                        })

                    } else {
                        displayErrorMessage("Error al obtener el accessToken.")
                    }
                } else {
                    System.out.println("Mensaje:    " + response.raw())
                    displayErrorMessage("Error en la respuesta del servidor.")
                }
            }

            override fun onFailure(call: Call<AccessTokenResponse>, t: Throwable) {
                displayErrorMessage("Error en la solicitud de accessToken.")
            }
        })


    }

    private fun requestArtist(id: String) {
        val clientId = "f13969da015a4f49bb1f1edef2185d4e"
        val clientSecret = "e3077426f4714315937111d5e82cd918"
        val base64Auth =
            Base64.encodeToString("$clientId:$clientSecret".toByteArray(), Base64.NO_WRAP)

        val tokenRequest = spotifyServiceToken.getAccessToken(
            "Basic $base64Auth",
            "client_credentials"
        )

        var tracks = mutableListOf<TopTrack>()
//        _topTracks.postValue(listOf())

        tokenRequest.enqueue(object : Callback<AccessTokenResponse> {
            override fun onResponse(
                call: Call<AccessTokenResponse>,
                response: Response<AccessTokenResponse>
            ) {
                if (response.isSuccessful) {
                    val accessTokenResponse = response.body()
                    val accessToken = accessTokenResponse?.accessToken

                    if (accessToken != null) {

                        val searchRequest = spotifyService.getArtist("Bearer $accessToken", id)

                        searchRequest.enqueue(object : Callback<ArtistRequest> {
                            override fun onResponse(
                                call: Call<ArtistRequest>,
                                response: Response<ArtistRequest>
                            ) {
                                if (response.isSuccessful) {
                                    val artistResponse = response.body()

                                    if (artistResponse != null) {

                                        val qArtist = ArtistRequest(
                                            artistResponse.id,
                                            artistResponse.images,
                                            artistResponse.name,
                                        )

                                        _artist.postValue(qArtist)

                                        println("FINAL GENRES: $qArtist")
                                        println("FINAL TRACKS: " + tracks)


                                    } else {
                                        displayErrorMessage("No se encontró el album.")
                                    }
                                } else {
                                    println("Mensaje:    " + response.raw())
                                    displayErrorMessage("Error en la respuesta del servidor.")
                                }
                            }

                            override fun onFailure(call: Call<ArtistRequest>, t: Throwable) {
                                displayErrorMessage("Error en la solicitud de búsqueda.")
                                println("Error: " + t.message)
                            }
                        })

                        val searchTopTracks = spotifyService.getTopTracks("Bearer $accessToken", id)

                        searchTopTracks.enqueue(object : Callback<TopTracksResponse> {
                            override fun onResponse(
                                call: Call<TopTracksResponse>,
                                response: Response<TopTracksResponse>
                            ) {
                                if (response.isSuccessful) {
                                    val topTracksResponse = response.body()

                                    if (topTracksResponse != null && topTracksResponse.tracks.isNotEmpty()) {

                                        println("TOP TRACK BEFORE FOR: ${topTracksResponse.tracks}")

                                        for (track in topTracksResponse!!.tracks) {

                                            var prev = ""
                                            if (track.preview_url != null) {
                                                prev = track.preview_url
                                            }

                                            var newTrack = TopTrack(
                                                Album(
                                                    track.album.name,
                                                    track.album.images,
                                                    track.album.artists,
                                                    "",
                                                    ArrayList(),
                                                    ArrayList()
                                                ),
                                                track.name,
                                                track.popularity,
                                                prev,
                                            )

                                            tracks.add(newTrack)
                                        }

                                        _topTracks.postValue(tracks)

                                        println("TOP TRACKS FROM VIEWMODEL: $tracks")


                                    } else {
                                        displayErrorMessage("No se encontró el album.")
                                    }
                                } else {
                                    println("Mensaje:    " + response.raw())
                                    displayErrorMessage("Error en la respuesta del servidor.")
                                }
                            }

                            override fun onFailure(call: Call<TopTracksResponse>, t: Throwable) {
                                displayErrorMessage("Error en la solicitud de búsqueda.")
                                println("Error: " + t.message)
                            }
                        })

                    } else {
                        displayErrorMessage("Error al obtener el accessToken.")
                    }
                } else {
                    System.out.println("Mensaje:    " + response.raw())
                    displayErrorMessage("Error en la respuesta del servidor.")
                }
            }

            override fun onFailure(call: Call<AccessTokenResponse>, t: Throwable) {
                displayErrorMessage("Error en la solicitud de accessToken.")
            }
        })
    }

    private fun requestRelated(id: String) {
        val clientId = "f13969da015a4f49bb1f1edef2185d4e"
        val clientSecret = "e3077426f4714315937111d5e82cd918"
        val base64Auth =
            Base64.encodeToString("$clientId:$clientSecret".toByteArray(), Base64.NO_WRAP)

        val tokenRequest = spotifyServiceToken.getAccessToken(
            "Basic $base64Auth",
            "client_credentials"
        )

       // var tracks = mutableListOf<TopTrack>()
//        _topTracks.postValue(listOf())

        tokenRequest.enqueue(object : Callback<AccessTokenResponse> {
            override fun onResponse(
                call: Call<AccessTokenResponse>,
                response: Response<AccessTokenResponse>
            ) {
                if (response.isSuccessful) {
                    val accessTokenResponse = response.body()
                    val accessToken = accessTokenResponse?.accessToken

                    if (accessToken != null) {

                        val searchRequest = spotifyService.getRelatedArtists("Bearer $accessToken", id)

                        searchRequest.enqueue(object : Callback<RelatedArtistsResponse> {
                            override fun onResponse(
                                call: Call<RelatedArtistsResponse>,
                                response: Response<RelatedArtistsResponse>
                            ) {
                                if (response.isSuccessful) {
                                    val artistResponse = response.body()

                                    if (artistResponse != null) {




                                        //val qArtist = ArtistRequest(
                                          //  artistResponse.id,
                                            //artistResponse.images,
                                            //artistResponse.name,
                                        //)

                                        //_artist.postValue(qArtist)


                                        _relatedArtists.postValue(artistResponse.artists)
                                        println("FINAL RLATED: $artistResponse")



                                    } else {
                                        displayErrorMessage("No se encontró el album.")
                                    }
                                } else {
                                    println("Mensaje:    " + response.raw())
                                    displayErrorMessage("Error en la respuesta del servidor.")
                                }
                            }

                            override fun onFailure(call: Call<RelatedArtistsResponse>, t: Throwable) {
                                displayErrorMessage("Error en la solicitud de búsqueda.")
                                println("Error: " + t.message)
                            }
                        })


                    } else {
                        displayErrorMessage("Error al obtener el accessToken.")
                    }
                } else {
                    System.out.println("Mensaje:    " + response.raw())
                    displayErrorMessage("Error en la respuesta del servidor.")
                }
            }

            override fun onFailure(call: Call<AccessTokenResponse>, t: Throwable) {
                displayErrorMessage("Error en la solicitud de accessToken.")
            }
        })
    }
}

