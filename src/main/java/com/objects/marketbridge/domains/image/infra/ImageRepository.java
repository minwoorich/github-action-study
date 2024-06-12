package com.objects.marketbridge.domains.image.infra;

import com.objects.marketbridge.domains.image.domain.Image;

import java.util.List;

public interface ImageRepository {

    void save(Image image);

    Image findById(Long id);

    void delete(Image image);

    void deleteById(Long id);

    void deleteAllByIdInBatch(List<Long> ids);
}
