package example.naoki.ble_myo;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by naoki on 15/04/09.
 *
 */
 
public class EmgData {
    private ArrayList<Double> emgData = new ArrayList<>();

    public EmgData() {
    }

    public EmgData(EmgCharacteristicData characteristicData) {
        this.emgData = new ArrayList<>( characteristicData.getEmg8Data_abs().getEmgArray() );
    }

    public EmgData(ArrayList<Double> emgData) {
        this.emgData = emgData;
    }

    public String getLine() {
        StringBuilder return_SB = new StringBuilder();
        for (int i_emg_num = 0; i_emg_num < 8; i_emg_num++) {
            return_SB.append(String.format("%f,", emgData.get(i_emg_num)));
        }
        return return_SB.toString();
    }

    public void setLine(String line) {
        ArrayList<Double> data = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(line , ",");
        for (int i_emg_num = 0; i_emg_num < 8; i_emg_num++) {
            data.add(Double.parseDouble(st.nextToken()));
        }
        emgData = data;
    }

    public void addElement(double element) {
        emgData.add(element);
    }

    public void setElement(int index ,double element) {
        emgData.set(index,element);
    }

    public Double getElement(int index) {
        if (index < 0 || index > emgData.size() - 1) {
            return null;
        } else {
            return emgData.get(index);
        }
    }

    public ArrayList<Double> getEmgArray() {
        return this.emgData;
    }

    public Double getDistanceFrom(EmgData baseData) {
        Double distance = 0.00;
        for (int i_element = 0; i_element < 8; i_element++) {
            distance += Math.pow((emgData.get(i_element) - baseData.getElement(i_element)),2.0);
        }
        return Math.sqrt(distance);
    }

    public Double getInnerProductionTo(EmgData baseData) {
        Double val = 0.00;
        for (int i_element = 0; i_element < 8; i_element++) {
            val += emgData.get(i_element) * baseData.getElement(i_element);
        }
        return val;
    }

    public Double getNorm(){
        Double norm = 0.00;
        for (int i_element = 0; i_element < 8; i_element++) {
            norm += Math.pow( emgData.get(i_element) ,2.0);
        }
        return Math.sqrt(norm);
    }
}
