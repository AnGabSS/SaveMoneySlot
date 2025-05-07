package com.tech.padawan.financialmanager.goal.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.Date;

@Entity
@Table(name = "goals")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Goal {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;
    private double targetAmount;
    private double savedAmount;
    @Nullable
    private String reason;
    @Nullable
    private Date deadline;
    @ColumnDefault("false")
    private boolean isCompleted;
    private Date createdAt;
    private Date updatedAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt = new Date();
    }

    @PreUpdate
    protected void onUpdate(){
        this.updatedAt = new Date();
        this.isCompleted = this.savedAmount >= this.targetAmount;
    }


}
