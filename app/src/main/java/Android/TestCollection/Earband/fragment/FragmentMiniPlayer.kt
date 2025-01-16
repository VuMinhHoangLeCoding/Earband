package Android.TestCollection.Earband.fragment

import Android.TestCollection.Earband.Constants
import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.activity.AudioPlayerActivity
import Android.TestCollection.Earband.application.AppPlayerDataModel
import Android.TestCollection.Earband.databinding.FragmentMiniPlayerBinding
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
import javax.inject.Inject

@AndroidEntryPoint
class FragmentMiniPlayer : Fragment() {

    @Inject
    lateinit var appPlayerDataModel: AppPlayerDataModel

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
                appPlayerDataModel.selectedAudio.collect { audio ->
                    binding.textViewTitle.text = audio.title
                    if (!audio.composer.isNullOrEmpty()) binding.textViewComposer.text =
                        audio.composer else binding.textViewComposer.setText(R.string.Unknown)
                    binding.seekbar.max = audio.duration.toInt()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                appPlayerDataModel.isPlaying.collect { isPlaying ->
                    if (isPlaying) binding.audioPlayButton.setImageResource(R.drawable.pause_icon_wrap)
                    else binding.audioPlayButton.setImageResource(R.drawable.play_icon_wrap)
                }
            }
        }

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Constants.BROADCAST_ACTION_AUDIO_SELECTED -> {
                        val selectedAudio = appPlayerDataModel.selectedAudio.value
                        updatePlaylistOnSelectedAudioPlaylistId(selectedAudio.playlistId)
                        val audio = getAudioFromPlaylist(selectedAudio, 0)

                        if (audio != Audio.emptyAudio) {

                            setSelectedAudioAndUpdateHistory(audio)

                            triggerAudioPlayerService("PLAY")
                            triggerPlayerActivity()

                        } else if (audio == Audio.emptyAudio) {
                            Log.e(TAG, "Unknown Error occurred!")
                        }
                    }

                    Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_PLAY -> {
                        if (!appPlayerDataModel.isPlaying.value) triggerPlayOrPausePlayer()
                    }

                    Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_PAUSE -> {
                        if (appPlayerDataModel.isPlaying.value) triggerPlayOrPausePlayer()
                    }

                    Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_FORWARD -> {
                        if (appPlayerDataModel.getPlayModeValue() == 2) triggerAudioRandom()
                        else triggerAudioForward()
                    }

                    Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_BACKWARD -> {
                        triggerAudioPlayerService("BACKWARD")
                    }

                    Constants.BROADCAST_ACTION_PLAYER_BACKWARD -> {
                        if (appPlayerDataModel.getPlayModeValue() == 2) triggerAudioRandom()
                        else {
                            val currentAudio = appPlayerDataModel.getSelectedAudio()
                            val selectedAudio = getAudioFromPlaylist(currentAudio, -1)
                            setSelectedAudioAndUpdateHistory(selectedAudio)
                            triggerAudioPlayerService("PLAY")
                        }
                    }

                    Constants.BROADCAST_ACTION_PLAYER_ENDED -> {
                        triggerAudioForward()
                    }

                    Constants.BROADCAST_ACTION_PLAYER_PROGRESSION -> {
                        val progress = intent.getLongExtra("PROGRESSION", 0L)
                        binding.seekbar.progress = progress.toInt()
                    }

                    Constants.BROADCAST_ACTION_SHUFFLE -> {
                        val shuffleFrom = intent.getStringExtra("SHUFFLE_FROM")
                        when (shuffleFrom) {
                            "LOCAL" -> {
                                updatePlaylistOnSelectedAudioPlaylistId(0L)
                                val pos = appPlayerDataModel.getRandomPosition()
                                val audio = appPlayerDataModel.getAudioFromPosition(pos)
                                setSelectedAudioAndUpdateHistory(audio)
                                triggerAudioPlayerService("PLAY")
                            }
                        }
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
                if (appPlayerDataModel.isPlaying.value) {
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
            if(appPlayerDataModel.getPlayModeValue() == 2) triggerAudioRandom()
            else triggerAudioForward()
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

    private fun updatePlaylistOnSelectedAudioPlaylistId(id: Long) {
        if (id != -1L) {
            val currentPlaylistId = appPlayerDataModel.getCurrentPlaylist().id
            if (currentPlaylistId != id) {
                if (id == 0L) {
                    appPlayerDataModel.setAudios(mainViewModel.getLocalAudios())
                    appPlayerDataModel.setCurrentPlaylist(Playlist.localPlaylist)
                    if (appPlayerDataModel.currentAudios.value.isEmpty() || appPlayerDataModel.getCurrentPlaylist() == Playlist.emptyPlaylist)
                        Log.e(TAG, "Playlist is empty")
                    else Log.d(TAG, "playlist set")
                } else Log.e(TAG, "Some error")
            } else Log.e(TAG, "$currentPlaylistId")
        } else Log.e(TAG, "No Audio selected")
    }

    private fun getAudioFromPlaylist(audio: Audio, forwardInt: Int): Audio {
        var selectedAudio = Audio.emptyAudio
        if (audio != Audio.emptyAudio) {
            if (appPlayerDataModel.hasAudio(audio)) {
                if (appPlayerDataModel.getAudioPosition(audio) != -1) {
                    selectedAudio = appPlayerDataModel.getAudioFromPosition(appPlayerDataModel.getAudioPosition(audio) + forwardInt)
                } else Log.e(TAG, "No Audio Position")
            } else Log.e(TAG, "No Audio in Data Model Playlist")
        } else Log.e(TAG, "Audio is empty")
        return selectedAudio
    }

    private fun triggerAudioPlayerService(command: String) {
        val intentService = Intent(requireContext(), AudioPlayerService::class.java).apply {
            putExtra("MINI_PLAYER_COMMAND", command)
        }
        requireContext().startService(intentService)
    }

    private fun triggerPlayerActivity() {
        val intentActivity = Intent(requireContext(), AudioPlayerActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }
        requireContext().startActivity(intentActivity)
    }

    private fun addAudioToHistory(audio: Audio) {
        val time: Long = System.currentTimeMillis()
        mainViewModel.addNewAudioHistoryEntity(audio, time)
    }

    private fun triggerPlayOrPausePlayer() {
        if (!appPlayerDataModel.isPlaying.value) {
            if (!playerServiceState) {
                triggerAudioPlayerService("PLAY")
                playerServiceState = true
            }
            Util.broadcastState(requireContext(), Constants.BROADCAST_ACTION_MINI_PLAYER_PLAY)
        } else {
            Util.broadcastState(requireContext(), Constants.BROADCAST_ACTION_MINI_PLAYER_PAUSE)
        }
    }

    private fun triggerAudioForward() {
        val audio = appPlayerDataModel.getSelectedAudio()
        val selectedAudio = getAudioFromPlaylist(audio, 1)
        setSelectedAudioAndUpdateHistory(selectedAudio)
        triggerAudioPlayerService("PLAY")
    }

    private fun triggerAudioRandom() {
        val pos = appPlayerDataModel.getRandomPosition()
        val selectedAudio = appPlayerDataModel.getAudioFromPosition(pos)
        setSelectedAudioAndUpdateHistory(selectedAudio)
        triggerAudioPlayerService("PLAY")
    }

    private fun setSelectedAudioAndUpdateHistory(audio: Audio) {
        appPlayerDataModel.setSelectedAudio(audio)
        if (audio != Audio.emptyAudio) {
            addAudioToHistory(audio)
        }
    }

    companion object {
        private const val TAG = "FragmentMiniPlayer"
    }
}