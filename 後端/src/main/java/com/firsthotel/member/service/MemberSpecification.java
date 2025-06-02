package com.firsthotel.member.service;

import org.springframework.data.jpa.domain.Specification;

import com.firsthotel.member.bean.Member;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class MemberSpecification implements Specification<Member> {

    private String name;
    private Integer phone;
    private String email;

    public MemberSpecification(String name, Integer phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    @Override
    public Predicate toPredicate(Root<Member> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate finalPredicate = criteriaBuilder.conjunction();  // 初始化为所有条件都成立

        // 根据 name 条件构造查询
        if (name != null && !name.isEmpty()) {
            finalPredicate = criteriaBuilder.and(finalPredicate, criteriaBuilder.like(root.get("name"), "%" + name + "%"));
        }

        // 根据 phone 条件构造查询
        if (phone != null) {
            finalPredicate = criteriaBuilder.and(finalPredicate, criteriaBuilder.equal(root.get("phone"), phone));
        }

        // 根据 email 条件构造查询
        if (email != null && !email.isEmpty()) {
            finalPredicate = criteriaBuilder.and(finalPredicate, criteriaBuilder.like(root.get("email"), "%" + email + "%"));
        }

        // 只查询未被软删除的会员，isDeleted == 0
        finalPredicate = criteriaBuilder.and(finalPredicate, criteriaBuilder.equal(root.get("isDeleted"), 0));

        return finalPredicate;
    }
}
