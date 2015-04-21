package example.naoki.ble_myo;

import java.util.ArrayList;

/**
 * Created by naoki on 15/04/17.
 */
public class GestureDetectMethod {
    private final static int COMPARE_NUM = 3;
    private final static int STREAM_DATA_LENGTH = 5;
    private final static Double THRESHOLD = 0.01;

    private final ArrayList<EmgData> compareGesture;

    private int streamCount = 0;
    private EmgData streamingMaxData;
    private Double detect_distance;
    private int detect_Num;

    private NumberSmoother numberSmoother = new NumberSmoother();

    public GestureDetectMethod(ArrayList<EmgData> gesture) {
        compareGesture = gesture;
    }

    public enum GestureState {
        No_Gesture,
        Gesture_1,
        Gesture_2,
        Gesture_3
    }

    private GestureState getEnum(int i_gesture) {
        switch (i_gesture) {
            case 0:
                return GestureState.Gesture_1;
            case 1:
                return GestureState.Gesture_2;
            case 2:
                return GestureState.Gesture_3;
            default:
                return GestureState.No_Gesture;
        }
    }

    public GestureState getDetectGesture(byte[] data) {
        EmgData streamData = new EmgData(new EmgCharacteristicData(data));
        streamCount++;
        if (streamCount == 1){
            streamingMaxData = streamData;
        } else {
            for (int i_element = 0; i_element < 8; i_element++) {
                if (streamData.getElement(i_element) > streamingMaxData.getElement(i_element)) {
                    streamingMaxData.setElement(i_element, streamData.getElement(i_element));
                }
            }
            if (streamCount == STREAM_DATA_LENGTH){
                detect_distance = getThreshold();
                detect_Num = -1;
                for (int i_gesture = 0;i_gesture < COMPARE_NUM ;i_gesture++) {
                    EmgData compData = compareGesture.get(i_gesture);
                    double distance = distanceCalculation(streamingMaxData, compData);
                    if (detect_distance > distance) {
                        detect_distance = distance;
                        detect_Num = i_gesture;
                    }
                }
                numberSmoother.addArray(detect_Num);
                streamCount = 0;
            }
        }
        return getEnum(numberSmoother.getSmoothingNumber());
    }

    private double getThreshold() {
        return THRESHOLD;
//        return 0.9;
    }

	// 2 vectors distance devied from each vectors norm.
    private double distanceCalculation(EmgData streamData, EmgData compareData){
        double return_val = streamData.getDistanceFrom(compareData)/streamData.getNorm()/compareData.getNorm();
        return return_val;
    }

	// Mathematical [sin] value of 2 vectors' inner angle.
    private double distanceCalculation_sin(EmgData streamData, EmgData compareData){
        double return_val = streamData.getInnerProductionTo(compareData)/streamData.getNorm()/compareData.getNorm();
        return return_val;
    }

	// Mathematical [cos] value of 2 vectors' inner angle from low of cosines.
    private double distanceCalculation_cos(EmgData streamData, EmgData compareData){
        double streamNorm  = streamData.getNorm();
        double compareNorm = compareData.getNorm();
        double distance    = streamData.getDistanceFrom(compareData);
        return (Math.pow(streamNorm,2.0)+Math.pow(compareNorm,2.0)-Math.pow(distance,2.0))/streamNorm/compareNorm/2;
    }


}
