package ru.msu.cmc.webprak.models;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Table(name = "selected_resumes")
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor

public class SelectedResume implements CommonEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "record_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "resume_id")
    @ToString.Exclude
    @NonNull
    private Resume resume;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "company_id")
    @ToString.Exclude
    @NonNull
    private SiteUser company;
}
