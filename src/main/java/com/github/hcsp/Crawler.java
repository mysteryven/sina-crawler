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

        Queue<String> linkToBeProcessed = new LinkedList();

        HashSet<String> linkHasBeenProcessed = new HashSet<>();
        linkToBeProcessed.add("https://sina.cn/");

        CloseableHttpClient httpclient = HttpClients.createDefault();

        while(!linkToBeProcessed.isEmpty()) {
            String link = linkToBeProcessed.poll();
            if (linkHasBeenProcessed.contains(link)) {
                continue;
            }

            linkHasBeenProcessed.add(link);

            HttpGet httpGet = new HttpGet(link);
            CloseableHttpResponse response = httpclient.execute(httpGet);
            String responseString = EntityUtils.toString(response.getEntity());
            Document document = Jsoup.parse(responseString);
            Elements elements = document.select("a");

            storeToDataBaseIfIsNewsPage(document);

            for (Element element: elements) {
                String href = element.attr("href");
                if (href.contains("news.sina.cn")) {
                    linkToBeProcessed.add(href);
                }
            }

        }
    }

    private static void storeToDataBaseIfIsNewsPage(Document doc, HashMap news) {
        Elements articles = doc.select("article");
        if (!articles.isEmpty()) {
            String title = articles.get(0).child(0).text();
            String text = articles.get(0).select("p.art_p").text();
            news.put(title, text);
        }
    }
}
