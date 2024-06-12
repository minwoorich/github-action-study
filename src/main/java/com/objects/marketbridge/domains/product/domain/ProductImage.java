package com.objects.marketbridge.domains.product.domain;

import com.objects.marketbridge.domains.image.domain.Image;
import com.objects.marketbridge.domains.member.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image image;

    //String으로 할것. 예시 : ImageType.ITEM_IMG.toString()
    private String imgType;

    private Long seqNo;

    @Builder
    private ProductImage(Long id,Product product, Image image, Long seqNo,String imgType) {
        this.id = id;
        this.product = product;
        this.image = image;
        this.seqNo = seqNo;
        this.imgType = imgType;
    }

    public void setProduct(Product product) { this.product = product; }

    public void setImage(Image image){ this.image = image; }

    public static ProductImage create(Product product, Image image, Long seqNo,String imgType){
        return ProductImage.builder()
                .product(product)
                .image(image)
                .seqNo(seqNo)
                .imgType(imgType)
                .build();
    }

}

