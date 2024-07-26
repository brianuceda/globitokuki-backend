package site.globitokuki.globitokuki_backend.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponseDTO extends ResponseDTO {
  private String token;
  
  @Builder
  public AuthResponseDTO(String message, Integer code, Boolean isTokenValid, String token) {
    super(message, code, isTokenValid);
    this.token = token;
  }
}
