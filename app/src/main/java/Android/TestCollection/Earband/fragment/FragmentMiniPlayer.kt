package Android.TestCollection.Earband.fragment

import Android.TestCollection.Earband.Constants
import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.activity.AudioPlayerActivity
import Android.TestCollection.Earband.application.AudioPlayerData
import Android.TestCollection.Earband.databinding.FragmentMiniPlayerBinding
import Android.TestCollection.Earband.model.Audio
import Android.TestCollection.Earband.service.AudioPlayerService
import Android.TestCollection.Earband.viewModel.MainViewModel
import android.annotation.SuppressLint
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FragmentMiniPlayer : Fragment() {

    @Inject
    lateinit var audioPlayerData: AudioPlayerData

    private var _binding: FragmentMiniPlayerBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var broadcastReceiver: BroadcastReceiver
    private var playerServiceState: Boolean = false

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView called")
        _binding = FragmentMiniPlayerBinding.inflate(inflater, container, false)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                audioPlayerData.selectedAudio.collect { audio ->
                    binding.textViewTitle.text = audio.title
                    if (!audio.composer.isNullOrEmpty()) binding.textViewComposer.text = audio.composer else binding.textViewComposer.setText(R.string.Unknown)
                    if (audio != Audio.emptyAudio) {
                        addAudioToHistory(audio)
                    }
                    binding.seekbar.max = audio.duration.toInt()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                audioPlayerData.isPlaying.collect { isPlaying ->
                    if (isPlaying) binding.audioPlayButton.setImageResource(R.drawable.audio_pause_black)
                    else binding.audioPlayButton.setImageResource(R.drawable.audio_play_black)
                }
            }
        }

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Constants.BROADCAST_ACTION_AUDIO_SELECTED -> {
                        val selectedAudio = mainViewModel.selectedAudio.value ?: Audio.emptyAudio
                        updatePlaylistOnSelectedAudioPlaylist(selectedAudio)
                        val audio = getAudioFromPlaylist(selectedAudio, 0)

                        if (audio != Audio.emptyAudio) {

                            audioPlayerData.setSelectedAudio(audio)

                            triggerAudioPlayerService("PLAY")

                            val intentActivity = Intent(requireContext(), AudioPlayerActivity::class.java).apply {
                                putExtra("MINI_PLAYER_STATE", "PLAY")
                                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                            }
                            requireContext().startActivity(intentActivity)
                        } else if (audio == Audio.emptyAudio) {
                            Util.triggerToast(requireContext(), "Unknown Error occurred!")
                        }
                    }

                    Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_PLAY -> {
                        if (!audioPlayerData.isPlaying.value) triggerPlayOrPausePlayer()
                    }

                    Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_PAUSE -> {
                        if (audioPlayerData.isPlaying.value) triggerPlayOrPausePlayer()
                    }

                    Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_FORWARD -> {
                        triggerAudioForward()
                    }

                    Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_BACKWARD -> {
                        triggerAudioPlayerService("BACKWARD")
                    }

                    Constants.BROADCAST_ACTION_PLAYER_BACKWARD -> {
                        val currentAudio = audioPlayerData.getSelectedAudio()
                        val selectedAudio = getAudioFromPlaylist(currentAudio, -1)
                        audioPlayerData.setSelectedAudio(selectedAudio)
                        triggerAudioPlayerService("PLAY")
                    }

                    Constants.BROADCAST_ACTION_PLAYER_ENDED -> {
                        triggerAudioForward()
                    }

                    Constants.BROADCAST_ACTION_PLAYER_PROGRESSION -> {
                        val progress = intent.getLongExtra("PROGRESSION", 0L)
                        binding.seekbar.progress = progress.toInt()
                    }

                    Constants.BROADCAST_ACTION_AUDIO_FOCUS_LOSS -> {
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
            addAction(Constants.BROADCAST_ACTION_PLAYER_PROGRESSION)
            addAction(Constants.BROADCAST_ACTION_AUDIO_FOCUS_LOSS)
        }
        if (Util.isAndroidVersionHigherOrEqualTiramisu()) {
            requireContext().registerReceiver(broadcastReceiver, intentFilter, Context.RECEIVER_EXPORTED)
        }
        if(!Util.isAndroidVersionHigherOrEqualTiramisu()) {
            requireContext().registerReceiver(broadcastReceiver, intentFilter)
        }
        return binding.root
    }


    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")

        binding.miniPlayer.setOnClickListener {
            val intent = Intent(requireContext(), AudioPlayerActivity::class.java).apply {
                if (audioPlayerData.isPlaying.value) {
                    putExtra("MINI_PLAYER_STATE", "PLAY")
                } else {
                    putExtra("MINI_PLAYER_STATE", "PAUSE")
                }
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            }
            requireContext().startActivity(intent)
        }

        binding.audioPlayButton.setOnClickListener {
            triggerPlayOrPausePlayer()
        }

        binding.buttonAudioForward.setOnClickListener {
            triggerAudioForward()
        }
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
        requireContext().stopService(Intent(requireContext(), AudioPlayerService::class.java))
        requireContext().unregisterReceiver(broadcastReceiver)
    }

    private fun updatePlaylistOnSelectedAudioPlaylist(audio: Audio) {
        if (audio != Audio.emptyAudio) {
            if (mainViewModel.getCurrentAudioPlaylistId() != audioPlayerData.getSelectedAudioPlaylist() && mainViewModel.getCurrentAudioPlaylistId() != -1L) {
                if (mainViewModel.getCurrentAudioPlaylistId() == 0L) {
                    audioPlayerData.setAudioPlaylist(mainViewModel.getLocalAudios())
                }
            }
        } else {
            Util.triggerToast(requireContext(), "No Audio selected")
        }
    }

    private fun getAudioFromPlaylist(audio: Audio, forwardInt: Int): Audio {
        var selectedAudio = Audio.emptyAudio
        if (audio != Audio.emptyAudio && audioPlayerData.hasAudio(audio)) {
            if (audioPlayerData.getAudioPosition(audio) != -1) {
                selectedAudio = audioPlayerData.getSelectedAudioFromPosition(audioPlayerData.getAudioPosition(audio) + forwardInt)
            } else Util.triggerToast(requireContext(), "No Audio Position")
        } else Util.triggerToast(requireContext(), "No Audio in AudioPlayerViewModel")
        return selectedAudio
    }

    private fun triggerAudioPlayerService(command: String) {
        val intentService = Intent(requireContext(), AudioPlayerService::class.java).apply {
            putExtra("MINI_PLAYER_COMMAND", command)
        }
        requireContext().startService(intentService)
    }

    private fun addAudioToHistory(audio: Audio) {
        val time: Long = System.currentTimeMillis()
        mainViewModel.addNewAudioHistoryEntity(audio, time)
    }

    private fun triggerPlayOrPausePlayer() {
        if (!audioPlayerData.isPlaying.value) {
            if (!playerServiceState) {
                triggerAudioPlayerService("PLAY")
                playerServiceState = true
            }
            Util.broadcastState(requireContext(), Constants.BROADCAST_ACTION_MINI_PLAYER_PLAY)
        } else {
            binding.audioPlayButton.setImageResource(R.drawable.audio_pause_black)
            Util.broadcastState(requireContext(), Constants.BROADCAST_ACTION_MINI_PLAYER_PAUSE)
        }
    }

    private fun triggerAudioForward() {
        val audio = audioPlayerData.getSelectedAudio()
        val selectedAudio = getAudioFromPlaylist(audio, 1)
        audioPlayerData.setSelectedAudio(selectedAudio)
        triggerAudioPlayerService("PLAY")
    }

    companion object {
        private const val TAG = "FragmentMiniPlayer"
    }
}