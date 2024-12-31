package Android.TestCollection.Earband.activity

import Android.TestCollection.Earband.BroadcastUtil
import Android.TestCollection.Earband.Constants
import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.databinding.ActivityAudioPlayerBinding
import Android.TestCollection.Earband.model.Audio
import Android.TestCollection.Earband.viewModel.AudioPLayerViewModel
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

class AudioPlayerActivity : AppCompatActivity() {

    private val TAG = "AudioPlayActivity"

    private val broadcastUtil = BroadcastUtil()
    private lateinit var binding: ActivityAudioPlayerBinding
    private var isPLaying: Boolean = false
    private lateinit var broadcastReceiver: BroadcastReceiver
    private val audioPlayerViewModel: AudioPLayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate called")

        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val miniPlayerState = intent?.getStringExtra("MINI_PLAYER_STATE")
        if (miniPlayerState == "PLAYER_PLAY") {
            isPLaying = true
            binding.buttonAudioPlayImage.setImageResource(R.drawable.chevon_right)
        } else if (miniPlayerState == "PLAYER_PAUSE") {
            isPLaying = false
            binding.buttonAudioPlayImage.setImageResource(R.drawable.align_vertical)
        }

        binding.buttonAudioPlay.setOnClickListener {
            isPLaying = !isPLaying
            if (isPLaying) {
                binding.buttonAudioPlayImage.setImageResource(R.drawable.chevon_right)
                broadcastUtil.broadcastPlayerState(this, Constants.BROADCAST_ACTION_PLAYER_PLAY)
            } else {
                binding.buttonAudioPlayImage.setImageResource(R.drawable.align_vertical)
                broadcastUtil.broadcastPlayerState(this, Constants.BROADCAST_ACTION_PLAYER_PAUSE)
            }
        }

        binding.buttonAudioForward.setOnClickListener {
            broadcastUtil.broadcastPlayerState(this, Constants.BROADCAST_ACTION_PLAYER_FORWARD)
        }
        binding.buttonAudioBackward.setOnClickListener {
            broadcastUtil.broadcastPlayerState(this, Constants.BROADCAST_ACTION_PLAYER_BACKWARD)
        }

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Constants.BROADCAST_ACTION_AUDIO -> {
                        val audio: Audio
                        if (isAndroidVersionHigherOrEqualTiramisu()) {
                            audio = intent.getParcelableExtra("NEW_AUDIO", Audio::class.java) ?: Audio.emptyAudio
                        } else {
                            @Suppress("DEPRECATION")
                            audio = intent.getParcelableExtra("NEW_AUDIO") ?: Audio.emptyAudio
                        }
                        if (audio != Audio.emptyAudio) {
                            Util.triggerToast(this@AudioPlayerActivity, audio.title)
                            binding.textviewTitle.text = "TEst Title"
                        } else Util.triggerToast(this@AudioPlayerActivity, "No Audio Selected")
                    }
                }
            }
        }
        val intentFilter = IntentFilter().apply {
            addAction(Constants.BROADCAST_ACTION_AUDIO)
        }
        registerReceiver(broadcastReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
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
        unregisterReceiver(broadcastReceiver)
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart called")
    }

    private fun isAndroidVersionHigherOrEqualTiramisu(): Boolean {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU
    }
}