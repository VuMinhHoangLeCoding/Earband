package Android.TestCollection.Earband.fragment

import Android.TestCollection.Earband.Constants
import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.databinding.MuelFragmentPlayerBinding
import Android.TestCollection.Earband.viewModel.MainViewModel
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.OnBackPressedCallback
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

    private lateinit var broadcastReceiver: BroadcastReceiver

    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: MuelFragmentPlayerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = MuelFragmentPlayerBinding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.popBackStack()
                }
            }
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.currentAudio.collect { audio ->
                    binding.textTitle.text = audio.title
                    binding.seekbar.max = audio.duration.toInt()
                    binding.timerTotalDuration.text = Util.formatTime(audio.duration)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.isPlaying.collect { isPlaying ->
                    if (isPlaying) binding.buttonPlayImage.setImageResource(R.drawable.muel_icon_pause_wrap)
                    else binding.buttonPlayImage.setImageResource(R.drawable.muel_icon_play_wrap)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.playMode.collect { playMode ->
                    mainViewModel.appUtility!!.playMode = playMode
                    when (playMode) {
                        0 -> binding.buttonPlayMode.setImageResource(R.drawable.muel_icon_loop)
                        1 -> binding.buttonPlayMode.setImageResource(R.drawable.muel_icon_loop_current)
                        2 -> binding.buttonPlayMode.setImageResource(R.drawable.muel_icon_shuffle)
                    }
                }
            }
        }

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Constants.BROADCAST_ACTION_PLAYER_PROGRESSION -> {
                        val progress = intent.getLongExtra("PROGRESSION", 0L)
                        binding.seekbar.progress = progress.toInt()
                    }
                }
            }
        }
        val intentFilter = IntentFilter().apply {
            addAction(Constants.BROADCAST_ACTION_PLAYER_PROGRESSION)
        }
        if (Util.isAndroidVersionHigherOrEqualTiramisu()){
            requireContext().registerReceiver(broadcastReceiver, intentFilter, Context.RECEIVER_EXPORTED)
        }

        Glide.with(this)
            .load(R.drawable.guitarist)
            .transform(BlurTransformation(30, 3))
            .into(binding.backgroundImage)

        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.timerProgress.text = Util.formatTime(progress.toLong())
                if (fromUser) {
                    Util.broadcastPlayerProgress(
                        requireContext(),
                        Constants.BROADCAST_ACTION_SEEKBAR_PROGRESSION_CHANGES,
                        progress.toLong()
                    )
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val seekbarProgress = seekBar?.progress ?: 0
                Util.broadcastPlayerProgress(
                    requireContext(),
                    Constants.BROADCAST_ACTION_SEEKBAR_PROGRESSION_CHANGES,
                    seekbarProgress.toLong()
                )
            }
        })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")

        binding.buttonPlay.setOnClickListener {
            val isPlaying = mainViewModel.isPlaying.value
            if (!isPlaying) {
                Util.broadcastAction(requireContext(), Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_PLAY)
            } else {
                Util.broadcastAction(requireContext(), Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_PAUSE)
            }
        }

        binding.buttonForward.setOnClickListener {
            Util.broadcastAction(requireContext(), Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_FORWARD)
        }
        binding.buttonBackward.setOnClickListener {
            Util.broadcastAction(requireContext(), Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_BACKWARD)
        }

        binding.buttonPlayMode.setOnClickListener {
            when (mainViewModel.playMode.value) {
                0 -> {
                    mainViewModel.setObservablePlayMode(1)
                }

                1 -> {
                    mainViewModel.setObservablePlayMode(2)
                }

                2 -> {
                    mainViewModel.setObservablePlayMode(0)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause called")
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
        _binding = null
    }

    companion object {
        private const val TAG = "FragmentPlayer"
    }
}