package cr.ac.una.spotify.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cr.ac.una.spotify.entity.Track


class AlbumGenresAdapter(
    var genres: ArrayList<String>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(cr.ac.una.spotify.R.layout.genre_item, parent, false)
        return GenreViewHolder(view)
    }

    override fun getItemCount(): Int {
        return genres.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = genres[position]

        val genreItem = item
        (holder as AlbumGenresAdapter.GenreViewHolder).bind(genreItem)


    }

    inner class GenreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val albumGenre =
            itemView.findViewById<TextView>(cr.ac.una.spotify.R.id.albumGenre)

        fun bind(genre: String) {
            albumGenre.text = genre

        }
    }

    fun updateData(newGenres: ArrayList<String>) {

        genres = newGenres
        if (!newGenres.isEmpty())
            if (newGenres[0] != "")
            //create empty list of images

//                newTracks.add(0, Track("", Album("", ArrayList<null>),"","", ""))
                notifyDataSetChanged()

    }

}