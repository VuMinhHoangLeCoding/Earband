package Android.TestCollection.Earband.activity

import Android.TestCollection.Earband.Constants
import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.application.AudioPlayerData
import Android.TestCollection.Earband.databinding.ActivityAudioPlayerBinding
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private lateinit var broadcastReceiver: BroadcastReceiver

    @Inject
    lateinit var audioPlayerData: AudioPlayerData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate called")


        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                audioPlayerData.selectedAudio.collect { audio ->
                    binding.textviewTitle.text = audio.title
                    binding.seekbar.max = audio.duration.toInt()
                    binding.timerTotalDuration.text = Util.formatTime(audio.duration)
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                audioPlayerData.isPlaying.collect { isPlaying ->
                    if (isPlaying) binding.buttonAudioPlayImage.setImageResource(R.drawable.audio_pause_black)
                    else binding.buttonAudioPlayImage.setImageResource(R.drawable.audio_play_black)
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

                    Constants.BROADCAST_ACTION_AUDIO_FOCUS_LOSS -> {
                    }
                }
            }
        }
        val intentFilter = IntentFilter().apply {
            addAction(Constants.BROADCAST_ACTION_PLAYER_PROGRESSION)
            addAction(Constants.BROADCAST_ACTION_AUDIO_FOCUS_LOSS)
        }
        registerReceiver(broadcastReceiver, intentFilter, Context.RECEIVER_EXPORTED)

        Glide.with(this)
            .load(R.drawable.disk_black_background_monotone_light_ginger)
            .transform(BlurTransformation(30, 3))
            .into(binding.backgroundImage)

        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.timerCurrentProgression.text = Util.formatTime(progress.toLong())
                if (fromUser) {
                    Util.broadcastPlayerProgress(this@AudioPlayerActivity, Constants.BROADCAST_ACTION_SEEKBAR_PROGRESSION_CHANGES, progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val seekbarProgress = seekBar?.progress ?: 0
                Util.broadcastPlayerProgress(
                    this@AudioPlayerActivity,
                    Constants.BROADCAST_ACTION_SEEKBAR_PROGRESSION_CHANGES,
                    seekbarProgress.toLong()
                )
            }
        })


    }

    override fun onStart() {
        super.onStart()

        Log.d(TAG, "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")

        binding.buttonAudioPlay.setOnClickListener {
            val isPlaying = audioPlayerData.isPlaying.value
            if (!isPlaying) {
                Util.broadcastState(this, Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_PLAY)
            } else {
                Util.broadcastState(this, Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_PAUSE)
            }
        }

        binding.buttonAudioForward.setOnClickListener {
            Util.broadcastState(this, Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_FORWARD)
        }
        binding.buttonAudioBackward.setOnClickListener {
            Util.broadcastState(this, Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_BACKWARD)
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
        unregisterReceiver(broadcastReceiver)
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart called")
    }

    companion object {
        private const val TAG = "AudioPlayActivity"
    }
}