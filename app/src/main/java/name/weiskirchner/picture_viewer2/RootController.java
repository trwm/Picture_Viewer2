package name.weiskirchner.picture_viewer2;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by michael on 09.07.17.
 */

public class RootController extends ExecuteAsRootBase {

    @Override
    protected ArrayList<String> getCommandsToExecute() {
        String command;
        ArrayList<String> commands = new ArrayList<>();
        File sdCardDirectory = Environment.getExternalStorageDirectory();
        Log.d("getCommandsToExecute", "External Storage: " + sdCardDirectory.getAbsolutePath());
        command = "cp /data/data/com.whatsapp/databases/msgstore.db* " + sdCardDirectory.getAbsolutePath() + "/PictureViewer/WADatabases/";
        commands.add(command);
        command = "cp /data/data/com.whatsapp/cache/Profile\\ Pictures/*.* " + sdCardDirectory.getAbsolutePath() + "/PictureViewer/WAProfilePictures/";
        commands.add(command);
        return commands;
    }
}
