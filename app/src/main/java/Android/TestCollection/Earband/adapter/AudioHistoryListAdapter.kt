package Android.TestCollection.Earband.adapter

import Android.TestCollection.Earband.databinding.MuelItemHistoryBinding
import Android.TestCollection.Earband.model.Audio
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

@SuppressLint("SetTextI18n")
class AudioHistoryListAdapter(private val onItemClicked: (Audio) -> Unit) :
    ListAdapter<Audio, AudioHistoryListAdapter.AudioHistoryViewHolder>(AudioComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioHistoryViewHolder {
        val binding = MuelItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AudioHistoryViewHolder(binding, onItemClicked)
    }

    override fun onBindViewHolder(holder: AudioHistoryViewHolder, position: Int) {
        val audio = getItem(position)
        holder.bind(audio)
    }

    class AudioHistoryViewHolder(
        private val binding: MuelItemHistoryBinding,
        private val onItemClicked: (Audio) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(audio: Audio) {
            binding.textTitle.text = audio.title
            if (audio.composer != "" && audio.composer != null) binding.textArtist.text =
                audio.composer else binding.textArtist.text = "Unknown Artist"
            if (audio.playlistId == 0L)
                binding.textPlaylist.text = "Local Media Storage"
            else
                binding.textPlaylist.text = "Unknown Album"


            binding.root.setOnClickListener {
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