package ru.msu.cmc.webprak.models;

import lombok.*;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "work_history")
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor

public class Work_history implements CommonEntity<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "record_id")
    private Long id;

    @Column(nullable = false, name = "vacancy_name")
    @NonNull
    private String vacancy_name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "applicant_id")
    @ToString.Exclude
    @NonNull
    private Site_user applicant_id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "company_id")
    @ToString.Exclude
    @NonNull
    private Site_user company_id;

    @Column(nullable = false, name = "salary")
    @NonNull
    private Long salary;

    @Column(nullable = false, name = "date_start")
    private LocalDate date_start;

    @Column(name = "date_end")
    private LocalDate date_end;
}
