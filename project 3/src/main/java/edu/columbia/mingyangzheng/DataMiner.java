package edu.columbia.mingyangzheng;

import edu.columbia.mingyangzheng.models.AssociationRule;
import edu.columbia.mingyangzheng.models.FrequentItemset;
import edu.columbia.mingyangzheng.util.BitSetMapper;

import java.util.*;

/**
 *  This class is used to find association rules with two steps:
 *  step 1: find Frequent Itemsets with Apriori above min support
 *  setp 2: generate association rules that satisfy the min confidence
 */
public class DataMiner {
    private double minSupport;
    private double minConfidence;
    BitSetMapper bitSetMapper;
    private List<BitSet> marketBaskets;
    private int categories = 0; // the number of different items, the same as the length of BitSet
    Map<Integer, Map<BitSet, Integer>> largeItemSetMap;


    public DataMiner(String dataset, double minSupport, double minConfidence) {
        this.minSupport = minSupport;
        this.minConfidence = minConfidence;
        bitSetMapper = new BitSetMapper(dataset);
        marketBaskets = bitSetMapper.getMarketBaskets();
        categories = bitSetMapper.getCategories();
    }

    public List<AssociationRule> findAssociationRules() {
//        step 1: find Frequent Itemsets with Apriori above min support
        Apriori apriori = new Apriori(minSupport, marketBaskets, categories);
        largeItemSetMap = apriori.generateAllLargeItemSets();

//        setp 2: generate association rules that satisfy the min confidence
        List<AssociationRule> associationRules = new ArrayList<AssociationRule>();
        for (int k = 2; k <= largeItemSetMap.size(); k++) {
            Map<BitSet, Integer> largeKItemSets = largeItemSetMap.get(k); // the itemSet in round k, with k items in each set
            for (BitSet itemSet : largeKItemSets.keySet()) {
                for (int i = itemSet.nextSetBit(0); i >= 0; i = itemSet.nextSetBit(i + 1)) {
                    BitSet itemSetClone1 = (BitSet) itemSet.clone();
                    BitSet itemSetClone2 = new BitSet(categories);
                    itemSetClone1.clear(i);  //  LHS
                    itemSetClone2.set(i); //  RHS
                    double confidence = largeItemSetMap.get(k).get(itemSet) / 1.0 / largeItemSetMap.get(k - 1).get(itemSetClone1);
                    if (confidence >= minConfidence) {
//                        map from bitset to set of string
                        Set<String> LHS = bitSetMapper.getItemsByBitSet(itemSetClone1);
                        Set<String> RHS = bitSetMapper.getItemsByBitSet(itemSetClone2);
                        associationRules.add(new AssociationRule(LHS, RHS, confidence, largeItemSetMap.get(k).get(itemSet) / 1.0 / marketBaskets.size()));
                    }
                }
            }
        }
        return associationRules;
    }

    // getFrequentItemsets: map from Bitset to Set of Strings
    public List<FrequentItemset> getFrequentItemsets() {
        List<FrequentItemset> frequentItemsets = new ArrayList<FrequentItemset>();
        for (int k = 1; k <= largeItemSetMap.size(); k++) {
            Map<BitSet, Integer> largeKItemSets = largeItemSetMap.get(k);
            for (BitSet itemSet : largeKItemSets.keySet()) {
                Set<String> items = bitSetMapper.getItemsByBitSet(itemSet);
                frequentItemsets.add(new FrequentItemset(items, largeKItemSets.get(itemSet) / 1.0 / marketBaskets.size()));
            }
        }
        return frequentItemsets;
    }


}
