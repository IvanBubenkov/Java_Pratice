package ru.msu.cmc.webprak.models;

import lombok.*;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Entity
@Table(name = "resume")
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor

public class Resume implements CommonEntity<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "resume_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    @ToString.Exclude
    @NonNull
    private Site_user user_id;

    @Column(name = "min_salary_req", precision = 10, scale = 2)
    private BigDecimal min_salary_req;

    @Column(nullable = false, name = "number_of_views")
    @ColumnDefault("0")
    private Long number_of_views;

    @Column(nullable = false, name = "vacancy_name")
    @NonNull
    private String vacancy_name;
}
