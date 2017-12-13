package edu.columbia.mingyangzheng;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 Apriori realization:
 1. Apriori Candidate Generation
        (1) join step
        (2) prune step
 2.  retain the k-itemsets above min support
 */
public class Apriori {

    double minSupport;
    List<BitSet> marketBaskets;
    int categories = 0;

    Map<Integer, Map<BitSet, Integer>> largeItemSetMap = new HashMap<Integer, Map<BitSet, Integer>>();

    public Apriori(double minSupport, List<BitSet> marketBaskets, int categories) {
        this.minSupport = minSupport;
        this.marketBaskets = marketBaskets;
        this.categories = categories;
    }


    // init the {large 1-itemsets};
    public void generateLarge1ItemSets() {

        // step1: find all the 1-itemsets
        Map<BitSet, Integer> candidates = new HashMap<BitSet, Integer>();//
        for (BitSet marketBasket : marketBaskets) {
            for (int i = marketBasket.nextSetBit(0); i >= 0; i = marketBasket.nextSetBit(i + 1)) {
                BitSet itemSet = new BitSet(categories);
                itemSet.set(i);
                candidates.put(itemSet, candidates.getOrDefault(itemSet, 0) + 1);
            }
        }
        // step 2: only retain the 1-itemsets above min support
        candidateSetsToLargeKItemSets(candidates, 1);


    }

    // retain the k-itemsets above min support
    private void candidateSetsToLargeKItemSets(Map<BitSet, Integer> candidates, int k) {
        largeItemSetMap.put(k, new HashMap<BitSet, Integer>());
        Map<BitSet, Integer> largeKItemSets = largeItemSetMap.get(k);
        for (BitSet itemSet : candidates.keySet()) {
            int count = candidates.get(itemSet);
            if (count >= marketBaskets.size() * minSupport) {
                largeKItemSets.put(itemSet, count);
            }
        }
        //  System.out.println(largeItemSetMap.get(k).size());
    }


    public void generateLargeKItemSets(int k) {

        //1. Apriori Candidate Generation
            //(1) join step
        Map<BitSet, Integer> candidates = new HashMap<BitSet, Integer>();
        Map<BitSet, Integer> largeK_1ItemSets =  largeItemSetMap.get(k - 1);
        for (BitSet itemSet1 : largeK_1ItemSets.keySet()) {
            for (BitSet itemSet2 : largeK_1ItemSets.keySet()) {
                BitSet itemSet1Clone = (BitSet) itemSet1.clone();
                int lastTrueIndex1 = itemSet1Clone.previousSetBit(categories - 1);
                int lastTrueIndex2 = itemSet2.previousSetBit(categories - 1);
                itemSet1Clone.clear(lastTrueIndex1);
                itemSet1Clone.set(lastTrueIndex2);
                if (lastTrueIndex1 < lastTrueIndex2 && itemSet1Clone.equals(itemSet2)) { // only different in the last item
                    itemSet1Clone.set(lastTrueIndex1);
                    //(2) prune step
                    int i = -1;
                    for (i = itemSet1Clone.nextSetBit(0); i >= 0; i = itemSet1Clone.nextSetBit(i + 1)) {
                        BitSet itemSetNew = (BitSet) itemSet1Clone.clone();
                        itemSetNew.clear(i);  // itemSetNew : (k-l)-subsets
                        if (!largeItemSetMap.get(k - 1).containsKey(itemSetNew)) {
                            break;
                        }
                    }
                    if (i == -1) {
                        candidates.put(itemSet1Clone, 0);
                    }
                }
            }
        }

        // count the frequency of the itemset by checking whether each marketBasket contians this itemset
        // using itemSetClone.and(marketBasket).equals(itemSet)
        //  like HashMap, take O(1) for contains method, but better than HashMap: items in Bitset are in order
        for (BitSet itemSet : candidates.keySet()) {
            for (BitSet marketBasket : marketBaskets) {
                BitSet itemSetClone = (BitSet) itemSet.clone();
                itemSetClone.and(marketBasket);
                if (itemSetClone.equals(itemSet)) {
                    candidates.put(itemSet, candidates.get(itemSet) + 1);
                }
            }
        }
        //2.  retain the k-itemsets above min support
        candidateSetsToLargeKItemSets(candidates, k);

//        for (BitSet itemSet : largeItemSetMap.get(k).keySet()) {
//            System.out.println(itemSet + " ... " + largeItemSetMap.get(k).get(itemSet));
//        }

    }

    // Apriori alogrithm
    public Map<Integer, Map<BitSet, Integer>> generateAllLargeItemSets() {
        generateLarge1ItemSets();
        for (int i = 2; largeItemSetMap.get(i - 1).size() > 0; i++) {
            generateLargeKItemSets(i);
        }
        largeItemSetMap.remove(largeItemSetMap.size());
//        for (Integer key : largeItemSetMap.keySet()) {
//            System.out.println(largeItemSetMap.get(key).size());
//        }
        return largeItemSetMap;
    }



}
