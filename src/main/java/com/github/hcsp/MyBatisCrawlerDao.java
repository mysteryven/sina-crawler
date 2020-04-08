package com.github.hcsp;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MyBatisCrawlerDao implements CrawlerDao {
    private SqlSessionFactory sqlSessionFactory;
    private String mybatisPrefix;

    public MyBatisCrawlerDao() {
        try {
            String resource = "db/mybatis/config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            mybatisPrefix = "com.github.hcsp.MyMapper.";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasNextLinkToBeProcessed() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            int count = session.selectOne(mybatisPrefix + "selectToBeProcessedCount");
            return count != 0;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getNextLinkAndThenDelete() {
        try(SqlSession session = sqlSessionFactory.openSession(true)) {
            String link = session.selectOne(mybatisPrefix + "selectNextLink");
            session.delete(mybatisPrefix + "deleteLink", link);
            return link;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    public boolean hasBeenProcessed(String link) {
        try(SqlSession session = sqlSessionFactory.openSession(true)) {
            int count = session.selectOne(mybatisPrefix + "selectHasBeenProcessedCount");
            return count != 0;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void storeLinkToLinkPool(String link) {
        Map<String, Object> param = new HashMap<>();
        param.put("tableName", "link_to_be_processed");
        param.put("link", link);
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            session.insert(mybatisPrefix + "insertLink", param);
        }
    }

    @Override
    public void storeNews(News news) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            session.insert(mybatisPrefix + "insertNews", news);
        }
    }

    @Override
    public void storeLinkToProcessed(String link) {
        Map<String, Object> param = new HashMap<>();
        param.put("tableName", "link_has_been_processed");
        param.put("link", link);
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            session.insert(mybatisPrefix + "insertLink", param);
        }
    }
}
