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
import mipkasnow.dev.rococo.dto.artist.ArtistCreateRequest;
import mipkasnow.dev.rococo.dto.artist.ArtistUpdateRequest;
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
@Table(name = "artist")
public class ArtistEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @JdbcType(UUIDJdbcType.class)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String name;

    @Column(nullable = false, length = 2000)
    private String biography;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "photo_file_id", nullable = false)
    private FileEntity photoFile;

    public ArtistEntity(ArtistCreateRequest createRequest, FileEntity photoFile) {
        this.name = createRequest.name();
        this.biography = createRequest.biography();
        this.photoFile = photoFile;
    }

    public void update(ArtistUpdateRequest updateRequest, FileEntity photoFile) {
        this.name = updateRequest.name();
        this.biography = updateRequest.biography();
        this.photoFile = photoFile;
    }
}
