package com.tech.padawan.financialmanager.champion.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="champion_levels")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ChampionLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "title is required")
    private String title;

    private LocalDateTime createdAt;

    @NotNull(message = "PointsNeeded is required")
    @ColumnDefault("0")
    private int pointsNeeded;

    @NotNull(message = "PointsMax is required")
    @ColumnDefault("0")
    private int pointsMax;

    public boolean pointsIsEnough(Integer points){
        return this.pointsNeeded <= points && this.pointsMax >= points;
    }
}
