package cr.ac.una.spotify.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import cr.ac.una.spotify.entity.Album
import cr.ac.una.spotify.entity.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class TrackAdapter (var tracks: ArrayList<Track>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        var view = convertView
//        if (view == null) {
//            view = LayoutInflater.from(context).inflate(cr.ac.una.spotify.R.layout.list_item, parent, false)
//        }
//
//        val track = getItem(position)
//
//        val albumImage = view!!.findViewById<ImageView>(cr.ac.una.spotify.R.id.albumImage)
//        val songTitle = view!!.findViewById<TextView>(cr.ac.una.spotify.R.id.songTitle)
//        val songArtist = view!!.findViewById<TextView>(cr.ac.una.spotify.R.id.songArtist)
//
//        songTitle.text = track!!.name.toString();
//        songArtist.text = track!!.artist.toString();
//        //ERROR HERE
//        CoroutineScope(Dispatchers.Main).launch {
//            val bitmap = withContext(Dispatchers.IO) { loadImage(track.imageUrl) }
//            albumImage.setImageBitmap(bitmap)
//        }
//
//        return view
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(cr.ac.una.spotify.R.layout.list_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = tracks[position]

        val trackItem = item
        (holder as TrackViewHolder).bind(trackItem)

    }

    override fun getItemCount(): Int {
        return tracks.size
    }



    inner class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {




        val albumImage = itemView.findViewById<ImageView>(cr.ac.una.spotify.R.id.albumImage)
        val songTitle = itemView.findViewById<TextView>(cr.ac.una.spotify.R.id.songTitle)
        val songArtist = itemView.findViewById<TextView>(cr.ac.una.spotify.R.id.songArtist)

        fun bind(track: Track) {

            //setbackground transparent

//            itemView.setBackgroundColor(0x00000000)

            songTitle.text = track!!.name.toString();
            songArtist.text = track!!.artists[0].name.toString();
            //ERROR HERE
            CoroutineScope(Dispatchers.Main).launch {
                val bitmap = withContext(Dispatchers.IO) { loadImage(track.imageUrl) }
                albumImage.setImageBitmap(bitmap)
            }

            itemView.setOnClickListener(){
                Toast.makeText(itemView.context, "Track: " + track.name, Toast.LENGTH_SHORT).show()
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

    fun updateData(newTracks: ArrayList<Track>) {

        tracks = newTracks
        if (!newTracks.isEmpty())
            if(newTracks[0].name !="")
                //create empty list of images

//                newTracks.add(0, Track("", Album("", ArrayList<null>),"","", ""))
        notifyDataSetChanged()

    }


}