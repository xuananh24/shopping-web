package com.example.auditlogservice.repository;

import com.example.auditlogservice.model.entity.UserServiceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserServiceLogRepository extends JpaRepository<UserServiceLog, Long> {
}
