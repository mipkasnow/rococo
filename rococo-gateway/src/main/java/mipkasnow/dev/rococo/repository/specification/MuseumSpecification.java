package mipkasnow.dev.rococo.repository.specification;

import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;
import mipkasnow.dev.rococo.entity.MuseumEntity;
import mipkasnow.dev.rococo.util.PredicateUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class MuseumSpecification {

    public static Specification<MuseumEntity> filter(String museumTitle) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            PredicateUtils.addIfNotBlank(predicates, museumTitle, () -> PredicateUtils.likePredicate(root, cb, MuseumEntity.Fields.title, museumTitle));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
