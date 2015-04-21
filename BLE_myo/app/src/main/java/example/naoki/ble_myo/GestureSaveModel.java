package example.naoki.ble_myo;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by naoki on 15/04/16.
 */
public class GestureSaveModel implements IGestureDetectModel{
    private final static Object LOCK = new Object();

    private String name = "";
    private IGestureDetectAction action;

    private GestureSaveMethod saveMethod;

    public GestureSaveModel(GestureSaveMethod method) {
        saveMethod = method;
    }

    @Override
    public void event(long time, byte[] data) {
        synchronized (LOCK) {
            saveMethod.addData(data);

            if (saveMethod.getSaveState() == GestureSaveMethod.SaveState.Not_Saved) {
                action("SAVE");
//                action(String.valueOf(gesture.getGestureCounter()));
            } else if (saveMethod.getSaveState() == GestureSaveMethod.SaveState.Have_Saved) {
                action("SAVED");
            }
        }
    }

    @Override
    public void setAction(IGestureDetectAction action) {
        this.action = action;
    }

    @Override
    public void action() {
        action.action("SAVE");
    }

    public void action(String message) {
        action.action(message);
    }

}
