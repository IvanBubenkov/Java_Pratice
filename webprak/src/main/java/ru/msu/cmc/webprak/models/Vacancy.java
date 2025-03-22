package ru.msu.cmc.webprak.models;

import lombok.*;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Entity
@Table(name = "vacancy")
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor

public class Vacancy implements CommonEntity<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "vacancy_id")
    private Long id;

    @Column(nullable = false, name = "vacancy_name")
    @NonNull
    private String vacancy_name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "company_id")
    @ToString.Exclude
    @NonNull
    private Site_user company_id;

    @Column(columnDefinition = "TEXT", name = "description")
    private String description;

    @Column(name = "min_salary", precision = 10, scale = 2)
    private BigDecimal minSalary;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "education")
    @ToString.Exclude
    private Educational_institutions education;

    @Column(nullable = false, name = "number_of_views")
    @ColumnDefault("0")
    private Long number_of_views;
}
