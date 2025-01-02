package Android.TestCollection.Earband.fragment.viewPager

import Android.TestCollection.Earband.Constants
import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.adapter.AudioListAdapter
import Android.TestCollection.Earband.databinding.ViewPagerRecyclerViewAudioBinding
import Android.TestCollection.Earband.service.AudioPlayerService
import Android.TestCollection.Earband.viewModel.AudioViewModel
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager

class ViewPagerRecyclerViewAudio : Fragment() {

    private var _binding: ViewPagerRecyclerViewAudioBinding? = null
    private val binding get() = _binding!!
    private lateinit var audioListAdapter: AudioListAdapter
    private val audioViewModel: AudioViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ViewPagerRecyclerViewAudioBinding.inflate(inflater, container, false)

        audioListAdapter = AudioListAdapter { audio ->
            audioViewModel.getAudio(audio)
            Util.broadcastNewAudio(requireContext(), audio, Constants.BROADCAST_ACTION_AUDIO_SELECTED)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = audioListAdapter
        audioViewModel.loadAudios()
        audioViewModel.loalAudios.observe(viewLifecycleOwner) { audios ->
            audioListAdapter.submitList(audios)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val intent = Intent(requireContext(), AudioPlayerService::class.java)
        requireContext().stopService(intent)
        _binding = null
    }
}