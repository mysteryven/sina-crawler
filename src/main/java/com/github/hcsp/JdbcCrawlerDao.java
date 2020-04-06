package com.github.hcsp;

import java.sql.*;
import java.util.HashMap;

public class JdbcCrawlerDao implements CrawlerDao {
    private Connection connection;

    public  JdbcCrawlerDao() {
        String jdbcUrl = "jdbc:h2:file://localhost:3306/news";
        try {
            connection =  DriverManager.getConnection(jdbcUrl, "root", "root");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean hasNextLinkToBeProcessed() {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM link_to_be_processe;");
            ResultSet resultSet = statement.executeQuery();
            int count = resultSet.getInt(1);
            return count != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public String getNextLinkAndThenDelete() {
        return null;
    }

    @Override
    public boolean hasBeenProcessed(String link) {

        return false;
    }

    @Override
    public void storeLinkToLinkPool(String link) {

    }

    @Override
    public void storeNews(HashMap news) {

    }
}
