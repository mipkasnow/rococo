package mipkasnow.dev.rococo.service;

import mipkasnow.dev.rococo.config.context.UserContext;
import mipkasnow.dev.rococo.dto.user.UserResponse;
import mipkasnow.dev.rococo.dto.user.UserUpdateRequest;
import mipkasnow.dev.rococo.entity.FileEntity;
import mipkasnow.dev.rococo.entity.MinioBucket;
import mipkasnow.dev.rococo.entity.UserEntity;
import mipkasnow.dev.rococo.exception.ApiException;
import mipkasnow.dev.rococo.exception.ErrorCode;
import mipkasnow.dev.rococo.junit.MockLogger;
import mipkasnow.dev.rococo.junit.UnitTest;
import mipkasnow.dev.rococo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.UUID;

import static mipkasnow.dev.rococo.helper.AssertUtil.assertApiException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
class UserServiceTest {

    @MockLogger
    static Logger log;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private S3FileService s3FileService;
    @InjectMocks
    private UserService userService;

    private final String username = "username";
    private final String firstName = "firstName";
    private final String lastName = "lastName";
    private final String avatar = "avatar";
    private final UUID userId = UUID.randomUUID();

    private final UserUpdateRequest userUpdateRequest = new UserUpdateRequest(userId, username, firstName, lastName, avatar);

    @Test
    void current_shouldReturnCurrentUser_whenUserAlreadyExists() {
        when(userContext.getUsername()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(
                new UserEntity(
                        userId,
                        username,
                        firstName,
                        lastName,
                        new FileEntity()
                )
        ));
        when(s3FileService.toDataUrl(any(FileEntity.class))).thenReturn(avatar);

        UserResponse current = userService.current();

        assertEquals(current.id(), userId);
        assertEquals(current.username(), username);
        assertEquals(current.firstname(), firstName);
        assertEquals(current.lastname(), lastName);
        assertEquals(current.avatar(), avatar);
    }

    @Test
    void current_shouldReturnCurrentUser_whenUserWasNotExists() {
        UserEntity userEntity = new UserEntity(username);
        userEntity.setId(userId);

        when(userContext.getUsername()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.save(new UserEntity(username))).thenReturn(userEntity);
        when(s3FileService.toDataUrl(isNull())).thenReturn(null);

        UserResponse current = userService.current();

        assertEquals(current.id(), userId);
        assertEquals(current.username(), username);
        assertNull(current.firstname());
        assertNull(current.lastname());
        assertNull(current.avatar());
    }

    @Test
    void current_shouldThrowUnauthorized_whenUsernameIsMissing() {
        when(userContext.getUsername()).thenThrow(new ApiException(ErrorCode.Unauthorized));

        assertApiException(ErrorCode.Unauthorized, () -> userService.current());
    }

    @Test
    void update_shouldThrowUnauthorized_whenUsernameIsMissing() {
        when(userContext.getUsername()).thenThrow(new ApiException(ErrorCode.Unauthorized));

        assertApiException(ErrorCode.Unauthorized, () -> userService.update(userUpdateRequest));
    }

    @Test
    void update_shouldThrowException_whenUserNotFound() {
        when(userContext.getUsername()).thenReturn(username);
        when(userRepository.findByUsernameOrThrow(username)).thenThrow(new ApiException(ErrorCode.UserNotFound));

        assertApiException(ErrorCode.UserNotFound, () -> userService.update(userUpdateRequest));
    }

    @Test
    void update_shouldLogUpdatedUser_whenRequestIsValid() {
        FileEntity previousAvatar = new FileEntity();
        FileEntity savedAvatar = new FileEntity();
        UserEntity user = new UserEntity(userId, username, firstName, lastName, previousAvatar);

        when(userContext.getUsername()).thenReturn(username);
        when(userRepository.findByUsernameOrThrow(username)).thenReturn(user);
        when(s3FileService.save(MinioBucket.avatar, previousAvatar, avatar)).thenReturn(savedAvatar);
        when(userRepository.saveAndFlush(user)).thenReturn(user);
        when(s3FileService.toDataUrl(savedAvatar)).thenReturn(avatar);

        UserResponse updated = userService.update(userUpdateRequest);

        assertEquals(updated.id(), userId);
        assertEquals(updated.username(), username);
        assertEquals(updated.firstname(), firstName);
        assertEquals(updated.lastname(), lastName);
        assertEquals(updated.avatar(), avatar);
        verify(log).info("User {} has been updated", username);
    }

    @Test
    void update_shouldThrowForbidden_whenUsernameDiffers() {
        UserEntity user = new UserEntity(userId, username, firstName, lastName, new FileEntity());
        UserUpdateRequest request = new UserUpdateRequest(userId, "other", firstName, lastName, avatar);

        when(userContext.getUsername()).thenReturn(username);
        when(userRepository.findByUsernameOrThrow(username)).thenReturn(user);

        assertApiException(ErrorCode.UserUpdateForbidden, () -> userService.update(request));
    }

    @Test
    void update_shouldThrowForbidden_whenIdDiffers() {
        UserEntity user = new UserEntity(userId, username, firstName, lastName, new FileEntity());
        UserUpdateRequest request = new UserUpdateRequest(UUID.randomUUID(), username, firstName, lastName, avatar);

        when(userContext.getUsername()).thenReturn(username);
        when(userRepository.findByUsernameOrThrow(username)).thenReturn(user);

        assertApiException(ErrorCode.UserUpdateForbidden, () -> userService.update(request));
    }

    @Test
    void update_shouldDeletePreviousAvatar_whenNewAvatarIsMissing() {
        FileEntity previousAvatar = new FileEntity();
        UserEntity user = new UserEntity(userId, username, firstName, lastName, previousAvatar);
        UserUpdateRequest request = new UserUpdateRequest(userId, username, firstName, lastName, null);

        when(userContext.getUsername()).thenReturn(username);
        when(userRepository.findByUsernameOrThrow(username)).thenReturn(user);
        when(s3FileService.save(MinioBucket.avatar, previousAvatar, null)).thenReturn(null);
        when(userRepository.saveAndFlush(user)).thenReturn(user);
        when(s3FileService.toDataUrl(isNull())).thenReturn(null);

        UserResponse updated = userService.update(request);

        assertEquals(updated.id(), userId);
        assertNull(updated.avatar());
        verify(s3FileService).delete(previousAvatar);
        verify(log).info("User {} has been updated", username);
    }

    @Test
    void update_shouldNotDeleteAvatar_whenUserHasNoAvatar() {
        UserEntity user = new UserEntity(userId, username, firstName, lastName, null);
        UserUpdateRequest request = new UserUpdateRequest(userId, username, firstName, lastName, null);

        when(userContext.getUsername()).thenReturn(username);
        when(userRepository.findByUsernameOrThrow(username)).thenReturn(user);
        when(s3FileService.save(MinioBucket.avatar, null, null)).thenReturn(null);
        when(userRepository.saveAndFlush(user)).thenReturn(user);
        when(s3FileService.toDataUrl(isNull())).thenReturn(null);

        UserResponse updated = userService.update(request);

        assertNull(updated.avatar());
        verify(s3FileService, never()).delete(any());
        verify(log).info("User {} has been updated", username);
    }
}