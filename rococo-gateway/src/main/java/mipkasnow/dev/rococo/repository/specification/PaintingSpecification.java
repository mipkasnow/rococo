package mipkasnow.dev.rococo.repository.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;
import mipkasnow.dev.rococo.entity.ArtistEntity;
import mipkasnow.dev.rococo.entity.PaintingEntity;
import mipkasnow.dev.rococo.util.PredicateUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class PaintingSpecification {

    public static Specification<PaintingEntity> filter(String paintingTitle, UUID artistId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            PredicateUtils.addIfNotBlank(predicates, paintingTitle, () -> PredicateUtils.likePredicate(root, cb, PaintingEntity.Fields.title, paintingTitle));
            if (artistId != null) {
                Join<PaintingEntity, ArtistEntity> artist = root.join(PaintingEntity.Fields.artist);
                predicates.add(cb.equal(artist.get(ArtistEntity.Fields.id), artistId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
