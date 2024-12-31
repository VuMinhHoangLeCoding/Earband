package Android.TestCollection.Earband.fragment

import Android.TestCollection.Earband.BroadcastUtil
import Android.TestCollection.Earband.Constants
import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.activity.AudioPlayerActivity
import Android.TestCollection.Earband.databinding.FragmentMiniPlayerBinding
import Android.TestCollection.Earband.model.Audio
import Android.TestCollection.Earband.service.AudioPlayerService
import Android.TestCollection.Earband.viewModel.MiniPLayerViewModel
import Android.TestCollection.Earband.viewModel.AudioViewModel
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels

class FragmentMiniPlayer : Fragment() {

    private val TAG = "FragmentMiniPlayer"


    private val broadcastUtil = BroadcastUtil()
    private var _binding: FragmentMiniPlayerBinding? = null
    private val binding get() = _binding!!
    private var isPlaying = false
    private val audioViewModel: AudioViewModel by activityViewModels()
    private val miniPLayerViewModel: MiniPLayerViewModel by viewModels()
    private lateinit var broadcastReceiver: BroadcastReceiver
    private val broadcastUil = BroadcastUtil()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(TAG, "onCreateView called")

        _binding = FragmentMiniPlayerBinding.inflate(inflater, container, false)

        binding.cardviewPlayButton.setOnClickListener {
            isPlaying = !isPlaying
            if (isPlaying) {
                binding.iconPlayButton.setImageResource(R.drawable.chevon_right)
                broadcastUtil.broadcastPlayerState(requireContext(), Constants.BROADCAST_ACTION_MINI_PLAYER_PLAY)
            } else {
                binding.iconPlayButton.setImageResource(R.drawable.align_vertical)
                broadcastUtil.broadcastPlayerState(requireContext(), Constants.BROADCAST_ACTION_MINI_PLAYER_PAUSE)
            }
        }

        binding.buttonAudioForward.setOnClickListener {
            val audio = miniPLayerViewModel.selectedAudio.value ?: Audio.emptyAudio
            val selectedAudio = getSelectedAudio(audio, 1)

            binding.textViewTitle.text = selectedAudio.title ?: ""
            binding.textViewComposer.text = selectedAudio.composer ?: "Unknown"

            isPlaying = true
            binding.iconPlayButton.setImageResource(R.drawable.chevon_right)

            triggerAudioPlayerService(selectedAudio)
        }


        binding.miniPlayer.setOnClickListener {
            val intent = Intent(requireContext(), AudioPlayerActivity::class.java).apply {
                if (isPlaying) {
                    putExtra("MINI_PLAYER_STATE", "PLAYER_PLAY")
                } else {
                    putExtra("MINI_PLAYER_STATE", "PLAYER_PAUSE")
                }
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            }
            requireContext().startActivity(intent)
        }

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Constants.BROADCAST_ACTION_AUDIO_SELECTED -> {

                        val audio = audioViewModel.currentAudio.value ?: Audio.emptyAudio
                        updatePlaylist(audio)
                        val selectedAudio = getSelectedAudio(audio, 0)

                        if (selectedAudio != Audio.emptyAudio) {
                            binding.textViewTitle.text = selectedAudio.title ?: ""
                            binding.textViewComposer.text = selectedAudio.composer ?: "Unknown"

                            isPlaying = true
                            binding.iconPlayButton.setImageResource(R.drawable.chevon_right)

                            triggerAudioPlayerService(selectedAudio)

                            val intentActivity = Intent(requireContext(), AudioPlayerActivity::class.java).apply {
                                putExtra("MINI_PLAYER_STATE", "PLAYER_PLAY")
                                putExtra("AUDIO", selectedAudio)
                                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                            }
                            requireContext().startActivity(intentActivity)
                            broadcastAudioToPlayer(selectedAudio)

                        } else if (selectedAudio == Audio.emptyAudio) {
                            Util.triggerToast(requireContext(), "Unknown Error occurred!")
                        }
                    }

                    Constants.BROADCAST_ACTION_PLAYER_PLAY -> {
                        if (!isPlaying) binding.cardviewPlayButton.performClick()
                    }

                    Constants.BROADCAST_ACTION_PLAYER_PAUSE -> {
                        if (isPlaying) binding.cardviewPlayButton.performClick()
                    }

                    Constants.BROADCAST_ACTION_PLAYER_FORWARD -> {
                        binding.buttonAudioForward.performClick()
                    }

                    Constants.BROADCAST_ACTION_PLAYER_BACKWARD -> {

                    }
                }
            }
        }
        val intentFilter = IntentFilter().apply {
            addAction(Constants.BROADCAST_ACTION_AUDIO_SELECTED)
            addAction(Constants.BROADCAST_ACTION_PLAYER_PLAY)
            addAction(Constants.BROADCAST_ACTION_PLAYER_PAUSE)
            addAction(Constants.BROADCAST_ACTION_PLAYER_FORWARD)
            addAction(Constants.BROADCAST_ACTION_PLAYER_BACKWARD)
        }
        requireContext().registerReceiver(broadcastReceiver, intentFilter, Context.RECEIVER_EXPORTED)

        return binding.root
    }


    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop called")
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called")
        super.onDestroy()
        _binding = null
    }

    private fun updatePlaylist(audio: Audio) {
        if (audio != Audio.emptyAudio) {
            if (audioViewModel.getCurrentAudioPlaylistId() != miniPLayerViewModel.getSelectedAudioPlaylist() && audioViewModel.getCurrentAudioPlaylistId() != -1L) {
                if (audioViewModel.getCurrentAudioPlaylistId() == 0L) {
                    miniPLayerViewModel.setAudioPlaylist(audioViewModel.loadAudiosFromLocal())
                }
            }
        } else {
            Util.triggerToast(requireContext(), "No Audio selected")
        }
    }

    private fun getSelectedAudio(audio: Audio, forwardInt: Int): Audio {
        var selectedAudio = Audio.emptyAudio
        if (audio != Audio.emptyAudio && miniPLayerViewModel.hasAudio(audio)) {
            if (miniPLayerViewModel.getAudioPosition(audio) != -1) {
                selectedAudio = miniPLayerViewModel.getSelectedAudioFromPosition(miniPLayerViewModel.getAudioPosition(audio) + forwardInt)
                miniPLayerViewModel.setSelectedAudio(selectedAudio)
            } else Util.triggerToast(requireContext(), "No Audio Position")
        } else Util.triggerToast(requireContext(), "No Audio in AudioPlayerViewModel")
        return selectedAudio
    }

    private fun triggerAudioPlayerService(audio: Audio) {
        val intentService = Intent(requireContext(), AudioPlayerService::class.java).apply {
            putExtra("AUDIO", audio)
        }
        requireContext().startService(intentService)
        broadcastUtil.broadcastPlayerState(requireContext(), Constants.BROADCAST_ACTION_MINI_PLAYER_PLAY)
    }

    private fun broadcastAudioToPlayer(audio: Audio) {
        broadcastUil.broadcastNewAudio(requireContext(), audio, Constants.BROADCAST_ACTION_AUDIO)
    }
}