package br.com.confidence;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class HashPasswordEncoder {
    public static void main(String[] args) {
    PasswordEncoder encoder = new BCryptPasswordEncoder();
    System.out.println("ADMIN  = " + encoder.encode(""));
    System.out.println("USER   = " + encoder.encode(""));
}

}
