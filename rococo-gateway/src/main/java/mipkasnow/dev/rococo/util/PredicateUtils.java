package mipkasnow.dev.rococo.util;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.function.Supplier;

@UtilityClass
public class PredicateUtils {

    public static void addIfNotBlank(List<Predicate> predicates, String value, Supplier<Predicate> predicateSupplier) {
        if (value != null && !value.isBlank()) {
            predicates.add(predicateSupplier.get());
        }
    }

    public static <T, K> Predicate likePredicate(From<T, K> from, CriteriaBuilder criteriaBuilder, String fieldName, String value) {
        return criteriaBuilder.like(
                criteriaBuilder.lower(from.get(fieldName)),
                "%" + value.toLowerCase() + "%"
        );
    }
}
