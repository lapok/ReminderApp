package org.example.reminderapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private static final String DEFAULT_TOKEN_TYPE = "Bearer";

    private String token;

    @Builder.Default
    private String type = DEFAULT_TOKEN_TYPE;

    private Long id;
    private String email;

    public AuthResponse(String token, Long id, String email){
        this.token = token;
        this.id = id;
        this.email = email;
        this.type = DEFAULT_TOKEN_TYPE;
    }

}
