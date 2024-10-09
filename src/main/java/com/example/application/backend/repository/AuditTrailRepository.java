package com.example.application.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.application.backend.entity.AuditTrail;

public interface AuditTrailRepository extends JpaRepository<AuditTrail, Long> {

}
