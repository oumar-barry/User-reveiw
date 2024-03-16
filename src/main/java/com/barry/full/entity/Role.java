package com.barry.full.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Role {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;
    @Enumerated(EnumType.STRING)
    private RoleType title;


}
