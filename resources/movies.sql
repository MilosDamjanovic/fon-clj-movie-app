-- :name create-author-table
-- :command :execute
-- :result :raw
-- :doc creates author table

CREATE TABLE if not exists author (
  author_id SERIAL primary key,
  name varchar(255) NOT NULL,
  surname varchar(255) NOT NULL,
  date_of_birth date NOT NULL,
  country varchar(255) NOT NULL
);

-- :name create-user-table
-- :command :execute
-- :result :raw
-- :doc creates user table
CREATE TABLE if not exists users (
  id SERIAL primary key,
  username varchar(50) not NULL,
  email varchar(30) not NULL,
  password varchar(255) not NULL
);

-- :name create-movie-review-table
-- :command :execute
-- :result :raw
-- :doc creates movie review table 
CREATE TABLE if not exists moviereview (
 movie_id int not NULL,
 author_id int not null,
 rating int,
 review varchar(255) DEFAULT null,
 date_of_review date DEFAULT null,
 primary key (author_id, movie_id),
   CONSTRAINT authorfk FOREIGN KEY (author_id) REFERENCES author (author_id),
   CONSTRAINT moviefk FOREIGN KEY (movie_id) REFERENCES movie (movie_id)
);

-- :name create-genre-table
-- :command :execute
-- :result :raw
-- :doc creates genre table
CREATE TABLE if not exists genre (
  genre_id SERIAL primary key,
  name varchar(255) DEFAULT NULL
);

-- :name create-movie-table
-- :command :execute
-- :result :raw
-- :doc creates movie table
CREATE TABLE if not exists movie (
    movie_id SERIAL primary KEY,
    movie_title TEXT,
    genre_id int,
    author_id int,
    year_of_issue int,
  FOREIGN KEY (author_id) REFERENCES author (author_id),
  FOREIGN KEY (genre_id) REFERENCES genre (genre_id)
);

-- :name get-movies :? :* 
SELECT movie_id, movie_title, g.genre_id, g.name, a.author_id, a.name as author_name, a.surname as author_surname, year_of_issue 
FROM movie as m
inner join genre as g on g.genre_id = m.genre_id
inner join author as a on a.author_id = m.author_id;

-- :name get-movie-reviews :? :* 
SELECT mr.movie_id, mr.author_id, a.name as author_name,
 a.surname as author_surname, m.movie_title as movie_name, m.year_of_issue as year_of_issue,
 rating, review, date_of_review 
FROM moviereview as mr
inner join author as a on a.author_id = mr.author_id
inner join movie as m on m.movie_id = mr.movie_id
ORDER BY movie_name;

-- :name get-movie-review-by-author-and-movie-id :? :* 
SELECT mr.movie_id, mr.author_id, a.name as author_name,
 a.surname as author_surname, m.movie_title as movie_name, m.year_of_issue as year_of_issue,
 rating, review, date_of_review 
FROM moviereview as mr
inner join author as a on a.author_id = mr.author_id
inner join movie as m on m.movie_id = mr.movie_id
WHERE mr.author_id = :author-id and mr.movie_id = :movie-id;

-- :name get-users :? :*
-- :doc retrieves all the created users
SELECT * FROM users;

-- :name get-author-by-id :? :* 
SELECT * FROM author
WHERE author_id = :author-id;

-- :name get-authors :? :* 
SELECT * FROM author;

-- :name get-genres :? :* 
SELECT * FROM genre;

-- :name get-movie-by-id :? :*
SELECT * FROM movie 
WHERE movie_id = :movie-id;

-- :name get-user-by-payload :? :1
SELECT * FROM users 
WHERE username = :username;

-- :name get-user-by-credentials :? :*
SELECT * FROM users
WHERE username = :username and password = :password;

-- :name get-movies-by-author-id :? :*
SELECT a.name, a.surname, m.movie_title FROM movie as m
inner join author as a on a.author_id = m.author_id
WHERE m.author_id = :author-id;

-- :name insert-movie :insert :*
INSERT INTO movie (movie_title, genre_id, author_id, year_of_issue)
VALUES(:movie-title, :genre-id, :author-id, :year-of-issue)
RETURNING *;

-- :name insert-movie-review :insert :*
INSERT INTO moviereview (movie_id, author_id, rating, review, date_of_review)
VALUES(:movie-id, :author-id, :rating, :review, :date-of-review)
RETURNING *;

-- :name insert-author :insert :*
INSERT INTO author (name, surname, date_of_birth, country)
VALUES(:name, :surname, :date-of-birth, :country)
RETURNING *;

-- :name insert-user :insert :*
INSERT INTO users (username, email, password)
VALUES(:username, :email, :password)
RETURNING *;

-- :name insert-genre :insert :*
INSERT INTO genre (genre_id, name)
VALUES(:genre-id, :name)
RETURNING genre_id;

-- :name delete-movie-by-id :! :1
DELETE FROM movie WHERE movie_id = :movie-id;

-- :name delete-author-by-id :! :1
DELETE FROM author WHERE author_id = :author-id;

-- :name delete-movie-review-by-author-and-movie-id :! :1
DELETE FROM moviereview WHERE author_id = :author-id and movie_id = :movie-id;

-- :name update-author-by-id :! :1
UPDATE author
SET name = :name, surname =:surname, date_of_birth=:date-of-birth, country=:country
WHERE author_id =:author-id;

-- :name update-movie-by-id :! :1
UPDATE movie
SET movie_title = :movie-title, genre_id = :genre-id, author_id = :author-id, year_of_issue=:year-of-issue
WHERE movie_id = :movie-id;

-- :name update-movie-review-by-author-and-movie-id :! :1
UPDATE moviereview
SET review = :review, author_id = :author-id, movie_id = :movie-id, rating=:rating, date_of_review=:date-of-review
WHERE movie_id = :movie-id and author_id = :author-id;



insert into users (id, name, email, password) values (1, 'user1', 'test@email.com', 'eyJhbGciOiJIUzI1NiJ9.eyJpZCI6NCwidXNlcm5hbWUiOiJ0ZXN0MyIsImVtYWlsIjoidGVzdDNAZW1haWwuY29tIn0.TzhOUFaRgP-mclPGNTLe2amcLkq0GcPrhK8wS8htrcc');
insert into author (name, surname, country, date_of_birth) values ('Christopher' , 'Nolan', 'USA' ,'2019-02-08T12:12:12Z');
insert into genre (id, name) values (1, 'Action');
insert into movie (movie_title, genre_id, author_id, year_of_issue) values ('Batman - Dark knight rises', 1, 1, 2008);
insert into moviereview (review, author_id, movie_id, date_of_review, rating) values ('excellent movie', 1, 1, '2020-02-08T12:12:12Z', 5);