package Android.TestCollection.Earband.ui.main

import Android.TestCollection.Earband.BroadcastAction
import Android.TestCollection.Earband.CallbackDropdownMenu
import Android.TestCollection.Earband.CallbackMainShuffle
import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.adapter.AudioListAdapter
import Android.TestCollection.Earband.model.Audio
import Android.TestCollection.Earband.service.AudioPlayerService
import Android.TestCollection.Earband.view_model.MainViewModel
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
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
            "MUD" -> inflater.inflate(R.layout.mud_viewpager_audio, container, false)
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
        audioListAdapter = AudioListAdapter(
            onItemClicked = { audio ->
                Util.broadcastNewAudio(requireContext(), audio, BroadcastAction.AUDIO_SELECTED)
            },

            onMenuButtonClicked = { audio, view ->
                showDropdownContextMenu(audio, view)
            }
        )



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
                val intent = Intent(BroadcastAction.SHUFFLE).apply {
                    putExtra("SHUFFLE_FROM", 0L)
                }
                requireContext().sendBroadcast(intent)
            }
        })

        fragmentTaskbarAboveViewPager.setDropdownMenuButtonCallback(object : CallbackDropdownMenu {
            override fun triggerOpenDropdownMenu() {
                val buttonDropdownMenu = fragmentTaskbarAboveViewPager.view?.findViewById<View>(R.id.button_dropdown_menu)
                buttonDropdownMenu?.let {
                    showDropdownMenu(it)
                }
            }
        })
    }

    private fun showDropdownMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.menu_dropdown, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.sort_name -> {
                    false
                }

                R.id.sort_new -> {
                 false
                }

                R.id.sort_date_modified -> {
                    false
                }

                R.id.sort_length -> {
                    false
                }
                else -> {
                    false
                }
            }
        }
        popupMenu.show()
    }

    private fun showDropdownContextMenu(audio: Audio, view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.menu_dropdown_recycler_view_item, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.favourite_audio -> {
                    false
                }

                else -> {
                    false
                }
            }
        }
        popupMenu.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val intent = Intent(requireContext(), AudioPlayerService::class.java)
        requireContext().stopService(intent)
    }
}