package example.naoki.ble_myo;

import java.util.ArrayList;

/**
 * Created by naoki on 15/04/17.
 */
public class NumberSmoother {
    private final static int SMOOTHING_LENGTH = 5;
    private final static int THRESHOLD_LENGTH = 3;

    private ArrayList<Integer> gestureNumArray = new ArrayList<>();
    private int[] numCounter = {0,0,0};
    private int storageDataCount = 0;

    public void addArray(Integer gestureNum) {
        gestureNumArray.add(gestureNum);
        if (gestureNum != -1) {
            numCounter[gestureNum]++;
        }
        storageDataCount++;
        if (storageDataCount > SMOOTHING_LENGTH) {
            int deleteNumber = gestureNumArray.get(0);
            if (deleteNumber != -1) {
                numCounter[deleteNumber]--;
            }
            gestureNumArray.remove(0);
            storageDataCount--;
        }
    }

    public int getSmoothingNumber() {
        for (int i_element = 0; i_element < 3; i_element++) {
            if (numCounter[i_element] >= THRESHOLD_LENGTH) {
                return i_element;
            }
        }
        return -1;
    }
}
