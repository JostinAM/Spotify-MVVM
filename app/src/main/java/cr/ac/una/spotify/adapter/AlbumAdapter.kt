package cr.ac.una.spotify.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class AlbumAdapter(
    var image: String,
    var name: String,
    var artist: String,
    var date: String,
    var genres: ArrayList<String>,
    var tracks: ArrayList<String>

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }


}