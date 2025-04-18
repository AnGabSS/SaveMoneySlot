package com.tech.padawan.financialmanager.user.repository;

import com.tech.padawan.financialmanager.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
