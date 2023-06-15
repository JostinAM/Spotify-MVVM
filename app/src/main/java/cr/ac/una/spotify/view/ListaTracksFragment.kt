package cr.ac.una.spotify.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cr.ac.una.spotify.R
import cr.ac.una.spotify.adapter.TrackAdapter
import cr.ac.una.spotify.dao.BusquedaDAO
import cr.ac.una.spotify.databinding.FragmentListaTracksBinding
import cr.ac.una.spotify.db.AppDatabase
import cr.ac.una.spotify.entity.Busqueda
import cr.ac.una.spotify.entity.Track
import cr.ac.una.spotify.viewModel.TrackViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList


class ListaTracksFragment : Fragment() {

    private var _binding: FragmentListaTracksBinding? = null
    private val binding get() = _binding!!
    private lateinit var trackViewModel: TrackViewModel
    private lateinit var tracks: List<Track>
    private lateinit var busquedaDAO: BusquedaDAO

    //
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        busquedaDAO = AppDatabase.getInstance(requireContext()).busquedaDao()
    }

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

        val searchView: SearchView = view.findViewById(R.id.search_view)
        var searchResults = ArrayList<Busqueda>()

        val from = arrayOf("suggestion")
        val to = intArrayOf(android.R.id.text1)
        val suggestionsAdapter = SimpleCursorAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            null,
            from,
            to,
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )
        searchView.suggestionsAdapter = suggestionsAdapter


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
//        val searchView: SearchView = view.findViewById(R.id.search_view)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    onSearchTracks(query)

                    val busqueda = Busqueda(
                        id = null,
                        text = query,
                        date = Date()
                    )


                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            busquedaDAO.insert(busqueda)
                        }
                    }

                    searchView.clearFocus()
                    val imm =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(searchView.windowToken, 0)
                    return true

                }



                return true

            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (newText != null && newText.length >= 5) {
                    lifecycleScope.launch {
                        searchResults = withContext(Dispatchers.IO) {
                            busquedaDAO.searchBusqueda("%$newText%")
                            //                            busquedaDAO.buscarCoincidencias("$newText")
                        } as ArrayList<Busqueda>

                        val cursor = MatrixCursor(arrayOf(BaseColumns._ID, "suggestion"))
                        searchResults.forEachIndexed { index, busqueda ->
                            cursor.addRow(
                                arrayOf(
                                    index,
                                    busqueda.text
                                )
                            )
                        }
                        suggestionsAdapter.changeCursor(cursor)
                        //print type of data of searchResults

                        println("ONQUERYTEXTCHANGE: $searchResults")
                    }
                }
                return true
            }


        })

        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            @SuppressLint("Range")
            override fun onSuggestionClick(position: Int): Boolean {
                val cursor = suggestionsAdapter.getItem(position) as Cursor
                val selection = cursor.getString(cursor.getColumnIndex("suggestion"))
                searchView.setQuery(selection, true)
                searchView.clearFocus()
                searchResults.clear()
                suggestionsAdapter.changeCursor(null)
                return true
            }

            override fun onSuggestionSelect(position: Int): Boolean {
                return true
            }
        })

        searchView.setOnCloseListener {
            // Clear the suggestions adapter when the SearchView is closed
            suggestionsAdapter.changeCursor(null)
            false
        }


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
                        bundle.putString("albumID", albumID)

                        findNavController().navigate(
                            R.id.action_FirstFragment_to_AlbumFragment,
                            bundle
                        )

                        return true
                    }
                    R.id.viewArtist -> {

                        println(binding.listView[position].findViewById(R.id.textViewOptions))

                        val bundle = Bundle()

                        bundle.putString("artistID", tracks[position].artists[0].id)

                        findNavController().navigate(
                            R.id.action_FirstFragment_to_ArtistFragment,
                            bundle
                        )


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