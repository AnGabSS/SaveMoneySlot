package com.tech.padawan.financialmanager.goal.model;

import com.tech.padawan.financialmanager.user.model.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "Name is required")
    private String name;
    @NotNull(message = "Target Amount is required")
    @Min(value = 0, message = "Target cannot be a negative number")
    private double targetAmount;
    @NotNull(message = "Saved Amount is required")
    private double savedAmount;
    @Nullable
    private String reason;
    @Nullable
    private Date deadline;
    @ColumnDefault("false")
    private boolean isCompleted;
    private Date createdAt;
    private Date updatedAt;
    @ManyToOne
    @NotNull(message = "User is required")
    private User user;

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
