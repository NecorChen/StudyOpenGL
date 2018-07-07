package cn.flyaudio.studyopengl;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by necorchen on 18-6-28.
 */

public class TextResourceReader {

    public static String readTextFileFromResorce(Context context,
                                                 int resourceId) {
        StringBuilder body = new StringBuilder();
        try {
            InputStream inputStream = context.getResources().
                    openRawResource(resourceId);
            InputStreamReader inputStreamReader =
                    new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String nextLine;

            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append("\n");
            }
        } catch (Exception e) {
            Log.d("necor-airball", "e===" + e.toString());
        }

        return body.toString();
    }
}
