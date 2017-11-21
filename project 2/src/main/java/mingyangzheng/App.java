package mingyangzheng;


import mingyangzheng.model.RelationEntity;
import mingyangzheng.util.WebPageParser;

import java.util.*;

/**
 * the entrance of the program, and the basic logic of the program
 */
public class App {
    public static void main( String[] args ) {

        String relationType;
        String API_KEY = (args.length == 0 ? "AIzaSyAHzQAbQFJmGyElhnh_VVFay_ECunRqVoE" : args[0]);
        String ENGINE_KEY = args.length == 0 ? "009650898989487274447:ghd3zgarfa4" : args[1];
        int relationNum = args.length == 0 ? 4 : Integer.parseInt(args[2]);
        double threshold = args.length == 0 ? 0.35 : Double.parseDouble(args[3]);
        String seedQuery = args.length == 0 ? "bill gates microsoft" : args[4];
        int k = args.length == 0 ? 10 : Integer.parseInt(args[5]);

        switch(relationNum) {
            case 1:
                relationType = "Live_In";
                break;
            case 2:
                relationType = "Located_In";
                break;
            case 3:
                relationType = "OrgBased_In";
                break;
            case 4:
                relationType = "Work_For";
                break;
            default:
                System.out.println("wrong relationNumber");
                return;
        }

        System.out.println("Parameters:");
        System.out.println("Client key      = " + API_KEY);
        System.out.println("Engine key      = " + ENGINE_KEY);
        System.out.println("Relation        = " + relationType);
        System.out.println("Threshold       = " + threshold);
        System.out.println("Query           = " + seedQuery);
        System.out.println("# of Tuples     = " + k);
        System.out.println("Loading necessary libraries; this will take a few seconds...");


        // Initialize the relavant services, including searchService, annotationService.
        SearchService searchService = new SearchService(API_KEY, ENGINE_KEY);
        AnnotationService annotationService = new AnnotationService(relationType);

        // Initialize List<RelationEntity> result, the List of unduplicated extracted tuples, as the empty List.
        List<RelationEntity> result = new ArrayList<RelationEntity>();

        // For each URL from the previous step that has not been processed before (skip already-seen URLs, use Set<String> visitedLinks to stored visited links)
        Set<String> visitedLinks = new HashSet<String>();
        int iterationCount = 0;
        String query = null;
        while (result.size() < k) {
            if (result.size() == 0 && query == null) {
                query = seedQuery;
            }
            else {
                //  select from result a tuple y such that (1) y has not been used for querying yet and (2) y has an extraction confidence that is highest among the tuples in result that have not yet been used for querying. Create a query q from tuple y by just concatenating the attribute values together
                int i;
                for (i = 0; i < result.size(); i++) {
                    RelationEntity relationEntity = result.get(i);
                    if (!relationEntity.isVisited()) {
                        //  Whether the tuple has been used is recorded by RelationEntity.isVisited(). After using this tuple, we set relationEntity.setVisited(true)
                        relationEntity.setVisited(true);
                        query = relationEntity.getEntityValue1() + " " + relationEntity.getEntityValue2();
                        break;
                    }
                }
                if (i == result.size()) {
                    // If no such y tuple exists, then stop. (ISE has "stalled" before retrieving k high-confidence tuples.)
                    System.out.println("Program could not produce a new query based on given tuples. Shutting down...");
                    return;
                }
            }
            iterationCount++;
            System.out.println("       ");
            System.out.println("=========== Iteration: " + iterationCount + " - Query: " + query + " ===========");
            // Query Google Custom Search Engine to obtain the URLs for the top-10 webpages for seed query
            List<String> links = searchService.searchLinksByKeywords(query);
            for (String link : links) {
                //
                if (!visitedLinks.contains(link)) {
                    System.out.println("Processing: " + link);
                    //  Extract the actual plain text from the webpage using Jsoup and other Regular expression.
                    String text = WebPageParser.parseByURL(link);

                    // Annotate the text with the Stanford CoreNLP software suite and, in particular, with the Stanford Relation Extractor
                    Set<RelationEntity> relationEntities = annotationService.TwoRoundAnnotate(text);
                    result.addAll(relationEntities);
                    System.out.println("Relations extracted from this website: " + relationEntities.size() + " (Overall: " + result.size() + ")");

                }

            }
            System.out.println("Pruning relations below threshold...");
            Collections.sort(result, new Comparator<RelationEntity>() {
                public int compare(RelationEntity o1, RelationEntity o2) {
                    return (int) Math.signum(o2.getConfidence() - o1.getConfidence());
                }
            });
            List<RelationEntity> tempList = new ArrayList<RelationEntity>();
            for (int i = 0; i < result.size(); i++) {
                RelationEntity relationEntity = result.get(i);
                //  Identify the tuples for r that have an associated extraction confidence of at least threshold and add them to List result.
                // Remove exact duplicates from List result: if result contains tuples that are identical to each other, keep only the copy that has the highest extraction confidence and remove from result the duplicate copies.
                if (relationEntity.getConfidence() > threshold && !tempList.contains(relationEntity) && tempList.size() < 10) {
                    tempList.add(relationEntity);
                }
            }
            result = tempList;
            System.out.println("Number of tuples after pruning: " + result.size());
            System.out.println("================== ALL RELATIONS =================");
            for (int i = 0; i < result.size(); i++) {
                RelationEntity relationEntity = result.get(i);
                System.out.println("Relation Type: " + relationEntity.getRelationType() + " | "
                        + "Confidence: " + relationEntity.getConfidence() + " | "
                        + "Entity #1: " + relationEntity.getEntityValue1() + " (" + relationEntity.getEntityType1() +")" + "\t\t\t"
                        + "Entity #2: " + relationEntity.getEntityValue2() + " (" + relationEntity.getEntityType2() +")");
            }
            System.out.println();
        }
        // If result contains at least k tuples, return the top-k such tuples sorted in decreasing order by extraction confidence, together with the extraction confidence of each tuple, and stop.
        System.out.println("Program reached 10 number of tuples. Shutting down...");



    }


}
