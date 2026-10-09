package mipkasnow.dev.rococo.service;

import lombok.RequiredArgsConstructor;
import mipkasnow.dev.rococo.entity.FileEntity;
import mipkasnow.dev.rococo.entity.MinioBucket;
import mipkasnow.dev.rococo.exception.ApiException;
import mipkasnow.dev.rococo.exception.ErrorCode;
import mipkasnow.dev.rococo.repository.FileRepository;
import mipkasnow.dev.rococo.dto.file.DecodedPhoto;
import mipkasnow.dev.rococo.util.PhotoCodecUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3FileService {

    private final S3Client s3Client;
    private final FileRepository fileRepository;

    public FileEntity save(MinioBucket bucket, FileEntity current, String dataUrl) {
        if (dataUrl == null || dataUrl.isBlank()) {
            return null;
        }
        DecodedPhoto decoded = PhotoCodecUtil.decode(dataUrl);
        String hash = sha256(decoded.bytes());
        if (current != null && hash.equals(current.getSha256())) {
            return current;
        }
        String previousKey = current == null ? null : current.getObjectKey();
        MinioBucket previousBucket = current == null ? null : current.getBucket();
        String objectKey = UUID.randomUUID() + "." + decoded.extension();
        put(bucket, objectKey, decoded);
        FileEntity file = current == null ? new FileEntity() : current;
        file.setObjectKey(objectKey);
        file.setBucket(bucket);
        file.setFileName(objectKey);
        file.setContentType(decoded.contentType());
        file.setSizeBytes(decoded.bytes().length);
        file.setSha256(hash);
        FileEntity saved = fileRepository.save(file);
        if (previousKey != null) {
            deleteObjectAfterCommit(previousBucket, previousKey);
        }
        return saved;
    }

    public void delete(FileEntity file) {
        if (file == null) {
            return;
        }
        MinioBucket bucket = file.getBucket();
        String objectKey = file.getObjectKey();
        fileRepository.delete(file);
        deleteObjectAfterCommit(bucket, objectKey);
    }

    public String toDataUrl(FileEntity file) {
        if (file == null) {
            return null;
        }
        return PhotoCodecUtil.toDataUrl(load(file));
    }

    private void put(MinioBucket bucket, String objectKey, DecodedPhoto decoded) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket.name())
                .key(objectKey)
                .contentType(decoded.contentType())
                .contentLength((long) decoded.bytes().length)
                .build();
        try {
            s3Client.putObject(request, RequestBody.fromBytes(decoded.bytes()));
        } catch (SdkException exception) {
            throw new ApiException(ErrorCode.FileStorage, exception);
        }
    }

    private byte[] load(FileEntity file) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(file.getBucket().name())
                .key(file.getObjectKey())
                .build();
        try {
            return s3Client.getObjectAsBytes(request).asByteArray();
        } catch (SdkException exception) {
            throw new ApiException(ErrorCode.FileStorage, exception);
        }
    }

    private void deleteObject(MinioBucket bucket, String objectKey) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket.name())
                .key(objectKey)
                .build();
        try {
            s3Client.deleteObject(request);
        } catch (SdkException exception) {
            throw new ApiException(ErrorCode.FileStorage, exception);
        }
    }

    private void deleteObjectAfterCommit(MinioBucket bucket, String objectKey) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            deleteObject(bucket, objectKey);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                deleteObject(bucket, objectKey);
            }
        });
    }

    private static String sha256(byte[] bytes) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
