package cr.ac.una.spotify.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import cr.ac.una.spotify.databinding.FragmentAlbumBinding
import cr.ac.una.spotify.entity.Album
import cr.ac.una.spotify.viewModel.TrackViewModel
import kotlinx.coroutines.*
import java.net.URL
import cr.ac.una.spotify.R
import cr.ac.una.spotify.adapter.AlbumGenresAdapter
import cr.ac.una.spotify.adapter.AlbumTracksAdapter
import cr.ac.una.spotify.adapter.TrackAdapter
import cr.ac.una.spotify.entity.Track


class AlbumFragment : Fragment() {

    private var _binding: FragmentAlbumBinding? = null
    private val binding get() = _binding!!
    private lateinit var trackViewModel: TrackViewModel
    private lateinit var genres: List<String>
    private lateinit var tracks: List<Track>

    private lateinit var albumId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        albumId = arguments?.getString("albumID").toString()

        println("Album ID: $albumId")

        trackViewModel = ViewModelProvider(requireActivity()).get(TrackViewModel::class.java)


        _binding = FragmentAlbumBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listGenres =
            view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.albumGenres)
        val listTracks =
            view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.albumTracks)

        genres = mutableListOf<String>()
        tracks = mutableListOf<Track>()

        var genresAdapter = AlbumGenresAdapter(genres as ArrayList<String>)

        var tracksAdapter = AlbumTracksAdapter(tracks as ArrayList<Track>)

        listGenres.adapter = genresAdapter

        listTracks.adapter = tracksAdapter

        listGenres.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        listTracks.layoutManager = LinearLayoutManager(requireContext())

        listGenres.setHasFixedSize(true)
        listTracks.setHasFixedSize(true)


        trackViewModel.album.observe(viewLifecycleOwner) { album ->
            Log.d("AlbumFragment", "Album Name: ${album.name}")

            CoroutineScope(Dispatchers.Main).launch {
                val bitmap =
                    withContext(Dispatchers.IO) { loadImage(album.images[1].url.toString()) }
                binding.imageView.setImageBitmap(bitmap)
            }

            binding.albumName.text = album.name
            binding.albumArtist.text = album.artists[0].name
            binding.albumDate.text = album.release_date
        }

        trackViewModel.genres.observe(viewLifecycleOwner) { elementos ->
            genresAdapter.updateData(elementos as ArrayList<String>)
            genres = elementos
        }

        trackViewModel.tracksForAlbum.observe(viewLifecycleOwner) { elementos ->
            println("ELEMENTOS: $elementos")
            tracksAdapter.updateData(elementos as ArrayList<Track>)
            tracks = elementos
        }

        viewLifecycleOwner.lifecycleScope.launch {
            trackViewModel.startLoadingAlbum(albumId)!!
        }
    }

    fun loadImage(imageUrl: String): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection()
            connection.doInput = true
            connection.connect()
            val inputStream = connection.getInputStream()
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}