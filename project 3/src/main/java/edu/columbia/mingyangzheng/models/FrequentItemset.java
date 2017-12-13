package edu.columbia.mingyangzheng.models;

import java.util.Set;

public class FrequentItemset implements Comparable<FrequentItemset>{
    private Set<String> items;
    private double support;

    public FrequentItemset(Set<String> items, double support) {
        this.items = items;
        this.support = support;
    }

    public Set<String> getItems() {
        return items;
    }

    public void setItems(Set<String> items) {
        this.items = items;
    }

    public double getSupport() {
        return support;
    }

    public void setSupport(double support) {
        this.support = support;
    }

    @Override
    public int compareTo(FrequentItemset o) {
        return (int) Math.signum(this.getSupport() - o.getSupport());
    }
}
