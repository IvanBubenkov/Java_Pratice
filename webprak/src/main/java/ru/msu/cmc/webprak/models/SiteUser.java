package ru.msu.cmc.webprak.models;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.persistence.*;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "site_user")
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class SiteUser implements CommonEntity<Long>, UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "user_id")
    private Long id;

    @Column(unique = true, nullable = false, name = "login")
    @NonNull
    private String login;

    @Column(nullable = false, name = "password")
    @NonNull
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "role")
    @ToString.Exclude
    @NonNull
    private Role role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "status")
    @ToString.Exclude
    @NonNull
    private UserStatus status;

    @Column(nullable = false, name = "full_name_company")
    @NonNull
    private String fullNameCompany;

    @Column(unique = true, nullable = false, name = "email")
    @NonNull
    private String email;

    @Column(name = "home_address")
    private String homeAddress;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "education")
    @ToString.Exclude
    private EducationalInstitution education;

    /* Реализация методов UserDetails */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String authority = "ROLE_" + role.getRoleName().toUpperCase();
        System.out.println("[DEBUG] GrantedAuthority: " + authority); // Логируем
        return Collections.singleton(new SimpleGrantedAuthority(authority));
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Аккаунт не заблокирован, если статус "Активный" (1)
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Конструкторы
    public SiteUser(String login,
                    String password,
                    Role role,
                    UserStatus status,
                    String fullNameCompany,
                    String email,
                    String homeAddress,
                    EducationalInstitution education) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.status = status;
        this.fullNameCompany = fullNameCompany;
        this.email = email;
        this.homeAddress = homeAddress;
        this.education = education;
    }

    public SiteUser(String login,
                    String password,
                    Role role,
                    UserStatus status,
                    String fullNameCompany,
                    String email,
                    String homeAddress) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.status = status;
        this.fullNameCompany = fullNameCompany;
        this.email = email;
        this.homeAddress = homeAddress;
    }
}