package com.github.hcsp;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class Crawler {
    public static void main(String[] args) throws IOException {
        HashMap<String, String> news = new HashMap<>();

        Queue<String> linkToBeProcessed = new LinkedList<>();

        HashSet<String> linkHasBeenProcessed = new HashSet<>();
        linkToBeProcessed.add("https://sina.cn/");

        CloseableHttpClient httpclient = HttpClients.createDefault();

        while(!linkToBeProcessed.isEmpty()) {
            String link = linkToBeProcessed.poll();

            if (isNewPage(link, linkHasBeenProcessed)) {
                Document document = getCurrentLinkDocument(httpclient, link);

                storeProcessedLink(linkHasBeenProcessed, link);
                storeNewsToDataBase(document, news);
                storeALinkToLinkPool(linkToBeProcessed, document);
            }
        }
    }

    private static void storeProcessedLink(HashSet<String> linkHasBeenProcessed, String link) {
        linkHasBeenProcessed.add(link);
    }

    private static Document getCurrentLinkDocument(CloseableHttpClient httpclient, String link) throws IOException {
        HttpGet httpGet = new HttpGet(link);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        String responseString = EntityUtils.toString(response.getEntity());
        return Jsoup.parse(responseString);
    }

    private static void storeALinkToLinkPool(Queue<String> linkToBeProcessed, Document document) {
        Elements elements = document.select("a");
        for (Element element: elements) {
            String href = element.attr("href");
            if (href.contains("news.sina.cn")) {
                linkToBeProcessed.add(href);
            }
        }
    }

    private static boolean isNewPage(String link, HashSet<String> linkHasBeenProcessed) {
        return !linkHasBeenProcessed.contains(link);
    }

    private static void storeNewsToDataBase(Document doc, HashMap<String, String> news) {
        Elements articles = doc.select("article");
        if (!articles.isEmpty()) {
            String title = articles.get(0).child(0).text();
            String text = articles.get(0).select("p.art_p").text();
            System.out.println(title);
            news.put(title, text);
        }
    }
}
