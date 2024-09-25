package com.egleprojects.book_info.filter;

import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;

public class GenericSpecificationBuilder<T> {
    public Specification<T> buildSpecification(Map<String, Object> filters) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            for (Map.Entry<String, Object> filter : filters.entrySet()) {
                String key = filter.getKey();
                Object value = filter.getValue();

                switch (key) {
                    case String k when k.startsWith("min"): {
                        String field = key.substring(3);
                        predicate = criteriaBuilder.and(
                                predicate,
                                criteriaBuilder.greaterThanOrEqualTo(root.get(field), value.toString())
                        );
                        break;
                    }
                    case String k when k.startsWith("max"): {
                        String field = k.substring(3);
                        predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get(field), value.toString()));
                        break;
                    }
                    case String k when k.startsWith("between"): {
                        String field = key.substring(7);
                        String[] range = ((String) value).split(",");
                        predicate = criteriaBuilder.and(predicate, criteriaBuilder.between(root.get(field), range[0], range[1]));
                        break;
                    }
                    default:
                        if (NumberUtils.isCreatable(value.toString())) {
                            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(key), value));
                        } else if (value instanceof String) {
                            predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get(key), "%" + value + "%"));
                        }
                        break;
                }
            }
            return predicate;
        };
    }
}
