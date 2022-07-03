package pet.kodmark.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pet.kodmark.repository.UserRepository;

@SpringBootApplication
public class App {
    public static void main( String[] args ) {
        UserRepository.setUpDB();
        SpringApplication.run( App.class, args );
        UserRepository.closeConnection();
    }
}
