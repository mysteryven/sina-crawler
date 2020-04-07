package com.github.hcsp;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

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
        return false;
    }

    @Override
    public void storeLinkToLinkPool(String link) {

    }

    @Override
    public void storeNews(HashMap news) {

    }
}
