MERGE INTO GENRE (GENRE_ID, NAME)
    VALUES (1, 'Comedy'),
    (2, 'Drama'),
    (3, 'Cartoon'),
    (4, 'Thriller'),
    (5, 'Action'),
    (6, 'Documentary'),
    (7, 'Biography');

MERGE INTO MPA (MPA_ID, NAME)
    VALUES (1, 'G'),
    (2, 'PG'),
    (3, 'PG-13'),
    (4, 'R'),
    (5, 'NC-17');