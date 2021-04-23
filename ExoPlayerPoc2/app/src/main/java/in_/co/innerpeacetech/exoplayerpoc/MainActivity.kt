package in_.co.innerpeacetech.exoplayerpoc

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.offline.DownloadRequest
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.material.button.MaterialButton
import in_.co.innerpeacetech.exoplayerpoc.download.MyDownloadService
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<MaterialButton>(R.id.btn1).setOnClickListener { view ->
            doManualDownload()
        }

        init1()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun init1() {
        val player: SimpleExoPlayer = SimpleExoPlayer.Builder(applicationContext).build()
        val mediaItem: MediaItem =
            MediaItem.fromUri("https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_700KB.mp3")
        // Set the media item to be played.
        player.setMediaItem(mediaItem);
// Prepare the player.
        player.prepare();
// Start the playback.
        player.play();

    }

    fun initForDownloads() {
        val player1: SimpleExoPlayer = SimpleExoPlayer.Builder(applicationContext).build()
        val mediaItem: MediaItem =
            MediaItem.fromUri("https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_700KB.mp3")
        // Set the media item to be played.
        player1.setMediaItem(mediaItem);
// Prepare the player.
        player1.prepare();
// Start the playback.
        player1.play();

      /*  val cacheDataSourceFactory: DataSource.Factory =
            CacheDataSource.Factory()
                .setCache(downloadCache)
                .setUpstreamDataSourceFactory(httpDataSourceFactory)
                .setCacheWriteDataSinkFactory(null); // Disable writing.

        val player = SimpleExoPlayer.Builder(applicationContext)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(cacheDataSourceFactory)
            )
            .build()*/
    }

    fun doManualDownload() {
        val songId: String = "1"
        val sourceUri: Uri =
            Uri.parse("https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_700KB.mp3");
        val destinationUri: Uri =
            Uri.fromFile(File(applicationContext.filesDir.absolutePath + "/test.mp3"));
        val downloadRequest: DownloadRequest =
            DownloadRequest.Builder(songId, destinationUri).build();
        DownloadService.sendAddDownload(
            applicationContext,
            MyDownloadService::class.java, downloadRequest, false
        )
    }
}