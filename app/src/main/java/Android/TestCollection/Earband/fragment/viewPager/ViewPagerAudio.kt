package Android.TestCollection.Earband.fragment.viewPager

import Android.TestCollection.Earband.CallbackMainShuffle
import Android.TestCollection.Earband.Constants
import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.adapter.AudioListAdapter
import Android.TestCollection.Earband.fragment.FragmentTaskbarAboveViewPager
import Android.TestCollection.Earband.service.AudioPlayerService
import Android.TestCollection.Earband.viewModel.MainViewModel
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewPagerAudio : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fragmentBody: FragmentContainerView

    private lateinit var audioListAdapter: AudioListAdapter
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = when (Util.theme) {
            "MUEL" -> inflater.inflate(R.layout.muel_viewpager_audio, container, false)
            else -> inflater.inflate(R.layout.muel_viewpager_audio, container, false)
        }

        initializeView(view)
        setupRecyclerView()
        setupTaskbarAboveViewpager()

        return view
    }

    private fun initializeView(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view)
        fragmentBody = view.findViewById(R.id.fragment_body)
    }

    private fun setupRecyclerView() {
        audioListAdapter = AudioListAdapter { audio ->
            Util.broadcastNewAudio(requireContext(), audio, Constants.BROADCAST_ACTION_AUDIO_SELECTED)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = audioListAdapter
        mainViewModel.localAudios.observe(viewLifecycleOwner) { audios ->
            audioListAdapter.submitList(audios)
        }
    }

    private fun setupTaskbarAboveViewpager() {
        val fragmentTaskbarAboveViewPager = FragmentTaskbarAboveViewPager()
        val fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.replace(fragmentBody.id, fragmentTaskbarAboveViewPager)
        fragmentTransaction.commit()

        fragmentTaskbarAboveViewPager.setShuffleButtonCallback(object : CallbackMainShuffle {
            override fun triggerShuffle() {
                val intent = Intent(Constants.BROADCAST_ACTION_SHUFFLE).apply {
                    putExtra("SHUFFLE_FROM", "LOCAL")
                }
                requireContext().sendBroadcast(intent)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val intent = Intent(requireContext(), AudioPlayerService::class.java)
        requireContext().stopService(intent)
    }
}