package de.thb.webbaki.service.Exceptions;

import javax.naming.AuthenticationException;

public class EmailNotMatchingException extends AuthenticationException {

    public EmailNotMatchingException(){
        super("Die eingegebene Email stimmt nicht mit Ihrer aktuellen Email-Adresse überein");
    }

    public EmailNotMatchingException(String message){
        super(message);
    }
}
