package site.globitokuki.globitokuki_backend.entities;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cookie")
public class CookieEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 512)
    private String domain;

    private Long expirationDate;
    private Boolean hostOnly;
    private Boolean httpOnly;

    @Column(length = 512)
    private String name;

    @Column(length = 512)
    private String path;

    @Column(length = 512)
    private String sameSite;

    private Boolean secure;
    private Boolean session;

    @Column(length = 512)
    private String storeId;

    @Column(length = 2048)
    private String value;
}