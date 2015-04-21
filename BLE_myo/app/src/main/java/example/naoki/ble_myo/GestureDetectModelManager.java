package example.naoki.ble_myo;

/**
 * Created by naoki on 15/04/16.
 */
public class GestureDetectModelManager {
    private static final Object LOCK = new Object();
    private static IGestureDetectModel currentModel = new NopModel();

    public static IGestureDetectModel getCurrentModel(){
        synchronized (LOCK) {
            return currentModel;
        }
    }

    public static void setCurrentModel(IGestureDetectModel model){
        synchronized (LOCK) {
            currentModel = model;
        }
    }

}
