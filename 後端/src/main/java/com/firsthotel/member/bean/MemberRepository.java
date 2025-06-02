package com.firsthotel.member.bean;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;



public interface MemberRepository extends JpaRepository<Member, Integer>, JpaSpecificationExecutor<Member>  {

	@Query("select m from Member m where m.email = :email and m.isDeleted = 0")
	Member findByEmailIsDeleted(String email);
	
	Member findByEmail(String email);
	
	@Query("select m from Member m where m.email = :email and m.isDeleted = 0")
	Optional<Member> findByEmail1(String email);
	
	Member findByResetToken(String resetToken);

	Member findByPhone(String phone);
}
