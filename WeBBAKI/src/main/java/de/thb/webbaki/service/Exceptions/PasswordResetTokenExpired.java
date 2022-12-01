package de.thb.webbaki.service.Exceptions;

public class PasswordResetTokenExpired extends Exception {

    public PasswordResetTokenExpired(String msg){
        super(msg);
    }

    public PasswordResetTokenExpired(){super();}
}
