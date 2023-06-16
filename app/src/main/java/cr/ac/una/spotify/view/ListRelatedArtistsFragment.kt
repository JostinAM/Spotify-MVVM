package cr.ac.una.spotify.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import cr.ac.una.spotify.R
import cr.ac.una.spotify.adapter.RelatedArtistsAdapter
import cr.ac.una.spotify.databinding.FragmentListRelatedArtistsBinding
import cr.ac.una.spotify.entity.RelatedArtist
import cr.ac.una.spotify.viewModel.TrackViewModel
import kotlinx.coroutines.launch


class ListRelatedArtistsFragment : Fragment() {

    private var _binding: FragmentListRelatedArtistsBinding? = null
    private val binding get() = _binding!!
    private lateinit var trackViewModel: TrackViewModel

    private lateinit var related: List<RelatedArtist>

    private lateinit var artistId: String



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        artistId = arguments?.getString("artistID").toString()

        println("RELATED ID: $artistId")

        trackViewModel = ViewModelProvider(requireActivity()).get(TrackViewModel::class.java)

        _binding = FragmentListRelatedArtistsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listRelated = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.relatedArtistsList)

        related = mutableListOf<RelatedArtist>()

        var adapter = RelatedArtistsAdapter(related as ArrayList<RelatedArtist>)

        listRelated.adapter = adapter

        listRelated.layoutManager = LinearLayoutManager(requireContext())



        listRelated.setHasFixedSize(true)

        trackViewModel.relatedArtists.observe(viewLifecycleOwner){
            adapter.updateData(it as ArrayList<RelatedArtist>)

            related = it
        }

        viewLifecycleOwner.lifecycleScope.launch {
            trackViewModel.startLoadingRelated(artistId)!!
        }

    }


}