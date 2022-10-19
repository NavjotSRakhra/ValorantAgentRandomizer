package navjot.valorant.valorantagentradomizer.update;

import android.content.Context;
import android.net.ConnectivityManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Executor;

import javax.net.ssl.HttpsURLConnection;

import navjot.valorant.valorantagentradomizer.OnUpdatable;

public class Updatable {
    private String latestBuildVersion, latestReleaseResponse;
    private final String buildVersionName, latestReleaseURL;
    private final Context context;

    public Updatable(Context context, String currentBuildVersionName, String userName, String repoName) {
        this.context = context;
        this.buildVersionName = currentBuildVersionName;
        this.latestReleaseURL = "https://api.github.com/repos/" + userName + "/" + repoName + "/releases/latest";
    }

    public void checkUpdates(Executor executor, OnUpdatable actionIfUpdatable) {
        executor.execute(() -> {
            latestBuildVersion = getLatestBuildVersion();
            System.out.println("Checking for updates");
            System.out.println(latestBuildVersion);
            if (isUpdatable()) {
                String updateLink = getUpdateLink();
                System.out.println("update link");
                System.out.println(updateLink);
                actionIfUpdatable.onComplete(updateLink);
            }else {
                Update.deleteFile(context);
            }
        });
    }

    private String getUpdateLink() {
        String temp = latestReleaseResponse.substring(latestReleaseResponse.indexOf("browser_download_url") + ("browser_download_url\": ".length()));
        temp = temp.substring(0, temp.indexOf("\""));
        return temp;
    }

    private boolean isUpdatable() {
        return !latestBuildVersion.equals(buildVersionName);
    }

    private String getLatestBuildVersion() {
        try {

            if (((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo().isAvailable()) {
                int c;

                URL url = new URL(latestReleaseURL);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                InputStream in = httpsURLConnection.getInputStream();

                StringBuilder stringBuilder = new StringBuilder();

                while (-1 != (c = in.read())) {
                    stringBuilder.appendCodePoint(c);
                }
                in.close();
                httpsURLConnection.disconnect();

                latestReleaseResponse = stringBuilder.toString();
                String tag_name = latestReleaseResponse.substring(latestReleaseResponse.indexOf("tag_name") + ("tag_name\": ".length()));
                return tag_name.substring(1, tag_name.indexOf('\"'));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return buildVersionName;
    }
}
