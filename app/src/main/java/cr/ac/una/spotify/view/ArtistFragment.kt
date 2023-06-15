package cr.ac.una.spotify.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import cr.ac.una.spotify.R
import cr.ac.una.spotify.adapter.TopTracksAdapter
import cr.ac.una.spotify.databinding.FragmentArtistBinding
import cr.ac.una.spotify.entity.TopTrack
import cr.ac.una.spotify.entity.Track
import cr.ac.una.spotify.viewModel.TrackViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL


class ArtistFragment : Fragment() {
    private var _binding: FragmentArtistBinding? = null
    private val binding get() = _binding!!
    private lateinit var trackViewModel: TrackViewModel

    private lateinit var artistId: String
    private lateinit var topTracks: List<TopTrack>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        artistId = arguments?.getString("artistID").toString()

//        Toast.makeText(context, "Artist ID: $artistId", Toast.LENGTH_LONG).show()

        trackViewModel = ViewModelProvider(requireActivity()).get(TrackViewModel::class.java)

        _binding = FragmentArtistBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listTopTracks =
            view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.topTracks)

        topTracks = mutableListOf<TopTrack>()

        var adapter = TopTracksAdapter(topTracks as ArrayList<TopTrack>)

        listTopTracks.adapter = adapter

        listTopTracks.layoutManager = LinearLayoutManager(requireContext())

        listTopTracks.setHasFixedSize(true)

        trackViewModel.artist.observe(viewLifecycleOwner) {
            println("ARTIST FROM FRAGMENT: $it")

            CoroutineScope(Dispatchers.Main).launch {
                val bitmap =
                    withContext(Dispatchers.IO) { loadImage(it.images[1].url.toString()) }
                binding.artistImage.setImageBitmap(bitmap)
            }

            binding.artistName.text = it.name
        }

        trackViewModel.topTracks.observe(viewLifecycleOwner) {
            println("TOP TRACKS FROM FRAGMENT: $it")
//            topTracks = it
//            adapter = TopTracksAdapter(topTracks as ArrayList<TopTrack>)
//            listTopTracks.adapter = adapter
            adapter.updateData(it as ArrayList<TopTrack>)
            topTracks = it
        }

        viewLifecycleOwner.lifecycleScope.launch {
            trackViewModel.startLoadingArtist(artistId)!!
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