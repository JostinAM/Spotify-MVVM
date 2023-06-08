package cr.ac.una.spotify.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cr.ac.una.spotify.R
import cr.ac.una.spotify.adapter.TrackAdapter
import cr.ac.una.spotify.databinding.FragmentListaTracksBinding
import cr.ac.una.spotify.entity.Track
import cr.ac.una.spotify.viewModel.TrackViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ListaTracksFragment : Fragment() {

    private var _binding: FragmentListaTracksBinding? = null
    private val binding get() = _binding!!
    private lateinit var trackViewModel: TrackViewModel
    private lateinit var tracks: List<Track>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentListaTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<RecyclerView>(R.id.list_view)
        tracks = mutableListOf<Track>()
        var adapter = TrackAdapter(
            tracks as ArrayList<Track>,
            object : TrackAdapter.OptionsMenuClickListener {
                override fun onOptionsMenuClick(position: Int) {
                    //tracks[position].album.id
                    performOptionsMenuClick(position, tracks[position].album.id)
                }
            })
        listView.adapter = adapter
        listView.layoutManager = LinearLayoutManager(requireContext())

        trackViewModel = ViewModelProvider(requireActivity()).get(TrackViewModel::class.java)

        trackViewModel.tracks.observe(viewLifecycleOwner) { elementos ->
            adapter.updateData(elementos as ArrayList<Track>)
            tracks = elementos

        }

        // change searchView
        val searchView: SearchView = view.findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    onSearchTracks(query)
                    searchView.clearFocus()
                    val imm =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(searchView.windowToken, 0)
                    return true

                }

//                searchView.setQuery("", false)
//                searchView.isIconified = true

//                searchView.clearFocus()
//                hideKeyboardFrom(requireContext(), view)

                return true

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // You can implement real-time search here if needed
                return true
            }


        })


        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, 0) {

            //! que hace?

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                return
            }

//            override fun on


        })

        itemTouchHelper.attachToRecyclerView(listView)

    }

    private fun performOptionsMenuClick(position: Int, albumID: String) {

        val popupMenu =
            PopupMenu(
                requireContext(),
                binding.listView[position].findViewById(R.id.textViewOptions)
            )

        popupMenu.inflate(R.menu.options_menu)
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                when (item?.itemId) {
                    R.id.viewAlbum -> {

                        println(binding.listView[position].findViewById(R.id.textViewOptions))

                        val bundle = Bundle()
                        bundle.putString("key", albumID)

                        findNavController().navigate(
                            R.id.action_FirstFragment_to_AlbumFragment,
                            bundle
                        )

                        return true
                    }
                    R.id.viewArtist -> {
                        Toast.makeText(requireContext(), "View Artist clicked", Toast.LENGTH_SHORT)
                            .show()
                        return true
                    }

                }
                return false
            }
        })
        popupMenu.show()
    }


    private fun hideKeyboardFrom(context: Context, view: View?) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }


    private fun onSearchTracks(query: String) {
        GlobalScope.launch(Dispatchers.Main) {
            trackViewModel.startLoadingTracks(query)!!
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}