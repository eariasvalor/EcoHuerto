package com.huerto.api.infrastructure.adapters.out.media;

import com.huerto.api.domain.model.MediaUploadResult;
import com.huerto.api.domain.ports.out.MediaStoragePort;
import com.huerto.api.infrastructure.config.WhatsAppProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@Profile("meta")
public class MetaMediaAdapter implements MediaStoragePort {

    private final RestTemplate restTemplate;
    private final WhatsAppProperties props;

    public MetaMediaAdapter(RestTemplate restTemplate, WhatsAppProperties props) {
        this.restTemplate = restTemplate;
        this.props = props;
    }

    @Override
    public MediaUploadResult upload(byte[] bytes, String mimeType, String filename) {
        String url = "https://graph.facebook.com/%s/%s/media"
                .formatted(props.api().version(), props.api().phoneNumberId());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(props.api().token());
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("messaging_product", "whatsapp");
        body.add("type", mimeType);
        body.add("file", new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        });

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        @SuppressWarnings("unchecked")
        Map<String, String> response = restTemplate.postForObject(url, request, Map.class);

        String mediaId = response != null ? response.get("id") : null;
        return MediaUploadResult.fromMeta(mediaId);
    }
}