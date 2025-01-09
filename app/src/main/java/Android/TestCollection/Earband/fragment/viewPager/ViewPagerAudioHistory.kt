package Android.TestCollection.Earband.fragment.viewPager

import Android.TestCollection.Earband.Constants
import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.adapter.AudioHistoryListAdapter
import Android.TestCollection.Earband.application.AudioPlayerData
import Android.TestCollection.Earband.databinding.ViewPagerAudioHistoryBinding
import Android.TestCollection.Earband.viewModel.MainViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ViewPagerAudioHistory : Fragment() {

    @Inject
    lateinit var audioPlayerData: AudioPlayerData

    private var _binding: ViewPagerAudioHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var audioHistoryListAdapter: AudioHistoryListAdapter
    private val mainViewModel: MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ViewPagerAudioHistoryBinding.inflate(inflater, container, false)

        audioHistoryListAdapter = AudioHistoryListAdapter { audio ->
            mainViewModel.getAudio(audio)
            Util.broadcastNewAudio(requireContext(), audio, Constants.BROADCAST_ACTION_AUDIO_SELECTED)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = audioHistoryListAdapter

        mainViewModel.observableAudioHistoryEntityList.observe(viewLifecycleOwner) { _ ->
            mainViewModel.loadAudioHistoryList()
        }
        mainViewModel.audioHistoryList.observe(viewLifecycleOwner) { audios ->
            audioHistoryListAdapter.submitList(audios) {
                binding.recyclerView.scrollToPosition(0)
            }
        }

        return binding.root
    }
}