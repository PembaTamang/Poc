package in_.co.innerpeacetech.exoplayerpoc.download

import android.content.Context
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheKeyFactory
import com.google.android.exoplayer2.util.Util
import in_.co.innerpeacetech.exoplayerpoc.AudioCache
import in_.co.innerpeacetech.exoplayerpoc.songurl

class MyCacheDataSourceFactory constructor(
    private val context: Context,
    private val listener: CacheDataSource.EventListener? = null,
    private val dataSink : DataSink
) :
    DataSource.Factory {
    private val defaultDatasourceFactory: DefaultDataSourceFactory = DefaultDataSourceFactory(
        context,
        Util.getUserAgent(context, "exo")
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
        val dataSpec = DataSpec.Builder().setKey("mykey").setUri("mkey")
        dataSource.cacheKeyFactory.buildCacheKey(dataSpec.build())
        return MyCacheDataSource(dataSource,context)
    }

}