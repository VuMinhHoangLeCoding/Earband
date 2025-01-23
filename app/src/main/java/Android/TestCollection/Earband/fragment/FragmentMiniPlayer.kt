package Android.TestCollection.Earband.fragment

import Android.TestCollection.Earband.Constants
import Android.TestCollection.Earband.FragmentListener
import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.Util
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
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.cardview.widget.CardView
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

    private lateinit var seekbar: SeekBar
    private lateinit var miniPlayer: CardView
    private lateinit var textTitle: TextView
    private lateinit var textArtist: TextView
    private lateinit var buttonPLay: ImageButton
    private lateinit var buttonForward: ImageButton
    private lateinit var broadcastReceiver: BroadcastReceiver

    private val mainViewModel: MainViewModel by activityViewModels()
    private var playerServiceState: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentListener) listener = context
        else throw ClassCastException("$context must implement FragmentListener")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView called")
        val view = when (Util.theme) {
            "MUEL" -> inflater.inflate(R.layout.muel_fragment_mini_player, container, false)
            else -> inflater.inflate(R.layout.muel_fragment_mini_player, container, false)
        }

        initializeView(view)
        setupClickListener()
        setupStateFlowCollect()
        setupBroadcastReceiver()

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called")
        requireContext().stopService(Intent(requireContext(), AudioPlayerService::class.java))
        requireContext().unregisterReceiver(broadcastReceiver)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun initializeView(view: View) {
        seekbar = view.findViewById(R.id.seekbar)
        miniPlayer = view.findViewById(R.id.mini_player)
        textTitle = view.findViewById(R.id.text_title)
        textArtist = view.findViewById(R.id.text_artist)
        buttonPLay = view.findViewById(R.id.button_play)
        buttonForward = view.findViewById(R.id.button_forward)
    }

    private fun setupStateFlowCollect() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.currentAudio.collect { audio ->
                    textTitle.text = audio.title
                    if (audio.artistName != "")
                        textArtist.text = audio.artistName else textArtist.setText(R.string.Unknown)
                    seekbar.max = audio.duration.toInt()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.isPlaying.collect { isPlaying ->
                    if (isPlaying) buttonPLay.setImageResource(R.drawable.muel_icon_pause_wrap)
                    else buttonPLay.setImageResource(R.drawable.muel_icon_play_wrap)
                }
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun setupBroadcastReceiver() {
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
                            val audioInLocal = mainViewModel.getAudioInLocal(selectedAudio)
                            if (audioInLocal == Audio.emptyAudio) Log.e(TAG, "Error MiniPlayer: BROADCAST_AUDIO_SELECTED")
                            else {
                                setCurrentPlaylistAndCurrentAudios(selectedAudio)
                                addAudioToHistory(selectedAudio)
                                triggerAudioPlayerService(audioInLocal, "PLAY")
                                triggerCallback()
                            }
                        }
                    }

                    Constants.BROADCAST_ACTION_FROM_PLAYER -> {
                        val mode = intent.getStringExtra("STRING")
                        when (mode) {
                            "PLAY" -> {
                                setCurrentPlaylistAndCurrentAudios(mainViewModel.currentAudio.value)
                                if (!mainViewModel.isPlaying.value) triggerPlayOrPausePlayer()
                            }

                            "PAUSE" -> {
                                if (mainViewModel.isPlaying.value) triggerPlayOrPausePlayer()
                            }

                            "FORWARD" -> {
                                if (mainViewModel.playMode.value == 2) triggerAudioRandom()
                                else triggerAudioForward()
                            }

                            "BACKWARD" -> {
                                triggerAudioPlayerService(Audio.emptyAudio, "BACKWARD")
                            }
                        }
                    }

                    Constants.BROADCAST_ACTION_CONFIRM_BACKWARD -> {
                        if (mainViewModel.playMode.value == 2) triggerAudioRandom()
                        else triggerAudioBackward()
                    }

                    Constants.BROADCAST_ACTION_AUDIO_ENDED -> {
                        if (mainViewModel.playMode.value == 2) triggerAudioRandom()
                        else triggerAudioForward()
                    }

                    Constants.BROADCAST_ACTION_PLAYER_PROGRESSION -> {
                        val progress = intent.getLongExtra("PROGRESSION", 0L)
                        seekbar.progress = progress.toInt()
                    }

                    Constants.BROADCAST_ACTION_SHUFFLE -> {
                        val shuffleFrom = intent.getLongExtra("SHUFFLE_FROM", -1L)
                        when (shuffleFrom) {
                            0L -> {
                                mainViewModel.setCurrentAudiosAsLocalAudios()
                                mainViewModel.setCurrentPlaylist(Playlist.localPlaylist)
                                val pos = mainViewModel.getRandomPositionFromCurrentAudios()
                                val audio = mainViewModel.getAudioOnPositionFromLocal(pos)
                                addAudioToHistory(audio)
                                triggerAudioPlayerService(audio, "PLAY")
                                triggerCallback()
                            }
                            else -> Log.e(TAG, "Error MiniPlayer: BROADCAST_ACTION_SHUFFLE")
                        }
                    }
                }
            }
        }
        val intentFilter = IntentFilter().apply {
            addAction(Constants.BROADCAST_ACTION_AUDIO_SELECTED)
            addAction(Constants.BROADCAST_ACTION_FROM_PLAYER)
            addAction(Constants.BROADCAST_ACTION_AUDIO_ENDED)
            addAction(Constants.BROADCAST_ACTION_CONFIRM_BACKWARD)
            addAction(Constants.BROADCAST_ACTION_PLAYER_PROGRESSION)
            addAction(Constants.BROADCAST_ACTION_SHUFFLE)
        }
        if (Util.isAndroidVersionHigherOrEqualTiramisu()) {
            requireContext().registerReceiver(broadcastReceiver, intentFilter, Context.RECEIVER_EXPORTED)
        }
        if (!Util.isAndroidVersionHigherOrEqualTiramisu()) {
            requireContext().registerReceiver(broadcastReceiver, intentFilter)
        }
    }

    private fun setupClickListener() {
        miniPlayer.setOnClickListener {
            triggerCallback()
        }

        buttonPLay.setOnClickListener {
            triggerPlayOrPausePlayer()
        }

        buttonForward.setOnClickListener {
            if (mainViewModel.playMode.value == 2) triggerAudioRandom()
            else triggerAudioForward()
        }
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
            Util.broadcastBoolean(requireContext(), true, Constants.BROADCAST_ACTION_FROM_MINI_PLAYER)
            mainViewModel.setIsPlaying(true)
        } else {
            Util.broadcastBoolean(requireContext(), false, Constants.BROADCAST_ACTION_FROM_MINI_PLAYER)
            mainViewModel.setIsPlaying(false)
        }
    }

    private fun triggerAudioForward() {
        val audio = mainViewModel.currentAudio.value
        setCurrentPlaylistAndCurrentAudios(audio)
        val pos = mainViewModel.getPositionOnAudioFromCurrentAudios(audio)
        val selectedAudio = mainViewModel.getAudioOnPositionFromCurrent(pos + 1)

        val audioInLocal = mainViewModel.getAudioInLocal(selectedAudio)
        if (audioInLocal == Audio.emptyAudio) Log.e(TAG, "Error miniPlayer: triggerAudioForward()")
        else {
            addAudioToHistory(selectedAudio)
            triggerAudioPlayerService(audioInLocal, "PLAY")
            mainViewModel.setIsPlaying(true)
        }
    }

    private fun triggerAudioBackward() {
        val currentAudio = mainViewModel.currentAudio.value
        val pos = mainViewModel.getPositionOnAudioFromCurrentAudios(currentAudio)
        val selectedAudio = mainViewModel.getAudioOnPositionFromCurrent(pos - 1)

        val audioInLocal = mainViewModel.getAudioInLocal(selectedAudio)
        if (audioInLocal == Audio.emptyAudio) Log.e(TAG, "Error miniPlayer: triggerAudioBackward()")
        else {
            addAudioToHistory(selectedAudio)
            triggerAudioPlayerService(audioInLocal, "PLAY")
            mainViewModel.setIsPlaying(true)
        }
    }

    private fun triggerAudioRandom() {
        setCurrentPlaylistAndCurrentAudios(mainViewModel.currentAudio.value)
        val pos = mainViewModel.getRandomPositionFromCurrentAudios()
        val selectedAudio = mainViewModel.getAudioOnPositionFromLocal(pos)

        val audioInLocal = mainViewModel.getAudioInLocal(selectedAudio)
        if (audioInLocal == Audio.emptyAudio) Log.e(TAG, "Error miniPlayer: triggerAudioRandom()")
        else {
            addAudioToHistory(selectedAudio)
            triggerAudioPlayerService(audioInLocal, "PLAY")
            mainViewModel.setIsPlaying(true)
        }
    }

    private fun triggerCallback() {
        listener?.callbackTriggerFragmentPlayer()
    }

    companion object {
        private const val TAG = "FragmentMiniPlayer"
    }
}