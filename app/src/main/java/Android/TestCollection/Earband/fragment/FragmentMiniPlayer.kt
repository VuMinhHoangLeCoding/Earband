package Android.TestCollection.Earband.fragment

import Android.TestCollection.Earband.BroadcastUtil
import Android.TestCollection.Earband.Constants
import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.databinding.FragmentMiniPlayerBinding
import Android.TestCollection.Earband.viewModel.AudioViewModel
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class FragmentMiniPlayer : Fragment() {

    private val broadcastUtil =  BroadcastUtil()
    private var _binding: FragmentMiniPlayerBinding? = null
    private val binding get() = _binding!!
    private var isPlaying = false
    private val audioViewModel: AudioViewModel by activityViewModels()
    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMiniPlayerBinding.inflate(inflater, container, false)

        binding.cardviewPlayButton.setOnClickListener {
            isPlaying = !isPlaying
            if (isPlaying) {
                binding.iconPlayButton.setImageResource(R.drawable.chevon_right)
                broadcastUtil.broadcastPlayerState(requireContext(), Constants.BROADCAST_ACTION_PLAYER_PLAY)
            } else {
                binding.iconPlayButton.setImageResource(R.drawable.align_vertical)
                broadcastUtil.broadcastPlayerState(requireContext(), Constants.BROADCAST_ACTION_PLAYER_PAUSE)
            }
        }

        broadcastReceiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val audio = audioViewModel.currentAudio.value
                binding.textViewTitle.text = audio?.title ?: ""
                binding.textViewComposer.text = audio?.composer ?: "Unknown"
                isPlaying = true
                binding.iconPlayButton.setImageResource(R.drawable.chevon_right)
            }
        }
        val intenteFilter =IntentFilter(Constants.BROADCAST_ACTION_AUDIO_SELECTED)
        requireContext().registerReceiver(broadcastReceiver, intenteFilter, Context.RECEIVER_EXPORTED)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }
}