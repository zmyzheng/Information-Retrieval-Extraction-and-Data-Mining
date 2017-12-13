package edu.columbia.mingyangzheng.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * a mapper between itemSet of string and BitSet
 * the advantage of using Bitset is (1) in order (2) constant time complexity for set's contains method
 */
public class BitSetMapper {


    private Map<String, Integer> itemToIndex = new HashMap<String, Integer>();
    private Map<Integer, String> indexToItem = new HashMap<Integer, String>();
    private List<BitSet> marketBaskets = new ArrayList<BitSet>();
    private String dataset;



    private int categories;

    public BitSetMapper(String dataset) {
        this.dataset = dataset;
        categories = mapItemWithIndex();
        generateMarketBaskets(categories);
    }



    // map from string to a Integer
    public int mapItemWithIndex() {
        BufferedReader bufr = null;
        try {
            bufr = new BufferedReader(new FileReader(dataset));
            String line = bufr.readLine();
            while ((line = bufr.readLine()) != null) {
                String[] items = line.split(",+");
                for (String item : items) {
                    if (!itemToIndex.containsKey(item)) {
                        int index = itemToIndex.size();
                        itemToIndex.put(item, index);
                        indexToItem.put(index, item);
                    }
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
        }
        return itemToIndex.size();
    }

    // map each line of the csv file to a BitSet
    public int generateMarketBaskets(int categories) {
        BufferedReader bufr = null;
        try {
            bufr = new BufferedReader(new FileReader(dataset));
            String line = bufr.readLine();
            while ((line = bufr.readLine()) != null) {
                BitSet marketBasket = new BitSet(categories);
                String[] items = line.split(",+");
                for (String item : items) {
                    marketBasket.set(itemToIndex.get(item));
                }
                marketBaskets.add(marketBasket);
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
        }

        return marketBaskets.size();

    }

    // parse BitSet to itemSet
    public Set<String> getItemsByBitSet(BitSet itemSet) {
        Set<String> items = new HashSet<String>();
        for (int i = itemSet.nextSetBit(0); i >= 0; i = itemSet.nextSetBit(i + 1)) {
            items.add(indexToItem.get(i));
        }
        return items;
    }



    public int getCategories() {
        return categories;
    }
    public List<BitSet> getMarketBaskets() {
        return marketBaskets;
    }

}
