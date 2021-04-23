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
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.CacheDataSink
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import in_.co.innerpeacetech.exoplayerpoc.SPHelper.set
import in_.co.innerpeacetech.exoplayerpoc.download.MyCacheDataSourceFactory
import kotlinx.android.synthetic.main.activity_second.*


val TAG = "myPlayer"
var permissions: Array<String> = arrayOf(
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)
val songurl =
    "https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_700KB.mp3"

lateinit var dataSink: DataSink
class SecondActivity : AppCompatActivity(), CacheDataSource.EventListener, Player.EventListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

     
        val player: SimpleExoPlayer = SimpleExoPlayer.Builder(applicationContext).build()


        dataSink =   CacheDataSink(AudioCache.getInstance(this)!!, 8*1024) // 8mb cache

        val mediaItem: MediaItem =
            MediaItem.fromUri(songurl)

        val mediaSource: MediaSource =
            getCachedMediaSourceFactory(this, this).createMediaSource(mediaItem)


        player.setMediaSource(mediaSource)
        player.playWhenReady = false
        player.prepare()
        player.addListener(this)
        playButton.setOnClickListener {
            if(player.isPlaying){
                player.pause()
            }else{
                player.play()

            }
        }


        if (!hasPermissions(this, *permissions)) {
            reqPermissions()
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
                dataSink
            )
        )

        return cachedMediaSourceFactory as ProgressiveMediaSource.Factory
    }


    override fun onCachedBytesRead(cacheSizeBytes: Long, cachedBytesRead: Long) {
        Log.i(TAG, "onCachedBytesRead: $cacheSizeBytes $cachedBytesRead")
        if(cacheSizeBytes == cachedBytesRead){
            Log.i(TAG, "onCachedBytesRead: CACHING DONE")
            SPHelper.mPrefs(this)[SPHelper.SHOULD_SAVE] = true
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