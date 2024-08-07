package site.globitokuki.globitokuki_backend.dtos.selenium;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CookiesDTO {
  private String domain;
  private Long expirationDate;
  private Boolean hostOnly;
  private Boolean httpOnly;
  private String name;
  private String path;
  private String sameSite;
  private Boolean secure;
  private Boolean session;
  private String storeId;
  private String value;
}