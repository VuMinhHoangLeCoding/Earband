package Android.TestCollection.Earband.fragment

import Android.TestCollection.Earband.Constants
import Android.TestCollection.Earband.FragmentListener
import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.databinding.MuelFragmentMiniPlayerBinding
import Android.TestCollection.Earband.model.Audio
import Android.TestCollection.Earband.model.Playlist
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

@AndroidEntryPoint
class FragmentMiniPlayer : Fragment() {
    private var listener: FragmentListener? = null
    private var _binding: MuelFragmentMiniPlayerBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var broadcastReceiver: BroadcastReceiver
    private var playerServiceState: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentListener) listener = context
        else throw ClassCastException("$context must implement FragmentListener")
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView called")
        _binding = MuelFragmentMiniPlayerBinding.inflate(inflater, container, false)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.currentAudio.collect { audio ->
                    binding.textViewTitle.text = audio.title
                    if (!audio.composer.isNullOrEmpty()) binding.textViewComposer.text =
                        audio.composer else binding.textViewComposer.setText(R.string.Unknown)
                    binding.seekbar.max = audio.duration.toInt()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.isPlaying.collect { isPlaying ->
                    if (isPlaying) binding.audioPlayButton.setImageResource(R.drawable.muel_icon_pause_wrap)
                    else binding.audioPlayButton.setImageResource(R.drawable.muel_icon_play_wrap)
                }
            }
        }

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Constants.BROADCAST_ACTION_AUDIO_SELECTED -> {
                        var selectedAudio: Audio = Audio.emptyAudio
                        if (Util.isAndroidVersionHigherOrEqualTiramisu()) {
                            try {
                                selectedAudio = intent.getParcelableExtra("NEW_AUDIO", Audio::class.java)!!
                            } catch (e: Exception) {
                                Log.e(TAG, " Error: $e")
                            }
                        }
                        if (selectedAudio != Audio.emptyAudio) {
                            setCurrentPlaylistAndCurrentAudios(selectedAudio)
                            addAudioToHistory(selectedAudio)
                            triggerAudioPlayerService(selectedAudio, "PLAY")
                            triggerCallback()
                        }
                    }

                    Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_PLAY -> {
                        setCurrentPlaylistAndCurrentAudios(mainViewModel.currentAudio.value)
                        if (!mainViewModel.isPlaying.value) triggerPlayOrPausePlayer()
                    }

                    Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_PAUSE -> {
                        if (mainViewModel.isPlaying.value) triggerPlayOrPausePlayer()
                    }

                    Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_FORWARD -> {
                        if (mainViewModel.playMode.value == 2) triggerAudioRandom()
                        else triggerAudioForward()
                    }

                    Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_BACKWARD -> {
                        triggerAudioPlayerService(Audio.emptyAudio, "BACKWARD")
                    }

                    Constants.BROADCAST_ACTION_PLAYER_BACKWARD -> {
                        if (mainViewModel.playMode.value == 2) triggerAudioRandom()
                        else {
                            val currentAudio = mainViewModel.currentAudio.value
                            val pos = mainViewModel.getPositionOnAudioFromCurrentAudios(currentAudio)
                            val selectedAudio = mainViewModel.getAudioOnPositionFromCurrent(pos - 1)
                            addAudioToHistory(selectedAudio)
                            updatePlaylistOnSelectedAudioPlaylistId(selectedAudio.playlistId)
                            triggerAudioPlayerService(selectedAudio, "PLAY")
                        }
                    }

                    Constants.BROADCAST_ACTION_PLAYER_ENDED -> {
                        if (mainViewModel.playMode.value == 2) triggerAudioRandom()
                        else triggerAudioForward()
                    }

                    Constants.BROADCAST_ACTION_PLAYER_PROGRESSION -> {
                        val progress = intent.getLongExtra("PROGRESSION", 0L)
                        binding.seekbar.progress = progress.toInt()
                    }

                    Constants.BROADCAST_ACTION_SHUFFLE -> {
                        val shuffleFrom = intent.getStringExtra("SHUFFLE_FROM")
                        when (shuffleFrom) {
                            "LOCAL" -> {
                                val pos = mainViewModel.getRandomPosition()
                                val audio = mainViewModel.getAudioOnPositionFromLocal(pos)
                                addAudioToHistory(audio)
                                triggerAudioPlayerService(audio, "PLAY")
                            }
                        }
                    }

                    Constants.BROADCAST_ACTION_IS_PLAYING -> {
                        val isPlaying = intent.getBooleanExtra("BOOLEAN", false)
                        mainViewModel.setIsPlaying(isPlaying)
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
            addAction(Constants.BROADCAST_ACTION_SHUFFLE)
        }
        if (Util.isAndroidVersionHigherOrEqualTiramisu()) {
            requireContext().registerReceiver(broadcastReceiver, intentFilter, Context.RECEIVER_EXPORTED)
        }
        if (!Util.isAndroidVersionHigherOrEqualTiramisu()) {
            requireContext().registerReceiver(broadcastReceiver, intentFilter)
        }

        binding.miniPlayer.setOnClickListener {
            triggerCallback()
        }

        binding.audioPlayButton.setOnClickListener {
            triggerPlayOrPausePlayer()
        }

        binding.buttonForward.setOnClickListener {
            if (mainViewModel.playMode.value == 2) triggerAudioRandom()
            else triggerAudioForward()
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
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called")
        _binding = null
        requireContext().stopService(Intent(requireContext(), AudioPlayerService::class.java))
        requireContext().unregisterReceiver(broadcastReceiver)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun updatePlaylistOnSelectedAudioPlaylistId(id: Long) {
        try {
            if (id != -1L && mainViewModel.currentPlaylist.value.id != id) {
                if (id == 0L) {
                    mainViewModel.setCurrentAudios(mainViewModel.localAudios.value!!)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error from mini player: $e")
        }
    }

    private fun getAudioFromCurrentAudiosOnPosition(audio: Audio, forwardInt: Int): Audio {
        var selectedAudio = Audio.emptyAudio
        if (audio != Audio.emptyAudio && mainViewModel.hasAudioInLocal(audio)) {
            selectedAudio = audio
        }
        return selectedAudio
    }

    private fun triggerAudioPlayerService(audio: Audio, command: String) {
        val intentService = Intent(requireContext(), AudioPlayerService::class.java).apply {
            putExtra("AUDIO", audio)
            putExtra("MINI_PLAYER_COMMAND", command)
        }
        requireContext().startService(intentService)
        mainViewModel.setIsPlaying(true)
    }

    private fun addAudioToHistory(audio: Audio) {
        val time: Long = System.currentTimeMillis()
        mainViewModel.addNewAudioHistoryEntity(audio, time)
    }

    private fun setCurrentPlaylistAndCurrentAudios(audio: Audio) {
        try {
            if (audio.playlistId != 1L && audio.playlistId != mainViewModel.currentPlaylist.value.id) {
                if (audio.playlistId == 0L) {
                    mainViewModel.setCurrentPlaylist(Playlist.localPlaylist)
                    mainViewModel.setCurrentAudios(mainViewModel.localAudios.value!!)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error Mini Player: $e")
        }
    }

    private fun triggerPlayOrPausePlayer() {
        if (!mainViewModel.isPlaying.value) {
            if (!playerServiceState) {
                triggerAudioPlayerService(mainViewModel.currentAudio.value, "PLAY")
                playerServiceState = true
            }
            Util.broadcastAction(requireContext(), Constants.BROADCAST_ACTION_MINI_PLAYER_PLAY)
            mainViewModel.setIsPlaying(true)
        } else {
            Util.broadcastAction(requireContext(), Constants.BROADCAST_ACTION_MINI_PLAYER_PAUSE)
            mainViewModel.setIsPlaying(false)
        }
    }

    private fun triggerAudioForward() {
        val audio = mainViewModel.currentAudio.value
        setCurrentPlaylistAndCurrentAudios(mainViewModel.currentAudio.value)
        val pos = mainViewModel.getPositionOnAudioFromCurrentAudios(audio)
        val selectedAudio = mainViewModel.getAudioOnPositionFromCurrent(pos + 1)
        addAudioToHistory(selectedAudio)
        triggerAudioPlayerService(selectedAudio, "PLAY")
        mainViewModel.setIsPlaying(true)
    }

    private fun triggerAudioRandom() {
        setCurrentPlaylistAndCurrentAudios(mainViewModel.currentAudio.value)
        val pos = mainViewModel.getRandomPosition()
        val selectedAudio = mainViewModel.getAudioOnPositionFromLocal(pos)
        addAudioToHistory(selectedAudio)
        triggerAudioPlayerService(selectedAudio, "PLAY")
        mainViewModel.setIsPlaying(true)
    }
//
//    private fun setSelectedAudioAndUpdateHistory(audio: Audio) {
//        mainViewModel.setCurrentAudio(audio)
//        if (audio != Audio.emptyAudio) {
//            addAudioToHistory(audio)
//        }
//    }

    private fun triggerCallback() {
        listener?.callbackTriggerFragmentPlayer()
    }

    companion object {
        private const val TAG = "FragmentMiniPlayer"
    }
}