package Android.TestCollection.Earband.adapter

import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.databinding.MuelItemAudioBinding
import Android.TestCollection.Earband.model.Audio
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class AudioListAdapter(private val onItemClicked: (Audio) -> Unit) :
    ListAdapter<Audio, AudioListAdapter.AudioViewHolder>(AudioComparator()) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val binding = MuelItemAudioBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AudioViewHolder(binding, onItemClicked)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        val audio = getItem(position)
        holder.bind(audio, position)
    }

    class AudioViewHolder(
        private val binding: MuelItemAudioBinding,
        private val onItemClicked: (Audio) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(audio: Audio, position: Int) {
            binding.textTitle.text = audio.title
            if (audio.composer != "" && audio.composer != null) binding.textArtist.text = audio.composer else binding.textArtist.setText(R.string.Unknown)

            if (position % 2 == 1){
                binding.layoutImageAlbum.setBackgroundResource(R.color.banana_leaf_green)
            }
            else {
                binding.layoutImageAlbum.setBackgroundResource(R.color.golden_orange)
            }

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