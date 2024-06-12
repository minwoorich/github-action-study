package com.objects.marketbridge.domains.image.infra;

import com.objects.marketbridge.domains.image.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageJpaRepository extends JpaRepository<Image, Long> {

    @Modifying
    @Query("DELETE FROM Image i WHERE i.id IN :ids")
    void deleteByIdIn(@Param("ids") List<Long> ids);

}
