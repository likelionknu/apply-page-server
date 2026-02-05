package com.likelionknu.applyserver.auth.data.entity;

import com.likelionknu.applyserver.auth.data.enums.StudentStatus;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "profile")
public class Profile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
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