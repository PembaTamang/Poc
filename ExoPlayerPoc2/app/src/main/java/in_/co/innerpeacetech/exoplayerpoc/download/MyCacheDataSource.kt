package in_.co.innerpeacetech.exoplayerpoc.download

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.Nullable
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.TransferListener
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import in_.co.innerpeacetech.exoplayerpoc.SPHelper
import in_.co.innerpeacetech.exoplayerpoc.SPHelper.set
import in_.co.innerpeacetech.exoplayerpoc.SecondActivity
import in_.co.innerpeacetech.exoplayerpoc.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.*
import kotlin.math.roundToInt


class MyCacheDataSource(
    private val cacheDataSource: CacheDataSource,
    private val context: Context
) :
    DataSource {
   override fun read(target: ByteArray, offset: Int, length1: Int): Int {
        return cacheDataSource.read(target, offset, length1)
    }

    override fun addTransferListener(transferListener: TransferListener) {
        cacheDataSource.addTransferListener(transferListener)
    }

    @Throws(IOException::class)
    override fun open(dataSpec: DataSpec): Long {
        return cacheDataSource.open(
            dataSpec
                .buildUpon()
                .setFlags(dataSpec.flags and DataSpec.FLAG_DONT_CACHE_IF_LENGTH_UNKNOWN.inv())
                .build()
        )
    }



    @Nullable
    override fun getUri(): Uri? {
        return cacheDataSource.uri
    }

    @Throws(IOException::class)
    override fun close() {
        cacheDataSource.close()
    }


    private fun saveCache(dataSpec: DataSpec) {
        val mediaStorageDir = context.getExternalFilesDirs(null)
        val internalStorage =
            mediaStorageDir[0].absolutePath // mediaStorageDir[1] will give external storage path
        val rootPath = internalStorage.substring(0, internalStorage.indexOf("Android"))
        val folder = File("$rootPath/CachedFolder") //creating a folder
        if (!folder.exists()) {
            folder.mkdir()
        }
        val samplemp3 = File("${folder.absolutePath}/cachedMusicfile.mp3")
        if (!samplemp3.exists()) {
            samplemp3.createNewFile()
        }

        var outFile: FileOutputStream? = null
        try {
            outFile = FileOutputStream(samplemp3)
            val data = ByteArray(1024)
            val totalBytesToRead = cacheDataSource.open(dataSpec)
            var bytesRead = 0
            var totalBytesRead = 0L
            while (bytesRead != C.RESULT_END_OF_INPUT) {
                bytesRead = cacheDataSource.read(data, 0, data.size)
                if (bytesRead != C.RESULT_END_OF_INPUT) {
                    outFile.write(data, 0, bytesRead)
                    if (totalBytesToRead == C.LENGTH_UNSET.toLong()) {
                        // Length of video in not known. Do something different here.
                    } else {
                        totalBytesRead += bytesRead
                        Log.i(
                            TAG,
                            "Save Progress: %d %%".format(((totalBytesRead.toDouble() / totalBytesToRead.toDouble()) * 100).roundToInt())
                        )
                    }
                }
            }

        } catch (e: IOException) {
            // error processing
        } finally {

            outFile?.flush()
            outFile?.close()
        }
    }

}
