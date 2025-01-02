package Android.TestCollection.Earband.fragment

import Android.TestCollection.Earband.Constants
import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.activity.AudioPlayerActivity
import Android.TestCollection.Earband.application.AppAudioPlayerData
import Android.TestCollection.Earband.application.EarbandApp
import Android.TestCollection.Earband.databinding.FragmentMiniPlayerBinding
import Android.TestCollection.Earband.model.Audio
import Android.TestCollection.Earband.service.AudioPlayerService
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

class FragmentMiniPlayer : Fragment() {

    private lateinit var appAudioPlayerData: AppAudioPlayerData

    private var _binding: FragmentMiniPlayerBinding? = null
    private val binding get() = _binding!!
    private var isPlaying = false
    private val audioViewModel: AudioViewModel by activityViewModels()
    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d(TAG, "onCreateView called")

        _binding = FragmentMiniPlayerBinding.inflate(inflater, container, false)

        appAudioPlayerData = (requireContext().applicationContext as EarbandApp).appAudioPlayerData

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                appAudioPlayerData.currentAudio.collect { audio ->
                    binding.textViewTitle.text = audio.title
                    binding.textViewComposer.text = audio.composer
                }
            }
        }

        binding.cardviewPlayButton.setOnClickListener {
            isPlaying = !isPlaying
            if (isPlaying) {
                binding.iconPlayButton.setImageResource(R.drawable.chevon_right)
                Util.broadcastState(requireContext(), Constants.BROADCAST_ACTION_MINI_PLAYER_PLAY)
            } else {
                binding.iconPlayButton.setImageResource(R.drawable.align_vertical)
                Util.broadcastState(requireContext(), Constants.BROADCAST_ACTION_MINI_PLAYER_PAUSE)
            }
        }

        binding.buttonAudioForward.setOnClickListener {
            val audio = appAudioPlayerData.getSelectedAudio()
            val selectedAudio = getAudioFromPlayerDataPlaylist(audio, 1)
            appAudioPlayerData.setSelectedAudio(selectedAudio)

            isPlaying = true
            binding.iconPlayButton.setImageResource(R.drawable.chevon_right)

            triggerAudioPlayerService(selectedAudio, "PLAY")
        }


        binding.miniPlayer.setOnClickListener {
            val intent = Intent(requireContext(), AudioPlayerActivity::class.java).apply {
                if (isPlaying) {
                    putExtra("MINI_PLAYER_STATE", "PLAY")
                } else {
                    putExtra("MINI_PLAYER_STATE", "PAUSE")
                }
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            }
            requireContext().startActivity(intent)
        }

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Constants.BROADCAST_ACTION_AUDIO_SELECTED -> {
                        val selectedAudio = audioViewModel.selectedAudio.value ?: Audio.emptyAudio
                        updatePlaylistOnSelectedAudioPLaylist(selectedAudio)
                        val audio = getAudioFromPlayerDataPlaylist(selectedAudio, 0)

                        if (audio != Audio.emptyAudio) {

                            appAudioPlayerData.setSelectedAudio(audio)

                            isPlaying = true
                            binding.iconPlayButton.setImageResource(R.drawable.chevon_right)

                            triggerAudioPlayerService(audio, "PLAY")

                            val intentActivity = Intent(requireContext(), AudioPlayerActivity::class.java).apply {
                                putExtra("MINI_PLAYER_STATE", "PLAY")
                                putExtra("AUDIO", audio)
                                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                            }
                            requireContext().startActivity(intentActivity)
                        } else if (audio == Audio.emptyAudio) {
                            Util.triggerToast(requireContext(), "Unknown Error occurred!")
                        }
                    }

                    Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_PLAY -> {
                        if (!isPlaying) binding.cardviewPlayButton.performClick()
                    }

                    Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_PAUSE -> {
                        if (isPlaying) binding.cardviewPlayButton.performClick()
                    }

                    Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_FORWARD -> {
                        binding.buttonAudioForward.performClick()
                    }

                    Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_BACKWARD -> {
                        val currentAudio = appAudioPlayerData.getSelectedAudio()
                        val selectedAudio = getAudioFromPlayerDataPlaylist(currentAudio, -1)

                        isPlaying = true
                        binding.iconPlayButton.setImageResource(R.drawable.chevon_right)

                        triggerAudioPlayerService(selectedAudio, "BACKWARD")
                    }

                    Constants.BROADCAST_ACTION_PLAYER_BACKWARD -> {
                        val currentAudio = appAudioPlayerData.getSelectedAudio()
                        val selectedAudio = getAudioFromPlayerDataPlaylist(currentAudio, -1)
                        appAudioPlayerData.setSelectedAudio(selectedAudio)
                    }

                    Constants.BROADCAST_ACTION_PLAYER_ENDED -> {
                        binding.buttonAudioForward.performClick()
                    }
                }
            }
        }
        val intentFilter = IntentFilter().apply {
            addAction(Constants.BROADCAST_ACTION_AUDIO_SELECTED)
            addAction(Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_PLAY)
            addAction(Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_PAUSE)
            addAction(Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_FORWARD)
            addAction(Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_BACKWARD)
            addAction(Constants.BROADCAST_ACTION_PLAYER_ENDED)
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
        super.onDestroy()
        Log.d(TAG, "onDestroy called")
        _binding = null
    }

    private fun updatePlaylistOnSelectedAudioPLaylist(audio: Audio) {
        if (audio != Audio.emptyAudio) {
            if (audioViewModel.getCurrentAudioPlaylistId() != appAudioPlayerData.getSelectedAudioPlaylist() && audioViewModel.getCurrentAudioPlaylistId() != -1L) {
                if (audioViewModel.getCurrentAudioPlaylistId() == 0L) {
                    appAudioPlayerData.setAudioPlaylist(audioViewModel.getAudios())
                }
            }
        } else {
            Util.triggerToast(requireContext(), "No Audio selected")
        }
    }

    private fun getAudioFromPlayerDataPlaylist(audio: Audio, forwardInt: Int): Audio {
        var selectedAudio = Audio.emptyAudio
        if (audio != Audio.emptyAudio && appAudioPlayerData.hasAudio(audio)) {
            if (appAudioPlayerData.getAudioPosition(audio) != -1) {
                selectedAudio = appAudioPlayerData.getSelectedAudioFromPosition(appAudioPlayerData.getAudioPosition(audio) + forwardInt)
            } else Util.triggerToast(requireContext(), "No Audio Position")
        } else Util.triggerToast(requireContext(), "No Audio in AudioPlayerViewModel")
        return selectedAudio
    }

    private fun triggerAudioPlayerService(audio: Audio, command: String) {
        val intentService = Intent(requireContext(), AudioPlayerService::class.java).apply {
            putExtra("AUDIO", audio)
            putExtra("MINI_PLAYER_COMMAND", command)
        }
        requireContext().startService(intentService)
    }

    companion object {
        private const val TAG = "FragmentMiniPlayer"
    }
}