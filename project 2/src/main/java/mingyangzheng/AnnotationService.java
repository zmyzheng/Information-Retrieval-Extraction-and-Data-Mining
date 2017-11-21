package mingyangzheng;


import edu.stanford.nlp.ie.machinereading.structure.EntityMention;
import edu.stanford.nlp.ie.machinereading.structure.MachineReadingAnnotations;
import edu.stanford.nlp.ie.machinereading.structure.RelationMention;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.RelationExtractorAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.stats.Counter;
import edu.stanford.nlp.util.CoreMap;
import mingyangzheng.model.RelationEntity;

import java.util.*;

/**
 * use  Stanford CoreNLP software suite to do two round annotation
 * Annotate the text with the Stanford CoreNLP software suite and, in particular, with the Stanford Relation Extractor, to extract all instances of the relation specified by input parameter relation number r. We only consider an extracted relation to be an instance of r if r has the highest extraction confidence among all relation types.
 * Relation annotation requires six annotators, namely, tokenize, ssplit, pos, lemma, ner, and parse. Unfortunately, the parse annotator is computationally expensive, so for efficiency you need to minimize its use. Specifically, you should not run parse over sentences that do not contain named entities of the right type for the relation of interest r. The required named entities for each relation type are as follows:
 Live_In: people and location
 Located_In: two locations
 OrgBased_In: organization and location
 Work_For: organization and people
 So to annotate the text, we should implement two pipelines. we should run the first pipeline, which consists of tokenize, ssplit, pos, lemma, and ner, for the full text that you extracted from a webpage. The output will identify the sentences in the webpage text together with the named entities, if any, that appear in each sentence.
 Then, we should run the second pipeline, which includes the expensive parse annotator, separately over each sentence that contains the required named entities for the relation of interest, as specified above. Note that the two named entities might appear in either order in a sentence and this is fine. The second pipeline consists of tokenize, ssplit, pos, lemma, ner, and parse.
 */
public class AnnotationService {
    private String relationType;
    private StanfordCoreNLP pipeline;
    private StanfordCoreNLP pipeline2;
    private RelationExtractorAnnotator relationExtractorAnnotator;

    private Map<String, Integer> map;

    private String[] entityTypes = new String[2];

    public AnnotationService(String relationType) {
        this.relationType = relationType;

        Properties props1 = new Properties();
        props1.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
        pipeline = new StanfordCoreNLP(props1);

        Properties props2 = new Properties();
        props2.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse");
        props2.setProperty("parse.model", "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
        props2.setProperty("ner.useSUTime", "0");
        pipeline2 = new StanfordCoreNLP(props2);
        relationExtractorAnnotator = new RelationExtractorAnnotator(props2);


        if (relationType.equals("Live_In")) {
            entityTypes[0] = "PEOPLE";
            entityTypes[1] = "LOCATION";
        } else if (relationType.equals("Located_In")) {
            entityTypes[0] = "LOCATION";
            entityTypes[1] = "LOCATION";
        } else if (relationType.equals("OrgBased_In")) {
            entityTypes[0] = "ORGANIZATION";
            entityTypes[1] = "LOCATION";
        } else if (relationType.equals("Work_For")) {
            entityTypes[0] = "ORGANIZATION";
            entityTypes[1] = "PEOPLE";
        }

        map = new HashMap<String, Integer>();
        mapReset();
    }

    //  To identify the sentences in the webpage text together with the named entities, we use a map to count the number of apperance of the named entities in the sentence.
    //  Initally, we set the required number of apperance of the named entities for the relation.
    private void mapReset() {
        if (relationType.equals("Live_In")) {
            map.put("PERSON", 1);
            map.put("LOCATION",1);
            map.put("ORGANIZATION", 0);
        } else if (relationType.equals("Located_In")) {
            map.put("PERSON", 0);
            map.put("LOCATION",2);
            map.put("ORGANIZATION", 0);
        } else if (relationType.equals("OrgBased_In")) {
            map.put("PERSON", 0);
            map.put("ORGANIZATION", 1);
            map.put("LOCATION",1);
        } else if (relationType.equals("Work_For")) {
            map.put("PERSON", 1);
            map.put("LOCATION",0);
            map.put("ORGANIZATION", 1);
        }
    }

    public Set<RelationEntity> TwoRoundAnnotate(String text) {

        Set<RelationEntity> relationEntities = new HashSet<RelationEntity>();

        Annotation annotation = new Annotation(text);
        pipeline.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

        StringBuilder sb = new StringBuilder();;

        for(CoreMap sentence: sentences) {

            mapReset();

            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String label = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                //System.out.println(label);

                //  Then every time the named entity appears in the sentence, we substract 1. If there are enough named entities appearing in the sentence, we retain the sentence for the second round of annotation.
                if (map.containsKey(label)) {
                    map.put(label, map.get(label) - 1);
                }
            }
            if (map.get("PERSON") <= 0 && map.get("LOCATION") <= 0 && map.get("ORGANIZATION") <= 0) {
                sb.append(sentence.toString());
                sb.append(" ");
            }
        }

        String filteredText = sb.toString();
//        System.out.println(filteredText);

        annotation = new Annotation(filteredText);
        pipeline2.annotate(annotation);
        relationExtractorAnnotator.annotate(annotation);

        sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

        for(CoreMap sentence: sentences) {
            List<RelationMention> relationMentions = sentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
            for (RelationMention relationMention: relationMentions) {
                if (relationMention.getType().equals(relationType)) {
                    double confidence = relationMention.getTypeProbabilities().getCount(relationType);
                    List<EntityMention> entities = relationMention.getEntityMentionArgs();
                    String entityType1 = entities.get(0).getType();
                    String entityValue1 = entities.get(0).getValue();
                    String entityType2 = entities.get(1).getType();
                    String entityValue2 = entities.get(1).getValue();
                    //System.out.println(entityType1 + " | " + entityValue1 + " | " + entityType2 + " | " +entityValue2 );
                    RelationEntity relationEntity = null;
                    if (entityType1.equals(entityTypes[0]) && entityType2.equals(entityTypes[1])) {
                        relationEntity = new RelationEntity(entityValue1, entityValue2, relationType, entityType1, entityType2, confidence);
                    } else if (entityType2.equals(entityTypes[0]) && entityType1.equals(entityTypes[1])) {
                        relationEntity = new RelationEntity(entityValue2, entityValue1, relationType, entityType2, entityType1, confidence);
                    }
                    //relationEntity = new RelationEntity(entityValue1, entityValue2, relationType, entityType1, entityType2, confidence);
                    if (relationEntity != null) {
                        if (relationEntities.add(relationEntity)) {
                            System.out.println("=============== EXTRACTED RELATION ===============");
                            System.out.println("Sentence: " + sentence);
                            System.out.println("RelationType: " + relationType + " | " + "Confidence= " + confidence + " | "
                                    + "EntityType1= " + entityType1 + " | " + "EntityValue1= " + entityValue1  + " | "
                                    + "EntityType2= " + entityType2 + " | " + "EntityValue2= " + entityValue2);
                            System.out.println("============== END OF RELATION DESC ==============");
                        }

                    }

                }


            }
        }
        return relationEntities;

    }

}
