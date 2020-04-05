package com.github.hcsp;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sun.jvm.hotspot.debugger.cdbg.CDebugger;

import java.io.IOException;
import java.util.*;

public class Crawler {
    public static void main(String[] args) throws IOException {
        Queue<String> linkToBeProcessed = new LinkedList();

        HashMap<String, String> linkHasBeenProcessed = new HashMap<>();
        linkToBeProcessed.add("https://sina.cn/");

        CloseableHttpClient httpclient = HttpClients.createDefault();

        while(!linkToBeProcessed.isEmpty()) {
            String link = linkToBeProcessed.poll();
            if (linkHasBeenProcessed.containsKey(link)) {
                continue;
            }

            linkHasBeenProcessed.put(link, link);

            HttpGet httpGet = new HttpGet(link);
            CloseableHttpResponse response = httpclient.execute(httpGet);
            String responseString = EntityUtils.toString(response.getEntity());
            Document document = Jsoup.parse(responseString);
            Elements elements = document.select("a");

            for (Element element: elements) {
                String href = element.attr("href");
                if (href.contains("news.sina.cn")) {
                    System.out.println(href);
                    linkToBeProcessed.add(href);
                }
            }
        }
    }
}
