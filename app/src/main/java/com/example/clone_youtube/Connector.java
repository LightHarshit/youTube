package com.example.clone_youtube;

import android.content.Context;
import android.util.Log;

import com.google.api.services.youtube.YouTube;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Connector {

    private YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {

        @Override
        public void initialize(HttpRequest request) throws IOException {

            request.getHeaders().set("X-Android-Package", PACKAGENAME);
           // request.getHeaders().set("X-Android-Cert",SHA1);
        }
    }).setApplicationName("SearchYoutube").build();

     static final String KEY = "AIzaSyC1zmOJzKIa2vwIYnIoHNXtW9ijN58hnAY";
    private YouTube.Search.List query;

    public static final String PACKAGENAME = "com.example.clone_youtube";
    //public static final String SHA1 = "SHA1_FINGERPRINT_STRING";

    private static final long MAXRESULTS = 60;

    public Connector(Context context) {

        try {

            query = youtube.search().list("id,snippet");
            query.setKey(KEY);
            query.setType("video");
            query.setFields("items(id/kind,id/videoId,snippet/title,snippet/description,snippet/thumbnails/high/url)");

        } catch (IOException e) {
            Log.d("YC", "Could not initialize: " + e);
        }
    }

    public List<Video> search(String keywords) {

        query.setQ(keywords);

        query.setMaxResults(MAXRESULTS);

        try {

            SearchListResponse response = query.execute();
            List<SearchResult> results = response.getItems();
            List<Video> items = new ArrayList<Video>();
            if (results != null) {
                items = setItemsList(results.iterator());
            }

            return items;

        } catch (IOException e) {

            Log.d("YC", "Could not search: " + e);
            return null;
        }
    }

    private static List<Video> setItemsList(Iterator<SearchResult> iteratorSearchResults) {

        List<Video> tempSetItems = new ArrayList<>();

        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }


        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();

            ResourceId r = singleVideo.getId();
            if (r.getKind().equals("youtube#video")) {

                Video item = new Video();

                Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getHigh();

                //retrieving title,description,thumbnail url, id from the heirarchy of each resource
                //Video ID - id/videoId
                //Title - snippet/title
                //Description - snippet/description
                //Thumbnail - snippet/thumbnails/high/url
                item.setId(singleVideo.getId().getVideoId());
                item.setTitle(singleVideo.getSnippet().getTitle());
                item.setDescription(singleVideo.getSnippet().getDescription());
                item.setThumbnailURL(thumbnail.getUrl());

                tempSetItems.add(item);

                System.out.println(" Video Id" + r.getVideoId());
                System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
                System.out.println(" Thumbnail: " + thumbnail.getUrl());
                System.out.println(" Description: "+ singleVideo.getSnippet().getDescription());
                System.out.println("\n-------------------------------------------------------------\n");
            }
        }
        return tempSetItems;
    }
}