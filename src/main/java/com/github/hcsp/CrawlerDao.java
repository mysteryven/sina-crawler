package com.github.hcsp;

import java.util.HashMap;

public interface CrawlerDao {
    boolean hasNextLinkToBeProcessed();

    String getNextLinkAndThenDelete();

    boolean hasBeenProcessed(String link);

    void storeLinkToLinkPool(String link);

    void storeNews(HashMap news);
}
