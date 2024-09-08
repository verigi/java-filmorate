package ru.yandex.practicum.filmorate.exception;

public class IncorrectUserDetails extends RuntimeException{
    public IncorrectUserDetails(final String message){
        super(message);
    }
}
