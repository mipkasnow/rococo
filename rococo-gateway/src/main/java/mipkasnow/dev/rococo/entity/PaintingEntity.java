package mipkasnow.dev.rococo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import mipkasnow.dev.rococo.dto.painting.PaintingCreateRequest;
import mipkasnow.dev.rococo.dto.painting.PaintingUpdateRequest;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.descriptor.jdbc.UUIDJdbcType;

import java.util.UUID;

@Getter
@Setter
@FieldNameConstants
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@NoArgsConstructor
@Table(name = "painting")
public class PaintingEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @JdbcType(UUIDJdbcType.class)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, unique = false, length = 255)
    private String title;

    @Column(nullable = true, length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artist_id", nullable = false)
    private ArtistEntity artist;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "museum_id", nullable = true)
    private MuseumEntity museum;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "content_file_id", nullable = false)
    private FileEntity contentFile;

    public PaintingEntity(PaintingCreateRequest createRequest, ArtistEntity artist, MuseumEntity museum, FileEntity contentFile) {
        this.title = createRequest.title();
        this.description = createRequest.description();
        this.contentFile = contentFile;
        this.artist = artist;
        this.museum = museum;
    }

    public void update(PaintingUpdateRequest updateRequest, ArtistEntity artist, MuseumEntity museum, FileEntity contentFile) {
        this.title = updateRequest.title();
        this.description = updateRequest.description();
        this.contentFile = contentFile;
        this.artist = artist;
        this.museum = museum;
    }
}
