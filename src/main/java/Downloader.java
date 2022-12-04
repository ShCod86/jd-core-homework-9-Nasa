import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.YoutubeProgressCallback;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.videos.VideoDetails;
import com.github.kiulian.downloader.model.videos.VideoInfo;

import java.io.File;


public class Downloader {

    public static void downloadVideo(String url, String path) {
        int index = url.indexOf("?");
        String videoId = url.substring(url.lastIndexOf("/") + 1, index);
        YoutubeDownloader downloader = new YoutubeDownloader();
        RequestVideoInfo request = new RequestVideoInfo(videoId);
        Response<VideoInfo> response = downloader.getVideoInfo(request);
        VideoInfo video = response.data();
        VideoDetails details = video.details();
        System.out.println(details.title());

        File outputDir = new File(path);
        RequestVideoFileDownload requestDownload = new RequestVideoFileDownload(video.bestVideoWithAudioFormat())
                .saveTo(outputDir)
                .renameTo(details.title())
                .overwriteIfExists(true)
                .callback(new YoutubeProgressCallback<File>() {
                    @Override
                    public void onDownloading(int i) {
                        System.out.printf("\b\b\b\b\b%d%%", i);
                    }

                    @Override
                    public void onFinished(File file) {
                        System.out.println("\nЗагружен файл: " + file);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println("Error: " + throwable.getLocalizedMessage());
                    }
                }).async();
        Response<File> responseDownload = downloader.downloadVideoFile(requestDownload);
        responseDownload.data();
    }

}
