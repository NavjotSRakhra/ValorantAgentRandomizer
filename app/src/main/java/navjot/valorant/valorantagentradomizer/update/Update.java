package navjot.valorant.valorantagentradomizer.update;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import navjot.valorant.valorantagentradomizer.BuildConfig;

public class Update {
    private final String updatePath;
    private final Context context;
    private final File downloadFile;
    private final String downloadURL;
    private boolean isDownloaded = false;

    public Update(Context context, String downloadPath, String downloadURL) {
        this.context = context;

        int leftLimit = 'a';
        int rightLimit = 'z';
        int targetStringLength = 25;
        Random random = new Random();

        String appName = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        this.updatePath = downloadPath + "/" + appName + ".apk";
        downloadFile = new File(updatePath);
        this.downloadURL = downloadURL;
    }

    public void download() {
        try {
            downloadFile.createNewFile();

            FileOutputStream os = new FileOutputStream(downloadFile);
            URL downloadLink = new URL(downloadURL);
            HttpsURLConnection downloadConnection = (HttpsURLConnection) downloadLink.openConnection();
            downloadConnection.setRequestMethod("GET");

            downloadConnection.connect();

            InputStream downloadStream = downloadConnection.getInputStream();

            byte[] buff = new byte[32 << 10];

            int c;

            while ((c = downloadStream.read(buff)) != -1) {
                os.write(buff, 0, c);
            }
            os.flush();
            os.close();
            isDownloaded = true;
            SharedPreferences.Editor ed = context.getSharedPreferences("downloadedFileName", Context.MODE_PRIVATE).edit();
            ed.putString("fileName", updatePath);
            ed.apply();
            downloadStream.close();
            downloadConnection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void install() throws FileNotFoundException {

//        Intent install;

        String fileName = context.getSharedPreferences("downloadedFileName", Context.MODE_PRIVATE).getString("fileName", null);
        if (fileName != null) {
            installPackage(new File(fileName));
            return;
        }

        if (!isDownloaded) {
            throw new FileNotFoundException("File not Downloaded");
        }
        installPackage(downloadFile);

    }

    private void installPackage(File downloadFile) {
        Intent install;
        Uri apkURI = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", downloadFile);
        install = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        install.setData(apkURI);
        install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(install);
    }

    public static void deleteFile(Context context) {
        String fileName = context.getSharedPreferences("downloadedFileName", Context.MODE_PRIVATE).getString("fileName", null);
        if (fileName != null) {
            if (!(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 213);
            }
            new File(fileName).delete();
        }
        context.getSharedPreferences("downloadedFileName", Context.MODE_PRIVATE).edit().remove("fileName").apply();
    }
}
