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
import mipkasnow.dev.rococo.dto.museum.MuseumCreateRequest;
import mipkasnow.dev.rococo.dto.museum.MuseumUpdateRequest;
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
@Table(name = "museum")
public class MuseumEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @JdbcType(UUIDJdbcType.class)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String title;

    @Column(nullable = true, length = 1000)
    private String description;

    @Column(nullable = true, length = 255)
    private String city;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_id", nullable = false)
    private CountryEntity country;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "photo_file_id", nullable = false)
    private FileEntity photoFile;

    public MuseumEntity(MuseumCreateRequest createRequest, CountryEntity country, FileEntity photoFile) {
        this.title = createRequest.title();
        this.description = createRequest.description();
        this.city = createRequest.geo().city();
        this.photoFile = photoFile;
        this.country = country;
    }

    public void update(MuseumUpdateRequest updateRequest, CountryEntity country, FileEntity photoFile) {
        this.title = updateRequest.title();
        this.description = updateRequest.description();
        this.city = updateRequest.geo().city();
        this.photoFile = photoFile;
        this.country = country;
    }
}
