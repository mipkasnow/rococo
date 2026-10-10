package mipkasnow.dev.rococo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mipkasnow.dev.rococo.config.context.UserContext;
import mipkasnow.dev.rococo.dto.user.UserResponse;
import mipkasnow.dev.rococo.dto.user.UserUpdateRequest;
import mipkasnow.dev.rococo.entity.FileEntity;
import mipkasnow.dev.rococo.entity.MinioBucket;
import mipkasnow.dev.rococo.entity.UserEntity;
import mipkasnow.dev.rococo.exception.ApiException;
import mipkasnow.dev.rococo.exception.ErrorCode;
import mipkasnow.dev.rococo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserContext userContext;
    private final S3FileService s3FileService;

    @Transactional
    public UserResponse current() {
        String username = userContext.getUsername();
        UserEntity user = userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.save(new UserEntity(username)));
        return toResponse(user);
    }

    @Transactional
    public UserResponse update(UserUpdateRequest request) {
        String username = userContext.getUsername();
        UserEntity user = userRepository.findByUsernameOrThrow(username);

        if (!username.equals(request.username()) || !user.getId().equals(request.id())) {
            throw new ApiException(ErrorCode.UserUpdateForbidden);
        }
        FileEntity previousAvatar = user.getAvatarFile();
        FileEntity avatar = s3FileService.save(MinioBucket.avatar, previousAvatar, request.avatar());
        user.update(request, avatar);
        UserEntity saved = userRepository.saveAndFlush(user);
        if (avatar == null && previousAvatar != null) {
            s3FileService.delete(previousAvatar);
        }

        log.info("User {} has been updated", username);

        return toResponse(saved);
    }

    private UserResponse toResponse(UserEntity user) {
        return new UserResponse(user, s3FileService.toDataUrl(user.getAvatarFile()));
    }
}
