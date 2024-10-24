package de.thb.webbaki.mail.Templates.AdminNotifications;

import de.thb.webbaki.configuration.HostnameReader;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AdminRegisterNotification {
    private final HostnameReader hostnameReader;

    public String buildAdminEmail(String name, String link, String userFirstname, String userLastname,
                                         String userEmail, String userBranche, String userCompany) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"de\" dir=\"ltr\">\n" +
                "  <head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title></title>\n" +
                "  </head>\n" +
                "\n" +
                "  <style>\n" +
                "    p{\n" +
                "      font-size:16px;\n" +
                "    }\n" +
                "\n" +
                "    html {\n" +
                "      font-family: sans-serif;\n" +
                "      text-align:center;\n" +
                "      align-content:center;\n" +
                "    }\n" +
                "\n" +
                "    table {\n" +
                "      width:560px;\n" +
                "      border-collapse: collapse;\n" +
                "      border: 2px solid rgb(200,200,200);\n" +
                "      letter-spacing: 1px;\n" +
                "      font-size: 0.9rem;\n" +
                "    }\n" +
                "\n" +
                "    td, th {\n" +
                "      border: 1px solid rgb(190,190,190);\n" +
                "      padding: 10px 20px;\n" +
                "    }\n" +
                "\n" +
                "    th {\n" +
                "      background-color: rgb(235,235,235);\n" +
                "    }\n" +
                "\n" +
                "    td {\n" +
                "      text-align: center;\n" +
                "    }\n" +
                "\n" +
                "    tr:nth-child(even) td {\n" +
                "      background-color: rgb(250,250,250);\n" +
                "    }\n" +
                "\n" +
                "    tr:nth-child(odd) td {\n" +
                "      background-color: rgb(245,245,245);\n" +
                "    }\n" +
                "\n" +
                "    caption {\n" +
                "      padding: 10px;\n" +
                "    }\n" +
                "  </style>\n" +
                "\n" +
                "  <body>\n" +
                "    <h2 style=\"background-color:black; color: white; padding: 20px 0; margin: 0 auto;\">Neue Registrierung auf WebBaKI</h2>\n" +
                "    <p>Hallo " + name + ",</p>\n" +
                "    <p>Es hat sich ein neuer WebBaKI-Nutzer registriert. Infos zum Nutzer:</p>\n" +
                "    <div class=\"tabledata\" style=\"display:flex;align-items:center; justify-content:center\">\n" +
                "      <table style=\"\">\n" +
                "          <tr>\n" +
                "            <td>Vorname</td>\n" +
                "            <td>" + userFirstname + "</td>\n" +
                "          </tr>\n" +
                "          <tr>\n" +
                "            <td>Nachname</td>\n" +
                "            <td>" + userLastname + "</td>\n" +
                "          </tr>\n" +
                "          <tr>\n" +
                "            <td>Firma</td>\n" +
                "            <td>" + userCompany + "</td>\n" +
                "          </tr>\n" +
                "          <tr>\n" +
                "            <td>Email</td>\n" +
                "            <td>" + userEmail + "</td>\n" +
                "          </tr>\n" +
                "          <tr>\n" +
                "            <td>Branche</td>\n" +
                "            <td>" + userBranche + "</td>\n" +
                "          </tr>\n" +
                "      </table>\n" +
                "    </div>\n" +
                "    <p>Der Account kann unter folgendem Link aktiviert oder abgelehnt werden:</p>\n" +
                "      <p>\n" +
                "        <a href=" + link + ">Nutzer verifizieren</a>\n" +
                "        <span></span>\n" +
                "        <a href=\""+hostnameReader.getHostnameWithoutEnding()+"/confirmation/userDenied\">Nutzer ablehnen</a>\n" +
                "      </p>\n" +
                "    <p>Der Link bleibt 3 Tage gültig.</p>\n" +
                "  </body>\n" +
                "</html>\n";
    }
}
