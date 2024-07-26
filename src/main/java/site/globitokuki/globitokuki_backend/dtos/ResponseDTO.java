package site.globitokuki.globitokuki_backend.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO {
  private String message;
  private Boolean isTokenValid;
  private Integer code;

  public ResponseDTO(String message) {
    this.message = message;
  }

  public ResponseDTO(String message, Integer code) {
    this.message = message;
    this.code = code;
  }

  public ResponseDTO(String message, Integer code, Boolean isTokenValid) {
    this.message = message;
    this.code = code;
    this.isTokenValid = isTokenValid;
  }

  public ResponseDTO(Boolean isTokenValid, Integer code) {
    this.isTokenValid = isTokenValid;
    this.code = code;
  }
}
