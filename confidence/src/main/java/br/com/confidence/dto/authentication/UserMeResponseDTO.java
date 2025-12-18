package br.com.confidence.dto.authentication;

import java.util.Set;

public record UserMeResponseDTO(

        Long id,
        String name,
        String email,
        Set<String> roles

) {

}
