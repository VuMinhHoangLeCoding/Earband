package Android.TestCollection.Earband.ui.main

import Android.TestCollection.Earband.BroadcastAction
import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.view_model.MainViewModel
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
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentPlayer : Fragment() {

    private lateinit var buttonPlay: CardView
    private lateinit var buttonForward: CardView
    private lateinit var buttonBackward: CardView
    private lateinit var seekbar: SeekBar
    private lateinit var backgroundImage: ImageView
    private lateinit var timerProgress: TextView
    private lateinit var timerTotalDuration: TextView
    private lateinit var textTitle: TextView
    private lateinit var buttonPlayImage: ImageView
    private lateinit var buttonPlayMode: ImageButton
    private lateinit var broadcastReceiver: BroadcastReceiver

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = when (Util.theme) {
            "MUEL" -> inflater.inflate(R.layout.muel_fragment_player, container, false)
            "MUD" -> inflater.inflate(R.layout.mud_fragment_player, container, false)
            else -> inflater.inflate(R.layout.muel_fragment_player, container, false)
        }

        initializeView(view)
        setupStateFlowCollect()
        setupListener()
        setupBroadcastReceiver()
        setupCallback()

        Glide.with(this)
            .load(R.drawable.guitarist)
            .transform(BlurTransformation(30, 3))
            .into(backgroundImage)

        return view
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop called")
        lifecycleScope.launch(Dispatchers.IO) {
            mainViewModel.upsertUtility()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called")
        requireContext().unregisterReceiver(broadcastReceiver)
    }

    private fun initializeView(view: View) {
        buttonPlay = view.findViewById(R.id.button_play)
        buttonForward = view.findViewById(R.id.button_forward)
        buttonBackward = view.findViewById(R.id.button_backward)
        seekbar = view.findViewById(R.id.seekbar)
        backgroundImage = view.findViewById(R.id.background_image)
        timerProgress = view.findViewById(R.id.timer_progress)
        timerTotalDuration = view.findViewById(R.id.timer_total_duration)
        textTitle = view.findViewById(R.id.text_title)
        buttonPlayImage = view.findViewById(R.id.button_play_image)
        buttonPlayMode = view.findViewById(R.id.button_play_mode)
    }

    private fun setupStateFlowCollect() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.currentAudio.collect { audio ->
                    textTitle.text = audio.title
                    seekbar.max = audio.duration.toInt()
                    timerTotalDuration.text = Util.formatTime(audio.duration)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.isPlaying.collect { isPlaying ->
                    if (isPlaying) buttonPlayImage.setImageResource(R.drawable.muel_icon_pause_wrap)
                    else buttonPlayImage.setImageResource(R.drawable.muel_icon_play_wrap)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.playMode.collect { playMode ->
                    mainViewModel.appUtility!!.playMode = playMode
                    when (playMode) {
                        0 -> buttonPlayMode.setImageResource(R.drawable.muel_icon_loop)
                        1 -> buttonPlayMode.setImageResource(R.drawable.muel_icon_loop_current)
                        2 -> buttonPlayMode.setImageResource(R.drawable.muel_icon_shuffle)
                    }
                }
            }
        }
    }

    private fun setupListener() {
        buttonPlay.setOnClickListener {
            val isPlaying = mainViewModel.isPlaying.value
            if (!isPlaying) {
                Util.broadcastString(requireContext(), "PLAY", BroadcastAction.FROM_PLAYER)
            } else {
                Util.broadcastString(requireContext(), "PAUSE", BroadcastAction.FROM_PLAYER)
            }
        }

        buttonForward.setOnClickListener {
            Util.broadcastString(requireContext(), "FORWARD", BroadcastAction.FROM_PLAYER)
        }
        buttonBackward.setOnClickListener {
            Util.broadcastString(requireContext(), "BACKWARD", BroadcastAction.FROM_PLAYER)
        }

        buttonPlayMode.setOnClickListener {
            when (mainViewModel.playMode.value) {
                0 -> mainViewModel.setObservablePlayMode(1)
                1 -> mainViewModel.setObservablePlayMode(2)
                2 -> mainViewModel.setObservablePlayMode(0)
            }
        }

        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                timerProgress.text = Util.formatTime(progress.toLong())
                if (fromUser) {
                    Util.broadPlayerProgress(
                        requireContext(),
                        BroadcastAction.SEEKBAR_PROGRESSION_CHANGES,
                        progress.toLong()
                    )
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val seekbarProgress = seekBar?.progress ?: 0
                Util.broadPlayerProgress(
                    requireContext(),
                    BroadcastAction.SEEKBAR_PROGRESSION_CHANGES,
                    seekbarProgress.toLong()
                )
            }
        })
    }

    private fun setupBroadcastReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    BroadcastAction.PLAYER_PROGRESSION -> {
                        val progress = intent.getLongExtra("PROGRESSION", 0L)
                        seekbar.progress = progress.toInt()
                    }
                }
            }
        }
        val intentFilter = IntentFilter().apply {
            addAction(BroadcastAction.PLAYER_PROGRESSION)
        }
        if (Util.isAndroidVersionHigherOrEqualTiramisu()) {
            requireContext().registerReceiver(broadcastReceiver, intentFilter, Context.RECEIVER_EXPORTED)
        }
    }

    private fun setupCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.popBackStack()
                }
            }
        )
    }

    companion object {
        private const val TAG = "FragmentPlayer"
    }
}