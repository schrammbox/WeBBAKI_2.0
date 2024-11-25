# WebBaKI (GER: "Webbasierte Bedrohungsanalyse Kritischer Infrastrukturen") 
 in cooperation with UP KRITIS (https://www.bsi.bund.de/DE/Themen/KRITIS-und-regulierte-Unternehmen/Kritische-Infrastrukturen/UP-KRITIS/up-kritis_node.html). 

This is the Code of the project WebBaKI. 
WebBaKI is a project powered by the TH Brandenburg (https://informatik.th-brandenburg.de) and a possibility for organisations in Germany to check their security respectfully.
A description for how to setup, configure and use is in the hands of the teamleader and developers of the project.
Licencing is prescribed in the Licence.txt file.


## Project Structure

- **`controller` package**: Contains all the controllers handling HTTP requests.
- **`service` package**: Hosts the business logic of the application.
- **`security` package**: Defines all Spring Security-related configurations and security mechanisms.
- **`mail` package**: Includes email templates and the service for sending emails.
- **`entity` package**: Contains all database entities (the model layer).
- **`repository` package**: Holds all repositories used for database interactions.
- **`configuration` package**: Includes additional configurations, such as the data loader.

## Application Overview

This is a standard Spring Boot application following conventional mechanisms and best practices. The primary configuration parameters are sourced from environment variables. Below is a list of these variables as defined in the `application.properties` file:

---

### Environment Variables

#### Database Configuration
- `DB_HOST`: Hostname of the database server (default: `localhost`).
- `DB_PORT`: Port number of the database server (default: `3306`).
- `DB_NAME`: Name of the database (default: `webbaki`).
- `DB_USERNAME`: Database username (default: `username`).
- `DB_PASSWORD`: Database password (default: `password`).

#### Server Configuration
- `PORT`: Port on which the application will run (default: `8080`).
- `SESSION_TIMEOUT`: Session timeout duration in seconds (default: `28800`).


#### Mail Configuration
- `MAIL_HOST`: Mail server host (default: `mail.host`).
- `MAIL_PORT`: Mail server port (default: `25`).
- `MAIL_USER`: Mail server username (default: `user@mail.mail`).
- `MAIL_PASSWORD`: Mail server password (default: `noPw`).

#### Miscellaneous
- `HELP_PATH`: Path for help PDF files (default: `/var/webbaki/help/`).
- `HOSTNAME`: Backend hostname (default: `localhost:8080/`).
- `EXPIRY_ADMIN`: Admin expiry duration in days (default: `14`).
- `EXPIRY_USER`: User expiry duration in days (default: `3`).

---

## Initial Setup

For the initial start, the following setup steps must be performed in the database:

1. Create a new user in the `user` table.
2. Add an entry in the `users_roles` table to grant admin privileges to this user (`id = 1`).
3. Populate all required initial data, including:
    - Branches
    - Master scenarios
    - Scenarios


Credits:
Teamleader is Prof. Dr. Michael Pilgermann (EMail: michael.pilgerman@th-brandenburg.de);
