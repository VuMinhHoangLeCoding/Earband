package Android.TestCollection.Earband.adapter

import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.model.Audio
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class AudioListAdapter(
    private val onItemClicked: (Audio) -> Unit,
    private val onMenuButtonClicked: (Audio, View) -> Unit
) :
    ListAdapter<Audio, AudioListAdapter.AudioViewHolder>(AudioComparator()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val layout = when (Util.theme) {
            "MUEL" -> R.layout.muel_item_audio
            "MUD" -> R.layout.mud_item_audio
            else -> R.layout.muel_item_audio
        }
        val itemView = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return AudioViewHolder(itemView, onItemClicked, onMenuButtonClicked)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        val audio = getItem(position)
        holder.bind(audio, position)
    }

    class AudioViewHolder(
        itemView: View,
        private val onItemClicked: (Audio) -> Unit,
        private val onMenuButtonClicked: (Audio, View) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val textTile: TextView = itemView.findViewById(R.id.text_title)
        private val textArtist: TextView = itemView.findViewById(R.id.text_artist)
        private val layoutImageAlbum: ConstraintLayout = itemView.findViewById(R.id.layout_image_album)
        private val buttonDropdownMenu: ImageButton = itemView.findViewById(R.id.button_dropdown_context_menu)

        fun bind(audio: Audio, position: Int) {
            textTile.text = audio.title
            if (audio.artistName != "") textArtist.text =
                audio.composer else textArtist.setText(R.string.Unknown)
            if (Util.theme == "MUEL") {
                if (position % 2 == 1) {
                    layoutImageAlbum.setBackgroundResource(R.color.green_banana_leaf)
                } else {
                    layoutImageAlbum.setBackgroundResource(R.color.orange_golden)
                }
            } else {
                layoutImageAlbum.setBackgroundResource(R.color.white_dirt_light)
            }

            itemView.setOnClickListener {
                onItemClicked(audio)
            }

            buttonDropdownMenu.setOnClickListener {
                onMenuButtonClicked(audio, buttonDropdownMenu)
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