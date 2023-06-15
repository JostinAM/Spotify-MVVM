package cr.ac.una.spotify.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import cr.ac.una.spotify.entity.TopTrack
import cr.ac.una.spotify.entity.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class TopTracksAdapter(
    var topTracks: ArrayList<TopTrack>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

//    lateinit var mediaPlayer: MediaPlayer

//    private var mediaPlayer = MediaPlayer()

    private val mediaPlayer: MediaPlayer = MediaPlayer()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        mediaPlayer = MediaPlayer()

        val view = LayoutInflater.from(parent.context)
            .inflate(cr.ac.una.spotify.R.layout.top_track_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return topTracks.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = topTracks[position]

        val trackItem = item
        (holder as TrackViewHolder).bind(trackItem)
    }

    inner class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val topAlbumImage = itemView.findViewById<ImageView>(cr.ac.una.spotify.R.id.topAlbumImage)
        val topSongTitle = itemView.findViewById<TextView>(cr.ac.una.spotify.R.id.topSongTitle)
        val topAlbumTitle = itemView.findViewById<TextView>(cr.ac.una.spotify.R.id.topAlbumTitle)
        val songPopularity = itemView.findViewById<TextView>(cr.ac.una.spotify.R.id.songPopularity)

//        private val mediaPlayer: MediaPlayer = MediaPlayer()


        private fun handlePreviewClick() {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.reset()
            } else {
                val previewUrl = topTracks[adapterPosition].preview_url
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
            topAlbumImage.setOnClickListener {
                handlePreviewClick()
            }
        }

        fun bind(topTrack: TopTrack) {

            topSongTitle.text = topTrack!!.name.toString();
            topAlbumTitle.text = topTrack!!.album.name.toString();
            songPopularity.text = topTrack!!.popularity.toString();

            CoroutineScope(Dispatchers.IO).launch {
                val bitmap = loadImage(topTrack.album.images[0].url)
                withContext(Dispatchers.Main) {
                    topAlbumImage.setImageBitmap(bitmap)
                }
            }
        }
    }

    private fun loadImage(imageUrl: String): Bitmap? {
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

    fun updateData(newTracks: ArrayList<TopTrack>) {

        topTracks = newTracks
        if (!newTracks.isEmpty())
            if (newTracks[0].name != "")
            //create empty list of images

//                newTracks.add(0, Track("", Album("", ArrayList<null>),"","", ""))
                notifyDataSetChanged()

    }


}