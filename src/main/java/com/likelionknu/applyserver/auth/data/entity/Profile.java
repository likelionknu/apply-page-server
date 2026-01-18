package com.likelionknu.applyserver.auth.data.entity;

import com.likelionknu.applyserver.auth.data.enums.StudentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "profile")
public class Profile {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "student_id", unique = true)
    private String studentId;

    @Column
    private String depart;

    @Column
    private String phone;

    @Column
    private Integer grade;

    @Enumerated(EnumType.STRING)
    @Column
    private StudentStatus status;
}
