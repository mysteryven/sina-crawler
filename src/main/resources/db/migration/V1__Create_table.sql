CREATE TABLE news(
id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
title text,
url varchar(1000),
content text,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) DEFAULT CHARACTER=utf8mb4;


CREATE TABLE link_to_be_processed(
    link VARCHAR (1000)
);

CREATE TABLE link_has_been_processed(
    link VARCHAR (1000)
);

INSERT INTO link_to_be_processed(link)
VALUES ("https://sina.cn/");

