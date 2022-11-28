package de.thb.webbaki.service.Exceptions;

public class NotAuthorizedException extends Exception{
    public NotAuthorizedException(String msg){
        super(msg);
    }
    public NotAuthorizedException(){
        super();
    }
}
