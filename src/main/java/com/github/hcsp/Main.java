package com.github.hcsp;

public class Main {
    public static void main(String[] args) {
        MyBatisCrawlerDao dao = new MyBatisCrawlerDao();
        for (int i = 0; i < 10; ++i) {
            new Crawler(dao).start();
        }
    }
}
