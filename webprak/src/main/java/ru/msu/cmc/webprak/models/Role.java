package ru.msu.cmc.webprak.models;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import jakarta.persistence.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Role implements CommonEntity<Long>, GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "role_id")
    private Long id;

    @Column(nullable = false, name = "role_name")
    @NonNull
    private String roleName;

    /* Реализация метода GrantedAuthority */
    @Override
    public String getAuthority() {
        // Префикс ROLE_ требуется для Spring Security
        return "ROLE_" + roleName.toUpperCase();
    }
}