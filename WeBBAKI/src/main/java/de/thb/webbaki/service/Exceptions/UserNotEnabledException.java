package de.thb.webbaki.service.Exceptions;

public class UserNotEnabledException extends Exception{
    public UserNotEnabledException(String message){
        super(message);
    }
    public UserNotEnabledException(){
        super();
    }

}
