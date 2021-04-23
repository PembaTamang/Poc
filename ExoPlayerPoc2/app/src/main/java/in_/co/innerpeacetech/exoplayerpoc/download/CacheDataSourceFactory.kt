package in_.co.innerpeacetech.exoplayerpoc.download

import android.content.Context
import com.google.android.exoplayer2.upstream.DataSink
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.util.Util
import in_.co.innerpeacetech.exoplayerpoc.AudioCache

class MyCacheDataSourceFactory constructor(
    private val context: Context,
    private val listener: CacheDataSource.EventListener? = null,
    private val dataSink : DataSink
) :
    DataSource.Factory {
    private val defaultDatasourceFactory: DefaultDataSourceFactory = DefaultDataSourceFactory(
        context,
        Util.getUserAgent(context, "ExoPlayerCache")
    )

    override fun createDataSource(): DataSource {
        val dataSource =   CacheDataSource(
            AudioCache.getInstance(context)!!,
            defaultDatasourceFactory.createDataSource(),
            FileDataSource(),
            dataSink,
            CacheDataSource.FLAG_BLOCK_ON_CACHE or CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
            listener
        )

        return MyCacheDataSource(dataSource,context)
    }

}