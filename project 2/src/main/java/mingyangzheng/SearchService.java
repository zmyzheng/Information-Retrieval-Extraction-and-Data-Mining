package mingyangzheng;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;

import java.io.IOException;
import java.util.*;

/**
 * use google cumstom search API to search by query; will return 10 results each time
 */
public class SearchService {
    private String API_KEY = "AIzaSyAHzQAbQFJmGyElhnh_VVFay_ECunRqVoE";
    private String ENGINE_KEY = "009650898989487274447:ghd3zgarfa4";
//    private Customsearch customsearch = new Customsearch(new NetHttpTransport(), new JacksonFactory(), null);
    private Customsearch customsearch;

    public SearchService(String API_KEY, String ENGINE_KEY) {
        this.API_KEY = API_KEY;
        this.ENGINE_KEY = ENGINE_KEY;
        customsearch = new Customsearch.Builder(new NetHttpTransport(), new JacksonFactory(), null).setApplicationName("cs6111project2").build();


    }


    public List<Result> searchByKeywords(String query) {
        List<Result> items = null;
        try {
            Customsearch.Cse.List list = customsearch.cse().list(query);
            list.setKey(API_KEY).setCx(ENGINE_KEY);
            Search results = list.execute();
            items = results.getItems();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<String> searchLinksByKeywords(String query) {
        List<Result> items = searchByKeywords(query);
        List<String> links = new ArrayList<String>();
        for (Result item : items) {
            links.add(item.getLink());
        }
        return links;
    }





}
