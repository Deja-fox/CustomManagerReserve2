package com.jald.reserve.util;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.iss.base.util.PersistenceUtil;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Set;

public class DownloadUtil {

    private static final String Tag = "KM:DownloadUtil";
    private static DownloadUtil instance;

    public static DownloadUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (DownloadUtil.class) {
                instance = new DownloadUtil(context);
            }
        }
        return instance;
    }

    public static interface DownloadListener {
        public void onDownloadComplete(String url, String downloadedFileUri, FileDescriptor fd);

        public void onDownloadedFileNotExistErr(String url, String downloadedFileUri, String msg);
    }

    public static final String KEY_DOWNLOAD_LIST = "KeyDownloadList";

    private Context context;
    private PersistenceUtil persistenceUtil;
    private DownloadManager downloadManager;
    private HashMap<String, Long> downloadList = new HashMap<String, Long>();
    private DownloadCompleteReceiver downloadCompleteReceiver;
    private DownloadListener downloadListener;
    private String tipsOnStartDownload;

    @SuppressWarnings("unchecked")
    private DownloadUtil(Context context) {
        this.context = context.getApplicationContext();
        this.persistenceUtil = PersistenceUtil.getInstance(context);
        this.downloadList = (HashMap<String, Long>) this.persistenceUtil.getSavedObject(KEY_DOWNLOAD_LIST);
        if (this.downloadList == null) {
            this.downloadList = new HashMap<String, Long>();
        }
        this.downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (this.downloadCompleteReceiver == null) {
            this.downloadCompleteReceiver = new DownloadCompleteReceiver();
            this.context.registerReceiver(downloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    }

    public void start(String url) {
        String fileName = url.substring(url.lastIndexOf('/') + 1);
        start(url, "文件下载", fileName + "正在下载中");
    }

    public void start(String url, String nTitle) {
        String fileName = url.substring(url.lastIndexOf('/') + 1);
        download(url, nTitle, fileName + "正在下载中");
    }

    public void start(String url, String nTitle, String nDesc) {
        download(url, nTitle, nDesc);
    }

    private void download(String url, String nTitle, String nDesc) {
        if (!checkNeedDownload(url)) {
            Log.w(Tag, "要下载的文件已经正在下载或已下载完成,不再新开下载");
            return;
        }
        if (tipsOnStartDownload != null) {
            Toast.makeText(context, tipsOnStartDownload, Toast.LENGTH_LONG).show();
        }
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, getFileNameByUrl(url));
        request.setTitle(nTitle);
        request.setDescription(nDesc);
        long downloadId = downloadManager.enqueue(request);
        putDownloadItemToList(url, downloadId);
    }

    private boolean checkNeedDownload(String url) {
        Long lastDownloadId = this.downloadList.get(url);
        if (lastDownloadId == null) {
            return true;
        }
        Query query = new Query();
        query.setFilterById(lastDownloadId);
        Cursor cursor = this.downloadManager.query(query);
        int statusIdx = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        if (cursor.moveToFirst()) {
            int status = cursor.getInt(statusIdx);
            cursor.close();
            Log.e(Tag, "下载状态" + status);
            if (status == DownloadManager.STATUS_FAILED) {
                this.downloadManager.remove(lastDownloadId);
                removeDownloadItemFromList(url);
                return true;
            } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
                FileDescriptor fd = null;
                try {
                    fd = this.downloadManager.openDownloadedFile(lastDownloadId).getFileDescriptor();
                } catch (FileNotFoundException e) {
                    Log.e(Tag, "之前下载完成的文件已不存在,重新下载");
                    this.downloadManager.remove(lastDownloadId);
                    removeDownloadItemFromList(url);
                    return true;
                }
                if (this.downloadListener != null) {
                    Query nQuery = new Query();
                    query.setFilterById(lastDownloadId);
                    Cursor nCursor = downloadManager.query(nQuery);
                    if (nCursor.moveToFirst()) {
                        int fileUriIdx = nCursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                        String downloadedFileUri = nCursor.getString(fileUriIdx);
                        nCursor.close();
                        downloadListener.onDownloadComplete(url, downloadedFileUri, fd);
                    }
                    return false;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private void putDownloadItemToList(String url, long downloadId) {
        this.downloadList.put(url, downloadId);
        this.persistenceUtil.saveObject(KEY_DOWNLOAD_LIST, this.downloadList);
    }

    private void removeDownloadItemFromList(String url) {
        this.downloadList.remove(url);
        this.persistenceUtil.saveObject(KEY_DOWNLOAD_LIST, this.downloadList);
    }

    boolean isFileExistence(String url) {
        String targetPath = getLocalStroePathByUrl(url);
        return new File(targetPath).exists();
    }

    void deleteLocalFileByUrl(String url) {
        File file = new File(getLocalStroePathByUrl(url));
        file.delete();
    }

    String getFileNameByUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    String getLocalStroePathByUrl(String url) {
        File parent = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File target = new File(parent, getFileNameByUrl(url));
        return target.getAbsolutePath();
    }

    public DownloadListener getDownloadCompleteListener() {
        return downloadListener;
    }

    public void setDownloadCompleteListener(DownloadListener downloadCompleteListener) {
        this.downloadListener = downloadCompleteListener;
    }

    ;

    public void setTipsOnStartDownload(String tipsOnStartDownload) {
        this.tipsOnStartDownload = tipsOnStartDownload;
    }

    class DownloadCompleteReceiver extends BroadcastReceiver {

        public DownloadCompleteReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                long handle = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                doDwonloadComplete(handle);
            }
        }

        public void doDwonloadComplete(long downloadId) {
            Query query = new Query();
            query.setFilterById(downloadId);
            Cursor cursor = downloadManager.query(query);
            if (cursor.moveToFirst()) {
                int fileUriIdx = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                String downloadedFileUri = cursor.getString(fileUriIdx);
                cursor.close();
                String originUrl = getUrlByDownloadId(downloadId);
                Log.e(Tag, "下载完成:原始URL" + originUrl + "=>本地文件存放URI" + downloadedFileUri);
                if (downloadListener != null) {
                    FileDescriptor fileDesc = null;
                    String fileNotFoundErrMsg = "";
                    try {
                        fileDesc = downloadManager.openDownloadedFile(downloadId).getFileDescriptor();
                    } catch (FileNotFoundException e) {
                        fileNotFoundErrMsg = "下载的文件丢失,可能文件已被删除!";
                        Log.e(Tag, fileNotFoundErrMsg);
                        downloadListener.onDownloadedFileNotExistErr(originUrl, downloadedFileUri, fileNotFoundErrMsg);
                        removeBadDownloadEntry(originUrl, downloadId);
                        return;
                    }
                    downloadListener.onDownloadComplete(originUrl, downloadedFileUri, fileDesc);
                }
            }
        }

        private void removeBadDownloadEntry(String originUrl, long downloadId) {
            downloadManager.remove(downloadId);
            removeDownloadItemFromList(originUrl);
        }

        private String getUrlByDownloadId(long downloadId) {
            Set<String> urlSet = downloadList.keySet();
            for (String url : urlSet) {
                long id = downloadList.get(url);
                if (id == downloadId) {
                    return url;
                }
            }
            return null;
        }
    }

}
