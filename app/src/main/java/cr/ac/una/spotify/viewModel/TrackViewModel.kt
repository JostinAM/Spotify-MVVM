package cr.ac.una.spotify.viewModel

import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cr.ac.una.spotify.entity.*
import cr.ac.una.spotify.service.SpotifyService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
                                                track.artists
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

    private fun requestAlbum(id: String): Album {
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

                                        println("AlbumResponse: $albumResponse")

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

        return qAlbum

    }


}

