package Android.TestCollection.Earband.fragment.viewPager

import Android.TestCollection.Earband.Constants
import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.adapter.HistoryListAdapter
import Android.TestCollection.Earband.viewModel.MainViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewPagerHistory : Fragment() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var historyListAdapter: HistoryListAdapter
    private val mainViewModel: MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = when (Util.theme) {
            "MUEL" -> inflater.inflate(R.layout.muel_viewpager_history, container, false)
            else -> inflater.inflate(R.layout.muel_viewpager_history, container, false)
        }

        initializeView(view)
        setupRecyclerView()

        return view
    }

    private fun initializeView(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view)
    }

    private fun setupRecyclerView() {
         historyListAdapter = HistoryListAdapter { audio ->
            Util.broadcastNewAudio(requireContext(), audio, Constants.BROADCAST_ACTION_AUDIO_SELECTED)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = historyListAdapter

        mainViewModel.audioHistoryList.observe(viewLifecycleOwner) { audios ->
            historyListAdapter.submitList(audios) {
                recyclerView.scrollToPosition(0)
            }
        }
    }
}