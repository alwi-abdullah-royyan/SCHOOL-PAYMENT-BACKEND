package com.beta.schoolpayment.specification;

import com.beta.schoolpayment.dto.request.PaymentFilterCriteria;
import com.beta.schoolpayment.model.Payment;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PaymentSpecification implements Specification<Payment> {

    private final PaymentFilterCriteria criteria;

    public PaymentSpecification(PaymentFilterCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Payment> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (criteria.getPaymentName() != null) {
            predicates.add(cb.like(cb.lower(root.get("paymentName")), "%" + criteria.getPaymentName().toLowerCase() + "%"));
        }

        Join<Object, Object> studentJoin = null;
        if (criteria.getStudentName() != null || criteria.getSchoolYearStartDate() != null) {
            studentJoin = root.join("student", JoinType.LEFT);
        }

        if (criteria.getStudentName() != null) {
            predicates.add(cb.like(cb.lower(studentJoin.get("name")), "%" + criteria.getStudentName().toLowerCase() + "%"));
        }

        if (criteria.getUserName() != null) {
            Join<Object, Object> userJoin = root.join("user", JoinType.LEFT);
            predicates.add(cb.like(cb.lower(userJoin.get("name")), "%" + criteria.getUserName().toLowerCase() + "%"));
        }

        if (criteria.getSchoolYearStartDate() != null && criteria.getSchoolYearEndDate() != null) {
            Join<Object, Object> schoolYearJoin = studentJoin.join("schoolYear", JoinType.LEFT);
            predicates.add(cb.between(
                    schoolYearJoin.get("startDate"),
                    criteria.getSchoolYearStartDate(),
                    criteria.getSchoolYearEndDate()
            ));
        }

        if (criteria.getPaymentStatus() != null) {
            predicates.add(cb.equal(root.get("paymentStatus"), criteria.getPaymentStatus()));
        }

        query.distinct(true); // Menghindari duplikasi hasil jika ada multiple joins

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
