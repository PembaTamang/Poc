package in_.co.innerpeacetech.exoplayerpoc.download;

import android.app.Notification;
import android.content.Context;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.scheduler.Requirements;
import com.google.android.exoplayer2.scheduler.Scheduler;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;

public class MyDownloadService extends DownloadService {
    private static final String DOWNLOAD_ACTION_FILE = "actions";
    private static final String DOWNLOAD_TRACKER_ACTION_FILE = "tracked_actions";
    private static final String DOWNLOAD_CONTENT_DIRECTORY = "downloads";

    private static File downloadDirectory;

    File downloadContentDirectory =
            new File(getDownloadDirectory(getApplicationContext()), DOWNLOAD_CONTENT_DIRECTORY);

    protected MyDownloadService(int foregroundNotificationId) {
        super(foregroundNotificationId);
    }

    protected MyDownloadService(int foregroundNotificationId, long foregroundNotificationUpdateInterval) {
        super(foregroundNotificationId, foregroundNotificationUpdateInterval);
    }

    private static synchronized File getDownloadDirectory(Context context) {
        if (downloadDirectory == null) {
            downloadDirectory = context.getExternalFilesDir(/* type= */ null);
            if (downloadDirectory == null) {
                downloadDirectory = context.getFilesDir();
            }
        }
        return downloadDirectory;
    }

    @Override
    protected DownloadManager getDownloadManager() {
        // Note: This should be a singleton in your app.
        ExoDatabaseProvider databaseProvider = new ExoDatabaseProvider(getApplicationContext());
        File downloadContentDirectory =
                new File(getDownloadDirectory(getApplicationContext()), DOWNLOAD_CONTENT_DIRECTORY);
// A download cache should not evict media, so should use a NoopCacheEvictor.
        SimpleCache downloadCache = new SimpleCache(
                downloadContentDirectory,
                new NoOpCacheEvictor(),
                databaseProvider);

// Create a factory for reading the data from the network.
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory();

// Choose an executor for downloading data. Using Runnable::run will cause each download task to
// download data on its own thread. Passing an executor that uses multiple threads will speed up
// download tasks that can be split into smaller parts for parallel execution. Applications that
// already have an executor for background downloads may wish to reuse their existing executor.
        Executor downloadExecutor = Runnable::run;

// Create the download manager.
        DownloadManager downloadManager = new DownloadManager(
                getApplicationContext(),
                databaseProvider,
                downloadCache,
                dataSourceFactory,
                downloadExecutor);

// Optionally, setters can be called to configure the download manager.
        downloadManager.setRequirements(new Requirements(Requirements.DEVICE_STORAGE_NOT_LOW));
        downloadManager.setMaxParallelDownloads(3);
        return downloadManager;
    }

    @Nullable
    @Override
    protected Scheduler getScheduler() {
        return null;
    }

    @Override
    protected Notification getForegroundNotification(List<Download> downloads) {
        return null;
    }
}
