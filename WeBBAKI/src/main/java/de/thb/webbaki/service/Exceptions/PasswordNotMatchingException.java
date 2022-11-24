package de.thb.webbaki.service.Exceptions;

import javax.naming.AuthenticationException;

public class PasswordNotMatchingException extends AuthenticationException {

    public PasswordNotMatchingException(){
        super("User password and entered password do not match.");
    }

    public PasswordNotMatchingException(String message){
        super(message);
    }
}
