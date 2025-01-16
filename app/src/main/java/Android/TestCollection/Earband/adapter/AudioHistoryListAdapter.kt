package Android.TestCollection.Earband.adapter

import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.databinding.RecyclerViewAudioHistoryBinding
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
        val binding = RecyclerViewAudioHistoryBinding.inflate(
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
        private val binding: RecyclerViewAudioHistoryBinding,
        private val onItemClicked: (Audio) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(audio: Audio) {
            binding.textviewTitle.text = audio.title
            if (audio.composer != "" && audio.composer != null) binding.textviewComposer.text =
                audio.composer else binding.textviewComposer.text = "Unknown Artist"
            if (audio.playlistId == 0L)
                binding.textviewPlaylist.text = "Local Media Storage"
            else
                binding.textviewPlaylist.text = "Unknown Album"


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