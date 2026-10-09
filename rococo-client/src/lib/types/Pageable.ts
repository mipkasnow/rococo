export type Pageable<ContentType> = {
    content: ContentType,
    page: {
        size: number,
        number: number,
        totalElements: number,
        totalPages: number,
    },
}
