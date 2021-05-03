package in_.co.innerpeacetech.exoplayerpoc

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.*
import in_.co.innerpeacetech.exoplayerpoc.SPHelper.set
import in_.co.innerpeacetech.exoplayerpoc.download.MyCacheDataSourceFactory
import in_.co.innerpeacetech.exoplayerpoc.download.SaveSong
import kotlinx.android.synthetic.main.activity_second.*
import java.io.File


val TAG = "myPlayer"
var permissions: Array<String> = arrayOf(
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)
val songurl =
    "https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_700KB.mp3"
val songurl1 = "https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_1MG.mp3"

lateinit var saveSong: SaveSong
lateinit var player: SimpleExoPlayer

class SecondActivity : AppCompatActivity(), CacheDataSource.EventListener, Player.EventListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
         player= SimpleExoPlayer.Builder(applicationContext).build()

        val mediaItem: MediaItem =
            MediaItem.fromUri(songurl)
        val mediaSource: MediaSource =
            getCachedMediaSourceFactory(this, this).createMediaSource(mediaItem)

        val mediaItem1: MediaItem =
            MediaItem.fromUri(songurl1)

        val mediaSource1: MediaSource =
            getCachedMediaSourceFactory(this, this).createMediaSource(mediaItem1)


        val concatinatingMediaSource = ConcatenatingMediaSource()
            concatinatingMediaSource.addMediaSource(mediaSource)
        concatinatingMediaSource.addMediaSource(mediaSource1)

        //    player.setMediaSource(mediaSource)
       player.setMediaSource(concatinatingMediaSource)

        player.playWhenReady = true
        player.addListener(this)




        playButton.setOnClickListener {
            if(player.isPlaying){
                player.pause()
            }else{
                player.play()

            }
        }
        refreshPlaylist.setOnClickListener {
            player.clearMediaItems()
            player.addMediaSource(mediaSource)
            player.prepare()
            player.playWhenReady = true
        }

        if (!hasPermissions(this, *permissions)) {
            reqPermissions()
        }else{
            player.prepare()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            when(requestCode){
                123 -> {
                    if ((grantResults.isNotEmpty() &&
                                grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    ) {
                        // Permission is granted.
                        player.prepare()
                    } else {
                        //permission denied.
                        Log.i(TAG, "onRequestPermissionsResult: DENIED")
                    }
                }
            }
    }

    private fun reqPermissions() {
        ActivityCompat.requestPermissions(
            this,
            permissions,
           123
        )
    }


    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    private var cachedMediaSourceFactory: ProgressiveMediaSource.Factory? = null


    private fun getCachedMediaSourceFactory(
        context: Context,
        listener: CacheDataSource.EventListener
    ): ProgressiveMediaSource.Factory {
        cachedMediaSourceFactory = ProgressiveMediaSource.Factory(
            MyCacheDataSourceFactory(
                context,
                listener,
                CacheDataSink(AudioCache.getInstance(this)!!, CacheDataSink.DEFAULT_FRAGMENT_SIZE)
            )
        )

        return cachedMediaSourceFactory as ProgressiveMediaSource.Factory
    }

  companion object{
      fun setListener(save: SaveSong){
          saveSong = save
      }
  }
    override fun onCachedBytesRead(cacheSizeBytes: Long, cachedBytesRead: Long) {
        Log.i(TAG, "onCachedBytesRead: $cacheSizeBytes $cachedBytesRead")
        if(cacheSizeBytes == cachedBytesRead){
          //  saveSong.save()
            Log.i(TAG, "onCachedBytesRead: CACHING DONE")
           // SPHelper.mPrefs(this)[SPHelper.SHOULD_SAVE] = true
         /*   val cache: SimpleCache? = AudioCache.getInstance(this)
            val cacheSpan: CacheSpan? = cache?.getCachedSpans(songurl)?.pollFirst()
            val cachedFile = cacheSpan?.file
            Log.i(TAG, "cached file size ${cachedFile?.length()} ")
            if (cacheSpan?.isCached == true && cachedFile?.exists() == true) {
                val mediaStorageDir = getExternalFilesDirs(null)
                val internalStorage =
                    mediaStorageDir[0].absolutePath // mediaStorageDir[1] will give external storage path
                val rootPath = internalStorage.substring(0, internalStorage.indexOf("Android"))
                val folder = File("$rootPath/CachedFolder") //creating a folder
                if (!folder.exists()) {
                    folder.mkdir()
                }
                val samplemp3 = File("${folder.absolutePath}/CM.mp3")
                if (!samplemp3.exists()) {
                    samplemp3.createNewFile()
                }
                cachedFile.copyTo(samplemp3)
            }*/
        }
    }


    override fun onCacheIgnored(reason: Int) {
        when (reason) {
            CacheDataSource.CACHE_IGNORED_REASON_ERROR -> Log.i(
                TAG,
                "onCacheIgnored: Lifecycle: Cache ignored due to error "
            )
            CacheDataSource.CACHE_IGNORED_REASON_UNSET_LENGTH -> Log.i(
                TAG,
                "onCacheIgnored: Lifecycle: Cache ignored due to unset length"
            )
        }
    }


    override fun onPlaybackStateChanged(state: Int) {
        super.onPlaybackStateChanged(state)
        val stateString: String
        when (state) {
            ExoPlayer.STATE_IDLE -> {
                stateString = "idle"
            }
            ExoPlayer.STATE_BUFFERING -> {
                stateString = "buffering"
                Log.i(TAG, stateString)

            }
            ExoPlayer.STATE_READY -> {
                stateString = "ready"
                Log.i(TAG, stateString)
                Toast.makeText(this, "Player READY", Toast.LENGTH_SHORT).show()
            }
            ExoPlayer.STATE_ENDED -> {
                stateString = "ended"
            }
            else -> {
                stateString = "unknown state"
            }
        }
        Log.i(TAG, "player state $stateString")
    }


}