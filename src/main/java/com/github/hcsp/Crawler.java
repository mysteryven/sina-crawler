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
        MyBatisCrawlerDao dao = new MyBatisCrawlerDao();

        CloseableHttpClient httpclient = HttpClients.createDefault();

        while(dao.hasNextLinkToBeProcessed()) {
            String link = dao.getNextLinkAndThenDelete();

            if (!dao.hasBeenProcessed(link)) {
                dao.storeLinkToProcessed(link);

                Document document = getCurrentLinkDocument(httpclient, link);
                storeALinkToLinkPool(document, dao);
                storeNewsToDataBase(document, dao);
            }
        }
    }


    private static Document getCurrentLinkDocument(CloseableHttpClient httpclient, String link) throws IOException {
        HttpGet httpGet = new HttpGet(link);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        String responseString = EntityUtils.toString(response.getEntity());
        return Jsoup.parse(responseString);
    }

    private static void storeALinkToLinkPool(Document document, CrawlerDao dao) {
        Elements elements = document.select("a");
        for (Element element: elements) {
            String href = element.attr("href");
            if (href.contains("news.sina.cn")) {
                dao.storeLinkToLinkPool(href);
            }
        }
    }

    private static void storeNewsToDataBase(Document doc, CrawlerDao dao) {
        Elements articles = doc.select("article");
        if (!articles.isEmpty()) {
            HashMap<String, String> news = new HashMap<>();
            String title = articles.get(0).child(0).text();
            String text = articles.get(0).select("p.art_p").text();
            System.out.println(title);
            System.out.println(text);
            dao.storeNews(new News(title, text));
        }
    }
}
