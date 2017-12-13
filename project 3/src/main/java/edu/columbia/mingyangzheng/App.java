package edu.columbia.mingyangzheng;

import edu.columbia.mingyangzheng.models.AssociationRule;
import edu.columbia.mingyangzheng.models.FrequentItemset;
import edu.columbia.mingyangzheng.util.DatasetGenerator;

import java.io.*;
import java.util.*;

/**
 * Entry of the program
 *
 */
public class App {
    public static void main( String[] args ) {

//        DatasetGenerator datasetGenerator =
//                new DatasetGenerator(400, "original-dataset.csv", "INTEGRATED-DATASET.csv");
//        datasetGenerator.generateIntegratedDataset();


        if (args.length != 0 && args.length != 3) {
            System.out.println("wrong number of parameters");
            return;
        }

        String dataset = args.length == 0 ? "INTEGRATED-DATASET.csv" : args[0];
        double minSupport = args.length == 0 ? 0.01 : Double.parseDouble(args[1]);
        double minConfidence = args.length == 0 ? 0.5 : Double.parseDouble(args[2]);


        DataMiner dataMiner = new DataMiner(dataset, minSupport, minConfidence);
        List<AssociationRule> associationRules = dataMiner.findAssociationRules();
        Collections.sort(associationRules, Collections.reverseOrder());//listed in decreasing order of their confidence
        List<FrequentItemset> frequentItemsets = dataMiner.getFrequentItemsets();
        Collections.sort(frequentItemsets, Collections.reverseOrder());  //listed in decreasing order of their support.


        // print result to console
        System.out.println("==Frequent itemsets (min_sup=" + minSupport + ")");
        for (FrequentItemset frequentItemset : frequentItemsets) {
            System.out.println(frequentItemset.getItems() + ", " + frequentItemset.getSupport());
        }
        System.out.println("");

        System.out.println("==High-confidence association rules (min_conf=" + minConfidence + ")");
        for (AssociationRule associationRule : associationRules) {
            System.out.println(associationRule.getLHS() + " => " + associationRule.getRHS() + " (Conf: " + associationRule.getConfidence() + ", Supp: " + associationRule.getSupport() + ")");
        }

        //  generate output.txt
        BufferedWriter bufw = null;
        try {
            bufw = new BufferedWriter(new FileWriter("output.txt"));
            bufw.write("==Frequent itemsets (min_sup=" + minSupport + ")");
            bufw.newLine();
            bufw.flush();
            for (FrequentItemset frequentItemset : frequentItemsets) {
                bufw.write(frequentItemset.getItems() + ", " + frequentItemset.getSupport());
                bufw.newLine();
                bufw.flush();
            }
            bufw.newLine();
            bufw.flush();
            bufw.write("==High-confidence association rules (min_conf=" + minConfidence + ")");
            bufw.newLine();
            bufw.flush();
            for (AssociationRule associationRule : associationRules) {
                bufw.write(associationRule.getLHS() + " => " + associationRule.getRHS() + " (Conf: " + associationRule.getConfidence() + ", Supp: " + associationRule.getSupport() + ")");
                bufw.newLine();
                bufw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufw != null)
                try {
                    bufw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

    }


}
