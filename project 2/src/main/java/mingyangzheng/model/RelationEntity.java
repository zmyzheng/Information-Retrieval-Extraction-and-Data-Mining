package mingyangzheng.model;

/**
 * the model, containing fields entityValue1, entityValue2, relationType, entityType1, entityType2, confidence, visited;
 * Duplication removal:
 we override hashcode() and equals() method to define whether two relationEntities are duplicated.
 */
public class RelationEntity {
    private String entityValue1;
    private String entityValue2;
    private String relationType;
    private String entityType1;
    private String entityType2;
    private double confidence;
    private boolean visited;

    public RelationEntity(String entityValue1, String entityValue2, String relationType, String entityType1, String entityType2, double confidence) {
        this.entityValue1 = entityValue1;
        this.entityValue2 = entityValue2;
        this.relationType = relationType;
        this.entityType1 = entityType1;
        this.entityType2 = entityType2;
        this.confidence = confidence;
    }




    public String getEntityValue1() {
        return entityValue1;
    }

    public void setEntityValue1(String entityValue1) {
        this.entityValue1 = entityValue1;
    }

    public String getEntityValue2() {
        return entityValue2;
    }

    public void setEntityValue2(String entityValue2) {
        this.entityValue2 = entityValue2;
    }
    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public String getEntityType1() {
        return entityType1;
    }

    public void setEntityType1(String entityType1) {
        this.entityType1 = entityType1;
    }

    public String getEntityType2() {
        return entityType2;
    }

    public void setEntityType2(String entityType2) {
        this.entityType2 = entityType2;
    }
    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj instanceof RelationEntity) {
            RelationEntity relationEntity = (RelationEntity) obj;
            if (this.relationType.equals(relationEntity.getRelationType()) && (this.entityValue1.equals(relationEntity.getEntityValue1()) && this.entityValue2.equals(relationEntity.getEntityValue2())
                    || this.entityValue2.equals(relationEntity.getEntityValue1()) && this.entityValue1.equals(relationEntity.getEntityValue2()))) {
                return true;
            }
        }
        return false;

    }

    @Override
    public int hashCode() {
        int result = getEntityValue1().hashCode() + getEntityValue2().hashCode() + getRelationType().hashCode();
        return result;
    }
}
