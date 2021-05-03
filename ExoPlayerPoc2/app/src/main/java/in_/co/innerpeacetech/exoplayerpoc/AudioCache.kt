package in_.co.innerpeacetech.exoplayerpoc

import android.content.Context
import com.google.android.exoplayer2.database.ExoDatabaseProvider

import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor

import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File


class AudioCache {
    companion object{
        private var sDownloadCache: SimpleCache? = null

        fun getInstance(context: Context): SimpleCache? {
            if (sDownloadCache == null) sDownloadCache = SimpleCache(
                File(context.cacheDir, "exoCache"),
                NoOpCacheEvictor(),
                ExoDatabaseProvider(context)
            )

            return sDownloadCache
        }

    }
}