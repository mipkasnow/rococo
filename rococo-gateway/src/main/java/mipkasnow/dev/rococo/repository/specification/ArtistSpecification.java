package mipkasnow.dev.rococo.repository.specification;

import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;
import mipkasnow.dev.rococo.entity.ArtistEntity;
import mipkasnow.dev.rococo.util.PredicateUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ArtistSpecification {

    public static Specification<ArtistEntity> filter(String artistName) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            PredicateUtils.addIfNotBlank(predicates, artistName, () -> PredicateUtils.likePredicate(root, cb, ArtistEntity.Fields.name, artistName));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
