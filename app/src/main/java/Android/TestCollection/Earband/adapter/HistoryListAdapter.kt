package Android.TestCollection.Earband.adapter

import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.model.Audio
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

@SuppressLint("SetTextI18n")
class HistoryListAdapter(private val onItemClicked: (Audio) -> Unit) :
    ListAdapter<Audio, HistoryListAdapter.AudioHistoryViewHolder>(AudioComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioHistoryViewHolder {
        val layout = when (Util.theme) {
            "MUEL" -> R.layout.muel_item_history
            else -> R.layout.muel_item_history
        }
        val itemView = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return AudioHistoryViewHolder(itemView, onItemClicked)
    }

    override fun onBindViewHolder(holder: AudioHistoryViewHolder, position: Int) {
        val audio = getItem(position)
        holder.bind(audio)
    }

    class AudioHistoryViewHolder(
        itemView: View,
        private val onItemClicked: (Audio) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val textTile: TextView = itemView.findViewById(R.id.text_title)
        private val textArtist: TextView = itemView.findViewById(R.id.text_artist)
        private val textPlaylist: TextView = itemView.findViewById(R.id.text_playlist)

        fun bind(audio: Audio) {
            textTile.text = audio.title
            if (audio.artistName != "") textArtist.text = audio.artistName else textArtist.text = "Unknown Artist"
            if (audio.playlistId == 0L) textPlaylist.text = "Local Media Storage" else textPlaylist.text = "Unknown Playlist"

            itemView.setOnClickListener {
                onItemClicked(audio)
            }
        }
    }

    class AudioComparator : DiffUtil.ItemCallback<Audio>() {
        override fun areItemsTheSame(oldItem: Audio, newItem: Audio): Boolean {
            return oldItem.id == newItem.id // Unique ID comparison
        }

        override fun areContentsTheSame(oldItem: Audio, newItem: Audio): Boolean {
            return oldItem == newItem // Check content equality
        }
    }
}