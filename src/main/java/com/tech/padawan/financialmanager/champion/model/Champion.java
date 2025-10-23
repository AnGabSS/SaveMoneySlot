package com.tech.padawan.financialmanager.champion.model;

import com.tech.padawan.financialmanager.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="champions")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Champion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nickname is required")
    @Column(unique = true)
    private String nickname;

    @NotNull(message = "Points is required")
    @Min(value = 0, message = "Value be a negative number")
    private int points;

    @OneToOne
    @NotNull(message = "User is required")
    private User user;

}
