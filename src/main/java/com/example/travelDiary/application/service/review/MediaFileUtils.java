package com.example.travelDiary.application.service.review;

import com.example.travelDiary.presentation.dto.request.s3.PreSignedUploadInitiateRequest;

import java.math.BigInteger;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class MediaFileUtils {
    public static String guessContentTypeFromName(PreSignedUploadInitiateRequest request) {
        return URLConnection.guessContentTypeFromName(request.getOriginalFileName());
    }

    public static String generateMD5Hash(String source) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(source.getBytes(StandardCharsets.UTF_8));
            BigInteger bigInt = new BigInteger(1, bytes);
            return bigInt.toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String generateKey(PreSignedUploadInitiateRequest request, String contentType, UUID userProfileId) {
        String sanitizedFileName = request
                .getOriginalFileName()
                .replaceAll("[^a-zA-Z0-9._-]", "_")
                .toLowerCase();

        String keyfront = String.format("uploads/%s/%s/%s/%s",
                userProfileId,
                sanitizedFileName,
                contentType,
                request.getScheduleId()
        );
        return keyfront + "/" + generateMD5Hash(keyfront);
    }

}
