package Android.TestCollection.Earband.fragment

import Android.TestCollection.Earband.BroadcastUtil
import Android.TestCollection.Earband.Constants
import Android.TestCollection.Earband.adapter.AudioListAdapter
import Android.TestCollection.Earband.databinding.FragmentRecyclerViewAudioBinding
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

class FragmentRecyclerViewAudio : Fragment() {

    private var _binding: FragmentRecyclerViewAudioBinding? = null
    private val binding get() = _binding!!
    private lateinit var audioListAdapter: AudioListAdapter
    private val audioViewModel: AudioViewModel by activityViewModels()
    private val broadcastUtil = BroadcastUtil()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecyclerViewAudioBinding.inflate(inflater, container, false)

        audioListAdapter = AudioListAdapter { audio ->
            audioViewModel.getAudio(audio)
            broadcastUtil.broadcastNewAudioSelected(requireContext(), audio, Constants.BROADCAST_ACTION_AUDIO_SELECTED)
            val intent = Intent(requireContext(), AudioPlayerService::class.java).apply{
                putExtra("AUDIO", audio)
            }
            requireContext().startService(intent)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = audioListAdapter
        audioViewModel.loadAudios()
        audioViewModel.audios.observe(viewLifecycleOwner) { audios ->
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