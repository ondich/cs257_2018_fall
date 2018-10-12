Setting up the books/authors database
CS257 Software Design
Fall 2018
Jeff Ondich

How to set up my books/authors data so you can run my
sample API and website.

1. Create the tables in your PostgreSQL database

    CREATE TABLE authors (
        id integer NOT NULL,
        last_name text,
        first_name text,
        birth_year integer,
        death_year integer
    );

    CREATE TABLE books (
        id integer NOT NULL,
        title text,
        publication_year integer
    );

    CREATE TABLE books_authors (
        book_id integer,
        author_id integer
    );

2. Turn my raw data (books-original.csv) into one CSV file per table.

    python3 books_and_authors_converter.py

This generates books.csv, authors.csv, and books_authors.csv.
    

3. Load the data

    psql
      \copy authors from 'authors.csv' DELIMITER ',' CSV NULL AS 'NULL'
      \copy books from 'books.csv' DELIMITER ',' CSV NULL AS 'NULL'
      \copy books_authors from 'books_authors.csv' DELIMITER ',' CSV NULL AS 'NULL'

