package mipkasnow.dev.rococo.util;

import lombok.experimental.UtilityClass;
import mipkasnow.dev.rococo.entity.ArtistEntity;
import mipkasnow.dev.rococo.entity.MuseumEntity;
import mipkasnow.dev.rococo.entity.PaintingEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@UtilityClass
public class PageUtil {

    private static final int MAX_PAGE_SIZE = 50;

    public static Pageable createPageableUnsorted(Integer page, Integer size) {
        return createPageableAscSorted(page, size, Sort.unsorted());
    }

    public static Pageable createPageableAscSorted(Integer page, Integer size, Sort sort) {
        int pageNumber = page < 0 ? 0 : page;
        int pageSize = size <= 0 ? 1 : size;
        return PageRequest.of(pageNumber, Math.min(pageSize, MAX_PAGE_SIZE), sort);
    }

    public Sort defaultAscSort(String byField) {
        return Sort.by(Sort.Direction.ASC, byField);
    }
}
