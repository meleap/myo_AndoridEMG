package example.naoki.ble_myo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by naoki on 15/04/09.
 */
public class MyoDataFileReader {
    private static File BASE_DIR = new File("sdcard");
    public static void init(File base){
        BASE_DIR = base;
    }

    private String dirname = "";
    private String filename = "";

    public MyoDataFileReader(String dirname,String filename) {
        this.dirname = dirname;
        this.filename = filename;
    }

    public void saveRAW(ArrayList<EmgCharacteristicData> myoDataList) {
        File targetFile = getMyoDataFile();
        targetFile.getParentFile().mkdirs();

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(targetFile);
            for(EmgCharacteristicData myoData : myoDataList){
                writer.println(myoData.getLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(writer != null){
                writer.close();
            }
        }
    }

    public void saveMAX(ArrayList<EmgData> myoDataList) {
        File targetFile = getMyoDataFile();
        targetFile.getParentFile().mkdirs();

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(targetFile);
            for(EmgData myoData : myoDataList){
                writer.println(myoData.getLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(writer != null){
                writer.close();
            }
        }
    }

    public ArrayList<EmgData> load(){
        File directory = getDirectory();
        ArrayList<EmgData> dataList = new ArrayList<EmgData>();
        if(!directory.isDirectory()){
            return dataList;
        }

        File targetFile = getMyoDataFile();
        FileReader fr = null;
        try{
            fr = new FileReader(targetFile);
            BufferedReader br = new BufferedReader(fr);

            String line = null;
            while((line = br.readLine()) != null){
                line = line.trim();
                EmgData data = new EmgData();
                data.setLine(line);

                dataList.add(data);
            }
            br.close();
        }
        catch(Exception e){
            if(fr != null){
                try {
                    fr.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        return dataList;
    }


    public File getMyoDataFile(){
        return new File(getDirectory(), filename);
    }

    private File getDirectory(){
        return new File(BASE_DIR, dirname);
    }
}
