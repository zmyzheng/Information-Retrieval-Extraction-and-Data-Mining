package edu.columbia.mingyangzheng.models;

import java.util.Set;

public class AssociationRule implements Comparable<AssociationRule> {
    private Set<String> LHS;
    private Set<String> RHS;
    private double confidence;
    private double support;

    public AssociationRule(Set<String> LHS, Set<String> RHS, double confidence, double support) {
        this.LHS = LHS;
        this.RHS = RHS;
        this.confidence = confidence;
        this.support = support;
    }

    public Set<String> getLHS() {
        return LHS;
    }

    public void setLHS(Set<String> LHS) {
        this.LHS = LHS;
    }

    public Set<String> getRHS() {
        return RHS;
    }

    public void setRHS(Set<String> RHS) {
        this.RHS = RHS;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public double getSupport() {
        return support;
    }

    public void setSupport(double support) {
        this.support = support;
    }

    @Override
    public int compareTo(AssociationRule o) {
        if (this.getConfidence() != o.getConfidence())
            return (int) Math.signum(this.getConfidence() - o.getConfidence());
        else
            return (int) Math.signum(this.getSupport() - o.getSupport());

    }
}
