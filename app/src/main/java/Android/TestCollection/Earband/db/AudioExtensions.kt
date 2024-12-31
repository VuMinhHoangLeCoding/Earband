package Android.TestCollection.Earband.db

import Android.TestCollection.Earband.model.Audio

fun List<AudioHistoryEntity>.fromHistoryToAudios(): List<Audio> {
    return map {
        it.toAudio()
    }
}

fun List<AudioEntity>.toAudios(): List<Audio>{
    return map{
        it.toAudio()
    }
}

fun Audio.toAudioHistory(timePlayed: Long): AudioHistoryEntity {
    return AudioHistoryEntity(
        id = id,
        title = title,
        trackNumber = trackNumber,
        year = year,
        duration = duration,
        data = data,
        dateModified = dateModified,
        albumId = albumId,
        albumName = albumName,
        artistId = artistId,
        artistName = artistName,
        composer = composer,
        albumArtist = albumArtist,
        timePlayed = timePlayed,
        playlistId = playlistId,
        idInPlaylist = idInPlaylist
    )
}

fun Audio.toAudioEntity(playlistId: Long): AudioEntity {
    return AudioEntity(
        playlistCreatorId = playlistId,
        id = id,
        title = title,
        trackNumber = trackNumber,
        year = year,
        duration = duration,
        data = data,
        dateModified = dateModified,
        albumId = albumId,
        albumName = albumName,
        artistId = artistId,
        artistName = artistName,
        composer = composer,
        albumArtist = albumArtist,
    )
}

fun AudioEntity.toAudio(): Audio {
    return Audio(
        id = id,
        title = title,
        trackNumber = trackNumber,
        year = year,
        duration = duration,
        data = data,
        dateModified = dateModified,
        albumId = albumId,
        albumName = albumName,
        artistId = artistId,
        artistName = artistName,
        composer = composer,
        albumArtist = albumArtist,
        playlistId = playlistCreatorId,
        idInPlaylist = songPrimaryKey
    )
}

fun AudioHistoryEntity.toAudio(): Audio{
    return Audio(
        id = id,
        title = title,
        trackNumber = trackNumber,
        year = year,
        duration = duration,
        data = data,
        dateModified = dateModified,
        albumId = albumId,
        albumName = albumName,
        artistId = artistId,
        artistName = artistName,
        composer = composer,
        albumArtist = albumArtist,
        playlistId = playlistId,
        idInPlaylist = idInPlaylist
    )
}