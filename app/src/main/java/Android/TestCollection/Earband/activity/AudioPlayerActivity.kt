package Android.TestCollection.Earband.activity

import Android.TestCollection.Earband.Constants
import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.application.AppAudioPlayerData
import Android.TestCollection.Earband.databinding.ActivityAudioPlayerBinding
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private var isPLaying: Boolean = false

    @Inject
    lateinit var appAudioPlayerData: AppAudioPlayerData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate called")


        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                appAudioPlayerData.selectedAudio.collect { audio ->
                    binding.textviewTitle.text = audio.title
                }
            }
        }

        binding.textviewTitle.text = appAudioPlayerData.getSelectedAudio().title

        val miniPlayerState = intent?.getStringExtra("MINI_PLAYER_STATE")
        if (miniPlayerState == "PLAY") {
            isPLaying = true
            binding.buttonAudioPlayImage.setImageResource(R.drawable.chevon_right)
        } else if (miniPlayerState == "PAUSE") {
            isPLaying = false
            binding.buttonAudioPlayImage.setImageResource(R.drawable.align_vertical)
        }

        binding.buttonAudioPlay.setOnClickListener {
            isPLaying = !isPLaying
            if (isPLaying) {
                binding.buttonAudioPlayImage.setImageResource(R.drawable.chevon_right)
                Util.broadcastState(this, Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_PLAY)
            } else {
                binding.buttonAudioPlayImage.setImageResource(R.drawable.align_vertical)
                Util.broadcastState(this, Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_PAUSE)
            }
        }

        binding.buttonAudioForward.setOnClickListener {
            Util.broadcastState(this, Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_FORWARD)
            isPLaying = true
            binding.buttonAudioPlayImage.setImageResource(R.drawable.chevon_right)
        }
        binding.buttonAudioBackward.setOnClickListener {
            Util.broadcastState(this, Constants.BROADCAST_ACTION_PLAYER_ACTIVITY_BACKWARD)
            isPLaying = true
            binding.buttonAudioPlayImage.setImageResource(R.drawable.chevon_right)
        }


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
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart called")
    }

    companion object {
        private const val TAG = "AudioPlayActivity"
    }
}