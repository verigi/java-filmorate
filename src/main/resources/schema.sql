DROP ALL OBJECTS;

CREATE TABLE IF NOT EXISTS mpa (
    mpa_id INT AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT mpa_pk PRIMARY KEY (mpa_id)
);

CREATE TABLE IF NOT EXISTS genre (
    genre_id INT AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT genre_pk PRIMARY KEY (genre_id)
);


CREATE TABLE IF NOT EXISTS films (
    film_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(200) NOT NULL,
    release_date DATE NOT NULL,
    duration INT NOT NULL,
    mpa_id INT NOT NULL,
    CONSTRAINT films_mpa_id_fk FOREIGN KEY (mpa_id) REFERENCES mpa(mpa_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(50) NOT NULL,
    login VARCHAR(50) NOT NULL,
    name VARCHAR(50),
    birthday DATE NOT NULL
);



CREATE TABLE IF NOT EXISTS friends (
    user_id INT NOT NULL,
    friend_id INT NOT NULL,
    CONSTRAINT friends_user_id_fk FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT friends_friend_id_fk FOREIGN KEY (friend_id) REFERENCES users(user_id) ON DELETE CASCADE
);



CREATE TABLE IF NOT EXISTS film_genre (
    film_id INT NOT NULL,
    genre_id INT NOT NULL,
    CONSTRAINT film_genre_film_id_fk FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    CONSTRAINT film_genre_genre_id_fk FOREIGN KEY (genre_id) REFERENCES genre(genre_id)
);

CREATE TABLE IF NOT EXISTS film_likes (
    film_id INT NOT NULL,
    user_id INT NOT NULL,
    CONSTRAINT film_likes_film_id_fk FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    CONSTRAINT film_likes_user_id_fk FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);