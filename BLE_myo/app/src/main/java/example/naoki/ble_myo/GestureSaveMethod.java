package example.naoki.ble_myo;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by naoki on 15/04/17.
 */
public class GestureSaveMethod {
    private final static String TAG = "Myo_compare";
    private final static String FileName = "compareData.dat";

    private final static int COMPARE_NUM = 3;
    private final static int SAVE_DATA_LENGTH = 5;
    private final static int AVERAGING_LENGTH = 10;

    private ArrayList<EmgCharacteristicData> rawDataList = new ArrayList<>();
    private ArrayList<EmgData> maxDataList = new ArrayList<>();
    private ArrayList<EmgData> compareGesture = new ArrayList<>();

    private SaveState saveState = SaveState.Ready;

    private int dataCounter = 0;
    private int gestureCounter = 0;

    public GestureSaveMethod() {
        MyoDataFileReader dataFileReader = new MyoDataFileReader(TAG,FileName);
        if (dataFileReader.load().size() == 3) {
            compareGesture = dataFileReader.load();
            saveState = SaveState.Have_Saved;
        }
    }

    public enum SaveState {
        Ready,
        Not_Saved,
        Now_Saving,
        Have_Saved,
    }

    public void addData(byte[] data) {
        rawDataList.add(new EmgCharacteristicData(data));
        dataCounter++;
        if (dataCounter % SAVE_DATA_LENGTH == 0) {
            EmgData dataMax = new EmgData();
            int count = 0;
            for (EmgCharacteristicData emg16Temp : rawDataList) {
                EmgData emg8Temp = emg16Temp.getEmg8Data_abs();
                if (count == 0) {
                    dataMax = emg8Temp;
                } else {
                    for (int i_element = 0; i_element < 8; i_element++) {
                        if (emg8Temp.getElement(i_element) > dataMax.getElement(i_element)) {
                            dataMax.setElement(i_element, emg8Temp.getElement(i_element));
                        }
                    }
                }
                count++;
            }
            if (rawDataList.size() < SAVE_DATA_LENGTH) {
                Log.e("GestureDetect", "Small rawData : " + rawDataList.size());
            }
            maxDataList.add(dataMax);
            rawDataList = new ArrayList<>();
        }
        if (dataCounter == SAVE_DATA_LENGTH * AVERAGING_LENGTH) {
            saveState = SaveState.Not_Saved;
            makeCompareData();
            gestureCount();
            dataCounter = 0;
        }
    }

    private void gestureCount() {
        gestureCounter++;
        if (gestureCounter == COMPARE_NUM) {
            saveState = SaveState.Have_Saved;
            gestureCounter = 0;
            MyoDataFileReader dataFileReader = new MyoDataFileReader(TAG,FileName);
            dataFileReader.saveMAX(getCompareDataList());
        }
    }

    private void makeCompareData() {
        EmgData tempData  = new EmgData();

        // Get each Max EMG-elements of maxDataList
        int count = 0;
        for (EmgData emg8Temp : maxDataList) {
            if (count == 0) {
                tempData = emg8Temp;
            } else {
                for (int i_element = 0; i_element < 8; i_element++) {
                    if (emg8Temp.getElement(i_element) > tempData.getElement(i_element)) {
                        tempData.setElement(i_element, emg8Temp.getElement(i_element));
                    }
                }
            }
            count++;
        }

        // Averaging EMG-elements of maxDataList
/*        int count = 0;
        for (EmgData emg8Temp : maxDataList) {
            if (count == 0) {
                tempData = emg8Temp;
            } else {
                for (int i_element = 0; i_element < 8; i_element++) {
                    tempData.setElement(i_element, tempData.getElement(i_element) + emg8Temp.getElement(i_element));
                }
            }
            count++;
        }
        for (int i_element = 0; i_element < 8; i_element++) {
            tempData.setElement(i_element, tempData.getElement(i_element)/maxDataList.size());
        }
*/
        if (maxDataList.size() < AVERAGING_LENGTH) {
            Log.e("GestureDetect", "Small aveData : " + maxDataList.size());
        }
        compareGesture.add(tempData);
        maxDataList = new ArrayList<>();
    }

    public SaveState getSaveState() {
        return saveState;
    }

    public void setState(SaveState state) {
        saveState = state;
    }

    public int getGestureCounter() {
        return gestureCounter;
    }

    public ArrayList<EmgData> getCompareDataList() {
        return compareGesture;
    }

}
