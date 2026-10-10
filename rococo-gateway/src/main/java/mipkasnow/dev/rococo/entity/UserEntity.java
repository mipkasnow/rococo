package mipkasnow.dev.rococo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mipkasnow.dev.rococo.dto.user.UserUpdateRequest;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.descriptor.jdbc.UUIDJdbcType;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @JdbcType(UUIDJdbcType.class)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = true, length = 255)
    private String firstname;

    @Column(nullable = true, length = 255)
    private String lastname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_file_id")
    private FileEntity avatarFile;

    public UserEntity(String username) {
        this.username = username;
    }

    public void update(UserUpdateRequest updateRequest, FileEntity avatarFile) {
        this.firstname = updateRequest.firstname();
        this.lastname = updateRequest.lastname();
        this.avatarFile = avatarFile;
    }
}
