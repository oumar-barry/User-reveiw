package com.barry.full.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Jwt {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String token;
    private boolean expire;
    @ManyToOne(cascade = {CascadeType.DETACH,CascadeType.MERGE})
    private User user;


}
