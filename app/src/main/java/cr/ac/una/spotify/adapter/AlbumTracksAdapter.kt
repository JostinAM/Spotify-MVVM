package cr.ac.una.spotify.adapter

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import cr.ac.una.spotify.entity.Track

class AlbumTracksAdapter(
    var tracks: ArrayList<Track>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mediaPlayer: MediaPlayer = MediaPlayer()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(cr.ac.una.spotify.R.layout.song_for_album_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = tracks[position]

        val trackItem = item
        (holder as TrackViewHolder).bind(trackItem)
    }

    inner class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val trackName =
            itemView.findViewById<TextView>(cr.ac.una.spotify.R.id.trackForAlbum)

        private fun handlePreviewClick() {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.reset()
            } else {
                val previewUrl = tracks[adapterPosition].preview_url
                if (previewUrl != null) {
                    try {
                        mediaPlayer.setDataSource(previewUrl)
                        mediaPlayer.prepare()
                        mediaPlayer.start()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(
                            itemView.context,
                            "No preview available",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        itemView.context,
                        "No preview available",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        init {
            trackName.setOnClickListener {
                handlePreviewClick()
            }
        }

        fun bind(track: Track) {
            trackName.text = track.name.toString()

        }
    }

    fun updateData(newTracks: ArrayList<Track>) {

        tracks = newTracks
//        if (!newTracks.isEmpty())
//            if (newTracks[0].name != "")
        //create empty list of images

//                newTracks.add(0, Track("", Album("", ArrayList<null>),"","", ""))
        notifyDataSetChanged()

    }
}