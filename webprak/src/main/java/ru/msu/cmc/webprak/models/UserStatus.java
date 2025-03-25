package ru.msu.cmc.webprak.models;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Table(name = "user_statuses")
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor

public class UserStatus implements CommonEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "status_id")
    private Long id;

    @Column(nullable = false, name = "status_name")
    @NonNull
    private String statusName;
}
