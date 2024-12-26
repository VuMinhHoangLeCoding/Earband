package Android.TestCollection.Earband.fragment

import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.adapter.AudioListAdapter
import Android.TestCollection.Earband.databinding.FragmentRecyclerViewAudioBinding
import Android.TestCollection.Earband.viewModel.AudioViewModel
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecyclerViewAudioBinding.inflate(inflater, container, false)

        audioListAdapter = AudioListAdapter { audio ->

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
        _binding = null
    }
}