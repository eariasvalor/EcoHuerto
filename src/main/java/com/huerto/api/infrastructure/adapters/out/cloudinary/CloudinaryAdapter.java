package com.huerto.api.infrastructure.adapters.out.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.huerto.api.domain.exception.ImageUploadException;
import com.huerto.api.domain.ports.out.ImageStoragePort;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class CloudinaryAdapter implements ImageStoragePort {

    private final Cloudinary cloudinary;

    public CloudinaryAdapter(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public ImageUploadResult upload(byte[] bytes, String originalFilename, String folder) {
        try {
            String publicId = folder + "/" + stripExtension(originalFilename);
            Map<?, ?> result = cloudinary.uploader().upload(bytes, ObjectUtils.asMap(
                    "folder",    folder,
                    "public_id", publicId,
                    "overwrite", true
            ));
            return new ImageUploadResult(
                    (String) result.get("public_id"),
                    (String) result.get("secure_url")
            );
        } catch (IOException e) {
            throw new ImageUploadException("Cloudinary upload failed", e);
        }
    }

    @Override
    public void delete(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new ImageUploadException("Cloudinary delete failed", e);
        }
    }

    private String stripExtension(String filename) {
        if (filename == null) return "image";
        int dot = filename.lastIndexOf('.');
        return dot > 0 ? filename.substring(0, dot) : filename;
    }
}