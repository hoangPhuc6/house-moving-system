package com.housemoving.backend.entity;

import com.housemoving.backend.enums.EmploymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "staff")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(name = "staff_code", unique = true, length = 30)
    private String staffCode;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status", nullable = false, length = 30)
    private EmploymentStatus employmentStatus = EmploymentStatus.ACTIVE;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
