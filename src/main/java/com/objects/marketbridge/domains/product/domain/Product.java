package com.objects.marketbridge.domains.product.domain;

import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.domains.category.domain.Category;
import com.objects.marketbridge.domains.coupon.domain.Coupon;
import com.objects.marketbridge.domains.member.domain.BaseEntity;
import com.objects.marketbridge.domains.order.domain.OrderDetail;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.OUT_OF_STOCK;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product")
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<ProdOption> prodOptions = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<ProductImage> productImages = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<ProdTag> prodTags = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Coupon> coupons = new ArrayList<>();

    private Boolean isOwn; // 로켓 true , 오픈 마켓 false

    private String name;

    private Long price;

    private Boolean isSubs;

    private Long stock;

    private String thumbImg;

    private Long discountRate;

    private String productNo;

    @Builder
    private Product(Long id, Category category, Boolean isOwn, String name, Long price, Boolean isSubs, Long stock, String thumbImg, Long discountRate,String productNo) {
        this.id = id;
        this.category = category;
        this.isOwn = isOwn; // 로켓 true , 오픈 마켓 false
        this.name = name;
        this.price = price;
        this.isSubs = isSubs;
        this.thumbImg = thumbImg;
        this.discountRate = discountRate;
        this.stock = stock;
        this.productNo = productNo;
    }

    public static Product create( Boolean isOwn, String name, Long price, Boolean isSubs, Long stock, String thumbImg, Long discountRate, String productNo){
        return Product.builder()
                .isOwn(isOwn)
                .name(name)
                .price(price)
                .isSubs(isSubs)
                .stock(stock)
                .thumbImg(thumbImg)
                .discountRate(discountRate)
                .productNo(productNo)
                .build();
    }

    public void setCategory(Category category){
        this.category = category;
    }

    public void addProductImages(ProductImage productImage){
        productImages.add(productImage);
        productImage.setProduct(this);
    }

    public void addProdOptions(ProdOption prodOption){
        prodOptions.add(prodOption);
        prodOption.setProduct(this);
    }

    public void addProdTags(ProdTag prodTag){
        prodTags.add(prodTag);
        prodTag.setProduct(this);
    }

    public void addOrderDetail(OrderDetail orderDetail) {
        orderDetails.add(orderDetail);
        orderDetail.setProduct(this);
    }

    public void increase(Long quantity) {
        stock += quantity;
    }

    public void decrease(Long quantity) {
        verifyStockAvailable(quantity);
        stock -= quantity;
    }

    public void verifyStockAvailable(Long quantity) {
        if (stock - quantity < 0) {
            throw CustomLogicException.createBadRequestError(OUT_OF_STOCK);
        }
    }
}
