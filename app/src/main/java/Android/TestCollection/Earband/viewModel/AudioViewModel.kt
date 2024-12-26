package Android.TestCollection.Earband.viewModel

import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.model.Audio
import Android.TestCollection.Earband.repository.AudioRepository
import Android.TestCollection.Earband.repository.RealAudioRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class AudioViewModel(application: Application) : AndroidViewModel(application) {
    private val audioRepository: AudioRepository = RealAudioRepository(application)

    private val _audios = MutableLiveData<List<Audio>>()
    val audios: LiveData<List<Audio>> = _audios

    fun loadAudios() {
        _audios.value = audioRepository.audios()
        val list = _audios.value
        val audioCount = list?.size
        Util.triggerToast(getApplication(), "Number of audios: $audioCount")
    }

}