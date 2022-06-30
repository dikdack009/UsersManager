package pet.kodmark.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pet.kodmark.repository.MySQLConnection;

@SpringBootApplication
public class App {
    public static void main( String[] args ) {
        MySQLConnection.setUpDB();
        SpringApplication.run( App.class, args );
        MySQLConnection.closeConnection();
    }
}
