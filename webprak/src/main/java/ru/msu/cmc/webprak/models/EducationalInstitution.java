package ru.msu.cmc.webprak.models;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Table(name = "educational_institutions")
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor

public class EducationalInstitution implements CommonEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "education_id")
    private Long id;

    @Column(nullable = false, name = "education_level")
    @NonNull
    private String educationLevel;
}
