package ru.msu.cmc.webprak.models;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Table(name = "site_user")
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor

public class Site_user implements CommonEntity<Long>{
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
    private Roles role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "status")
    @ToString.Exclude
    @NonNull
    private User_statuses status;

    @Column(nullable = false, name = "full_name_company")
    @NonNull
    private String full_name_company;

    @Column(unique = true, nullable = false, name = "email")
    @NonNull
    private String email;

    @Column(nullable = true, name = "home_address")
    private String home_address;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "education")
    @ToString.Exclude
    private Educational_institutions education;
}
