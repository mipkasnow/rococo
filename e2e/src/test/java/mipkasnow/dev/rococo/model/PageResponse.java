package mipkasnow.dev.rococo.model;

import java.util.List;

public record PageResponse<T>(List<T> content, Metadata page) {

    public record Metadata(long size, long number, long totalElements, long totalPages) {
    }
}
