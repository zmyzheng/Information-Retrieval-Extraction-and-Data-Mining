package edu.columbia.mingyangzheng.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * generate INTEGRATED-DATASET from original-dataset
 *  Resample the dataset randomly, the sample rate is 1: 400
    Change the column "Date" into the month the date belongs to.
 */
public class DatasetGenerator {

    private int sampleRate;
    private String inputFileName;
    private String outputFileName;
    private Random random;
    private Map<String, String> numToMonth;

    public DatasetGenerator(int sampleRate, String inputFileName, String outputFileName) {
        this.sampleRate = sampleRate;
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.random = new Random();
        this.numToMonth = new HashMap<String, String>();
        this.numToMonthInitialize();
    }

    private void numToMonthInitialize() {
        numToMonth.put("01", "January");
        numToMonth.put("02", "February");
        numToMonth.put("03", "March");
        numToMonth.put("04", "April");
        numToMonth.put("05", "May");
        numToMonth.put("06", "June");
        numToMonth.put("07", "July");
        numToMonth.put("08", "August");
        numToMonth.put("09", "September");
        numToMonth.put("10", "October");
        numToMonth.put("11", "November");
        numToMonth.put("12", "December");
    }

    public void generateIntegratedDataset() {
        BufferedReader bufr = null;
        BufferedWriter bufw = null;

        try {
            bufr = new BufferedReader(new FileReader(inputFileName));
            bufw = new BufferedWriter(new FileWriter(outputFileName));
            String line = null;
            //  copy csv header
            if ((line = bufr.readLine()) != null) {
                bufw.write(line);
                bufw.newLine();
                bufw.flush();
            }
            while ((line = bufr.readLine()) != null) {
                // randomly sample of the original dataset with sampleRate
                if (random.nextInt(sampleRate) == 0) {
                    line = parseLine(line);
                    bufw.write(line);
                    bufw.newLine();
                    bufw.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufr != null)
                try {
                    bufr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (bufw != null)
                try {
                    bufw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

    }

    // change date to the month they belong to
    public String parseLine(String line) {
        int splitIndex = line.indexOf(',');
        String date = line.substring(0, splitIndex);
        String month = date.split("/")[0];
        month = numToMonth.get(month);
        return month + line.substring(splitIndex);
    }



}
