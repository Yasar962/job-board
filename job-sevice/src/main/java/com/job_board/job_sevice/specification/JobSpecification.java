package com.job_board.job_sevice.specification;

import org.springframework.data.jpa.domain.Specification;

public class JobSpecification {
    public static Specification hasTitle(String title) {
        return (root, query, cb) ->
                title == null ? null : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() +
                                                                            "%");
    }
    public static Specification hasLocation(String location) {
        return (root, query, cb) ->
                location == null ? null : cb.like(cb.lower(root.get("location")), "%" +
                        location.toLowerCase() + "%");
    }
    public static Specification salaryBetween(Double min, Double max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min == null) return cb.lessThanOrEqualTo(root.get("maxSalary"), max);
            if (max == null) return cb.greaterThanOrEqualTo(root.get("minSalary"), min);
            return cb.and(cb.greaterThanOrEqualTo(root.get("minSalary"), min),
                    cb.lessThanOrEqualTo(root.get("maxSalary"), max));
        };
    }
}