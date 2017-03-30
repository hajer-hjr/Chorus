package chorus.chorus.com.chorus;

import android.content.Context;

/**
 * Created by hp on 11/01/2017.
 */

public class Config {


    private static  Context context;
    public Config(Context _context) {
        context = _context;
    }
    public static String FILE_UPLOAD_URL = "http://192.168.1.14/chorus" + "/?op=api&action=uploadMessage_file";

    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";
}
