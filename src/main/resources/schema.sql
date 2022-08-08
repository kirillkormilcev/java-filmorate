create table if not exists MPA_RATINGS (
    MPA_RATING_ID INTEGER auto_increment,
    RATING_NAME   CHARACTER VARYING(5),
    DESCRIPTION   CHARACTER VARYING(200),
    constraint MPA_RATING_ID
        primary key (MPA_RATING_ID)
);

create table if not exists GENRES (
    GENRE_ID    INTEGER auto_increment,
    GENRE_NAME  CHARACTER VARYING(50),
    DESCRIPTION CHARACTER VARYING(200),
    constraint GENRE_ID
        primary key (GENRE_ID)
);

create table if not exists FILMS (
    FILM_ID        BIGINT generated by default as identity (maxvalue 2147483647),
    FILM_NAME      CHARACTER VARYING(100) not null,
    DESCRIPTION    CHARACTER VARYING(200),
    DURATION       INTEGER,
    LIKES_RATING   INTEGER DEFAULT 0,
    MPA_RATING_ID  INTEGER DEFAULT 0,
    RELEASE_DATE   DATE,
    constraint FILM_ID
        primary key (FILM_ID),
    constraint MPA_RATING_ID_FK
        foreign key (MPA_RATING_ID) references MPA_RATINGS
);

create unique index if not exists FILMS_NAME_UNQ
    on FILMS (FILM_NAME);

create table if not exists FILM_GENRES (
    FILM_GENRES_ID INTEGER auto_increment,
    FILM_ID        BIGINT,
    GENRE_ID       INTEGER,
    constraint FILM_GENRE_ID
        primary key (FILM_GENRES_ID),
    constraint FILM_ID_FK1
        foreign key (FILM_ID) references FILMS,
    constraint GENRE_ID_FK
        foreign key (GENRE_ID) references GENRES
);

create table if not exists USERS (
    USER_ID       BIGINT generated by default as identity (maxvalue 2147483647),
    USER_NAME     CHARACTER VARYING(100),
    LOGIN         CHARACTER VARYING(50)  not null,
    EMAIL         CHARACTER VARYING(200) not null,
    BIRTHDAY      DATE,
    FRIENDS_COUNT INTEGER DEFAULT 0,
    constraint USER_ID
        primary key (USER_ID)
);

create unique index if not exists USERS_EMAIL_UNQ
    on USERS (EMAIL);

create unique index if not exists USERS_LOGIN_UNQ
    on USERS (LOGIN);

create table if not exists LIKES (
    LIKE_ID BIGINT auto_increment,
    FILM_ID INTEGER,
    USER_ID BIGINT,
    constraint LIKE_ID
        primary key (LIKE_ID),
    constraint FILM_ID_FK
        foreign key (FILM_ID) references FILMS,
    constraint USER_ID_FK
        foreign key (USER_ID) references USERS
);

create table if not exists FRIENDSHIPS (
    FRIENDSHIP_ID INTEGER auto_increment,
    USER1_ID      BIGINT,
    FRIEND2_ID    BIGINT,
    CONFIRM       BOOLEAN,
    constraint FRIENDSHIP_ID
        primary key (FRIENDSHIP_ID),
    constraint FRIEND2_ID_FK
        foreign key (FRIEND2_ID) references USERS,
    constraint USER1_ID_FK
        foreign key (USER1_ID) references USERS
);