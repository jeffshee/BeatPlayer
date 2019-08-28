package com.crrl.beatplayer.ui.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crrl.beatplayer.models.Album
import com.crrl.beatplayer.models.Playlist
import com.crrl.beatplayer.models.SearchData
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.repository.AlbumRepository
import com.crrl.beatplayer.repository.ArtistRepository
import com.crrl.beatplayer.repository.PlaylistRepository
import com.crrl.beatplayer.repository.SongsRepository
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

@SuppressLint("CheckResult")
class SongViewModel(private val context: Context?) : ViewModel() {

    private val songs: MutableLiveData<List<Song>> = MutableLiveData()
    private val _songsByPlaylist: MutableLiveData<List<Song>> = MutableLiveData()
    private val _playlistLiveData: MutableLiveData<List<Playlist>> = MutableLiveData()
    private val _songsByAlbum: MutableLiveData<List<Song>> = MutableLiveData()
    private val _artistLiveData: MutableLiveData<List<Album>> = MutableLiveData()
    private val searchData = SearchData()
    private val _searchLiveData: MutableLiveData<SearchData> = MutableLiveData()

    val searchLiveData = _searchLiveData

    fun search(searchString: String) {
        if (searchString.length >= 3) {
            Observable.fromCallable {
                searchData.apply {
                    val songs =
                        SongsRepository(context).searchSongs(searchString, 10).toMutableList()
                    val albums = AlbumRepository.getInstance(context)!!.search(searchString, 10)
                        .toMutableList()
                    val artists = ArtistRepository.getInstance(context)!!.search(searchString, 10)
                        .toMutableList()
                    if (songs.isNotEmpty())
                        songList = songs
                    if (albums.isNotEmpty())
                        albumList = albums
                    if (artists.isNotEmpty())
                        artistList = artists
                }
            }.observeOn(Schedulers.newThread()).subscribeOn(Schedulers.newThread())
                .subscribe { _searchLiveData.postValue(it) }
        } else {
            _searchLiveData.postValue(searchData.flush())
        }
    }

    fun liveData(): LiveData<List<Song>> = songs

    fun update() {
        Observable.fromCallable { SongsRepository(context).loadSongs() }
            .observeOn(Schedulers.newThread()).subscribeOn(Schedulers.newThread())
            .subscribe { songs.postValue(it) }
    }

    fun songsByPlayList(id: Long): LiveData<List<Song>> {
        Observable.fromCallable { PlaylistRepository.getInstance(context)!!.getSongsInPlaylist(id) }
            .observeOn(Schedulers.newThread()).subscribeOn(Schedulers.newThread())
            .subscribe { _songsByPlaylist.postValue(it) }
        return _songsByPlaylist
    }

    fun addToPlaylist(playlistId: Long, ids: LongArray) {
        PlaylistRepository.getInstance(context)!!.addToPlaylist(playlistId, ids)
    }

    fun playLists(): LiveData<List<Playlist>> {
        Observable.fromCallable { PlaylistRepository.getInstance(context)!!.getPlaylists() }
            .observeOn(Schedulers.newThread()).subscribeOn(Schedulers.newThread())
            .subscribe { _playlistLiveData.postValue(it) }
        return _playlistLiveData
    }

    fun getSongsByAlbum(id: Long): LiveData<List<Song>>? {
        Observable.fromCallable { AlbumRepository.getInstance(context)!!.getSongsForAlbum(id) }
            .subscribeOn(Schedulers.newThread()).subscribe { _songsByAlbum.postValue(it) }
        return _songsByAlbum
    }

    fun getArtistAlbums(artistId: Long): LiveData<List<Album>> {
        Observable.fromCallable {
            ArtistRepository.getInstance(context)!!.getAlbumsForArtist(artistId)
        }.observeOn(Schedulers.newThread()).subscribeOn(Schedulers.newThread())
            .subscribe { _artistLiveData.postValue(it) }
        return _artistLiveData
    }

}