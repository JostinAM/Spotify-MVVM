package cr.ac.una.spotify.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import cr.ac.una.spotify.R
import cr.ac.una.spotify.databinding.FragmentAlbumBinding
import cr.ac.una.spotify.entity.Album
import cr.ac.una.spotify.viewModel.TrackViewModel

class AlbumFragment : Fragment() {

    private var _binding: FragmentAlbumBinding? = null
    private val binding get() = _binding!!
    private lateinit var trackViewModel: TrackViewModel
    private lateinit var album: Album


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val data = arguments?.getString("key")

        println("data: $data")

        _binding = FragmentAlbumBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listGenres =
            view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.albumGenres)
        val listTracks =
            view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.albumTracks)

        val genres = mutableListOf<String>()
        val tracks = mutableListOf<String>()

        
    }


}