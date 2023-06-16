package cr.ac.una.spotify.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import cr.ac.una.spotify.entity.RelatedArtist
import cr.ac.una.spotify.entity.Track

class RelatedArtistsAdapter(
    var artists: ArrayList<RelatedArtist>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(cr.ac.una.spotify.R.layout.related_artist_item, parent, false)
        return ArtistViewHolder(view)
    }

    override fun getItemCount(): Int {
        return artists.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = artists[position]

        val artistItem = item

        (holder as ArtistViewHolder).bind(artistItem)
    }

    inner class ArtistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val artistName =
            itemView.findViewById<TextView>(cr.ac.una.spotify.R.id.relatedName)

        val artistPop = itemView.findViewById<TextView>(cr.ac.una.spotify.R.id.relatedPopularity)



        fun bind(artist: RelatedArtist) {
            artistName.text = artist.name.toString()
            artistPop.text = artist.popularity.toString()

        }
    }


    fun updateData(newGenres: ArrayList<RelatedArtist>) {

        artists = newGenres
        if (!newGenres.isEmpty())
            //if (newGenres[0] != "")
            //create empty list of images

//                newTracks.add(0, Track("", Album("", ArrayList<null>),"","", ""))
                notifyDataSetChanged()

    }

}