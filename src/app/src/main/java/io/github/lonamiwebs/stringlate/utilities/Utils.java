package io.github.lonamiwebs.stringlate.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.github.lonamiwebs.stringlate.R;

public class Utils {

    //region Network

    public static boolean isNotConnected(final Context ctx, boolean warn) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        boolean notConnected = activeNetworkInfo == null || !activeNetworkInfo.isConnected();
        if (notConnected && warn) {
            Toast.makeText(ctx, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
        }
        return notConnected;
    }

    //endregion

    //region Reading and writing files

    @NonNull
    public static String readFile(final File file) {
        try {
            return readCloseStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }

    @NonNull
    static String readCloseStream(final InputStream stream) {
        String line;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
                sb.append(line).append('\n');

            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return "";
    }

    public static boolean writeFile(final File file, final String content) {
        BufferedWriter writer = null;
        try {
            if (!file.getParentFile().isDirectory() && !file.getParentFile().mkdirs())
                return false;

            writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
            writer.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //endregion

    //region Searching in files

    public static boolean fileContains(File file, String... needles) {
        try {
            FileInputStream in = new FileInputStream(file);

            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) {
                for (String n : needles)
                    if (line.contains(n))
                        return true;
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    //endregion

    //region Directories

    public static boolean deleteRecursive(File dir) {
        boolean ok = true;
        if (dir.exists()) {
            if (dir.isDirectory()) {
                for (File child : dir.listFiles())
                    ok &= deleteRecursive(child);
            }
            ok &= dir.delete();
        }
        return ok;
    }

    //endregion
}
