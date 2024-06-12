create table address
(
    address_id bigint auto_increment
        primary key,
    member_id  bigint       null,
    alias      varchar(255) null,
    city       varchar(255) null,
    name       varchar(255) null,
    phone_no   varchar(255) null,
    street     varchar(255) null,
    zipcode    varchar(255) null,
    detail     varchar(255) null,
    is_default bit          null,
    created_at datetime(6)  null,
    updated_at datetime(6)  null,
    deleted_at datetime(6)  null
)
;

create table cart
(
    cart_id    bigint auto_increment
        primary key,
    member_id  bigint      null,
    product_id bigint      null,
    is_subs    bit         null,
    quantity   bigint      null,
    created_at datetime(6) null,
    updated_at datetime(6) null,
    deleted_at datetime(6) null
)
;

create table category
(
    category_id bigint auto_increment
        primary key,
    parent_id   bigint       null,
    level       bigint       null,
    name        varchar(255) null,
    created_at  datetime(6)  null,
    updated_at  datetime(6)  null,
    deleted_at  datetime(6)  null
)
;

create table coupon
(
    coupon_id     bigint auto_increment
        primary key,
    product_id    bigint       null,
    product_group_id    bigint       null,
    name          varchar(255) null,
    price         varchar(255) null,
    minimum_price bigint       null,
    count         bigint       null,
    start_date    datetime(6)  null,
    end_date      datetime(6)  null,
    created_at    datetime(6)  null,
    updated_at    datetime(6)  null,
    deleted_at    datetime(6)  null
)
;

create table image
(
    image_id   bigint auto_increment
        primary key,
    url        varchar(255) null,
    created_at datetime(6)  null,
    updated_at datetime(6)  null,
    deleted_at datetime(6)  null
)
;

create table member
(
    member_id  bigint auto_increment
        primary key,
    membership varchar(255) null,
    email      varchar(255) null,
    phone_no   varchar(255) null,
    name       varchar(255) null,
    password   varchar(255) null,
    is_agree   bit          null,
    is_alert   bit          null,
    created_at datetime(6)  null,
    updated_at datetime(6)  null,
    deleted_at datetime(6)  null
)
;

create table member_coupon
(
    member_coupon_id bigint auto_increment
        primary key,
    coupon_id        bigint      null,
    member_id        bigint      null,
    is_used          bit         null,
    used_date        datetime(6) null,
    end_date         datetime(6) null,
    created_at       datetime(6) null,
    updated_at       datetime(6) null,
    deleted_at       datetime(6) null
)
;

CREATE TABLE wishlist (
    wishlist_id	bigint auto_increment
        primary key,
    member_id	bigint	        null,
    product_id  bigint      null,
    created_at	datetime(6)	    null,
    updated_at	datetime(6)	    null,
    deleted_at	datetime(6)     null,
    UNIQUE (member_id, product_id)
)
;

create table membership
(
    membership_id      bigint auto_increment
        primary key,
    member_id          bigint       null,
    cid                varchar(255) null,
    tid                varchar(255) null,
    sid                varchar(255) null,
    item_name          varchar(255) null,
    subs_order_no      varchar(255) null,
    status_code        varchar(255) null,
    payment_method     varchar(255) null,
    quantity           bigint       null,
    total_amount       bigint       null,
    discount_amount    bigint       null,
    tax_free_amount    bigint       null,
    card_issuer_name   varchar(255) null,
    card_purchase_name varchar(255) null,
    card_no            varchar(255) null,
    card_install_month bigint       null,
    next_billing_date  date         null,
    created_at         datetime(6)  null,
    updated_at         datetime(6)  null,
    deleted_at         datetime(6)  null
)
;

create table option_category
(
    option_category_id bigint auto_increment
        primary key,
    name               varchar(255) null,
    created_at         datetime(6)  null,
    updated_at         datetime(6)  null,
    deleted_at         datetime(6)  null
)
;

create table options
(
    option_id          bigint auto_increment
        primary key,
    option_category_id bigint       null,
    name               varchar(255) null,
    created_at         datetime(6)  null,
    updated_at         datetime(6)  null,
    deleted_at         datetime(6)  null
)
;

create table order_detail
(
    order_detail_id  bigint auto_increment
        primary key,
    order_id         bigint       null,
    product_id       bigint       null,
    order_no         varchar(255) null,
    member_coupon_id bigint       null,
    reward_type      varchar(255) null,
    quantity         bigint       null,
    price            bigint       null,
    seller_id        bigint       null,
    status_code      varchar(255) null,
    tid              varchar(255) null,
    delivered_date   datetime(6)  null,
    reduced_quantity  bigint       null,
    cancelled_at     datetime(6)  null,
    created_at       datetime(6)  null,
    updated_at       datetime(6)  null,
    deleted_at       datetime(6)  null
)
;

create table orders
(
    order_id       bigint auto_increment
        primary key,
    member_id      bigint       null,
    address_id     bigint       null,
    order_name     varchar(255) null,
    order_no       varchar(255) null,
    total_discount bigint       null,
    total_price    bigint       null,
    real_price     bigint       null,
    tid            varchar(255) null,
    created_at     datetime(6)  null,
    updated_at     datetime(6)  null,
    deleted_at     datetime(6)  null
)
;

create table payment
(
    payment_id         bigint auto_increment
        primary key,
    order_id           bigint       null,
    order_no           varchar(255) null,
    payment_method     varchar(255) null,
    tid                varchar(255) null,
    card_issuer_name   varchar(255) null,
    card_purchase_name varchar(255) null,
    card_no            varchar(255) null,
    card_install_month bigint       null,
    total_amount       bigint       null,
    discount_amount    bigint       null,
    tax_free_amount    bigint       null,
    approved_at        datetime(6)  null,
    pg_token           varchar(255) null,
    created_at         datetime(6)  null,
    updated_at         datetime(6)  null,
    deleted_at         datetime(6)  null
)
;

create table prod_option
(
    prod_option_id bigint auto_increment
        primary key,
    product_id     bigint      null,
    option_id      bigint      null,
    created_at     datetime(6) null,
    updated_at     datetime(6) null,
    deleted_at     datetime(6) null
)
;

create table prod_tag
(
    prod_tag_id bigint auto_increment
        primary key,
    product_id  bigint      null,
    tag_id      bigint      null,
    created_at  datetime(6) null,
    updated_at  datetime(6) null,
    deleted_at  datetime(6) null
)
;

create table product
(
    product_id    bigint auto_increment
        primary key,
    category_id   bigint       null,
    is_own        bit          null,
    name          varchar(255) null,
    price         bigint       null,
    is_subs       bit          null,
    stock         bigint       null,
    thumb_img     varchar(255) null,
    discount_rate bigint       null,
    product_no    varchar(255) null,
    created_at    datetime(6)  null,
    updated_at    datetime(6)  null,
    deleted_at    datetime(6)  null
)
;

create table product_image
(
    product_image_id bigint auto_increment
        primary key,
    product_id       bigint       null,
    image_id         bigint       null,
    img_type         varchar(255) null,
    seq_no           bigint       null,
    created_at       datetime(6)  null,
    updated_at       datetime(6)  null,
    deleted_at       datetime(6)  null
)
;

create table review
(
    review_id       bigint auto_increment
        primary key,
    member_id       bigint      null,
    product_id      bigint      null,
    rating          bigint      null,
    summary         text        null,
    content         text        null,
    created_at      datetime(6) null,
    updated_at      datetime(6) null,
    deleted_at      datetime(6) null
)
;

create table review_image
(
    review_image_id bigint auto_increment
        primary key,
    review_id       bigint      null,
    image_id        bigint      null,
    seq_no          bigint      null,
    description     varchar(255) null,
    created_at      datetime(6) null,
    updated_at      datetime(6) null,
    deleted_at      datetime(6) null
)
;

create table review_like
(
    review_like_id bigint auto_increment
        primary key,
    review_id       bigint      null,
    member_id       bigint      null,
    created_at      datetime(6) null,
    updated_at      datetime(6) null,
    deleted_at      datetime(6) null
)
;

create table review_survey
(
    review_survey_id          bigint auto_increment
        primary key,
    review_id                 bigint      null,
    survey_category_id bigint      null,
    survey_content_id         bigint      null,
    created_at                datetime(6) null,
    updated_at                datetime(6) null,
    deleted_at                datetime(6) null
)
;

create table survey_category
(
    survey_category_id bigint auto_increment
        primary key,
    product_id                bigint       null,
    name                      varchar(255) null,
    created_at                datetime(6)  null,
    updated_at                datetime(6)  null,
    deleted_at                datetime(6)  null
)
;

create table status_code
(
    status_code_id bigint auto_increment
        primary key,
    type           varchar(255) null,
    code           varchar(255) null,
    name           varchar(255) null,
    created_at     datetime(6)  null,
    updated_at     datetime(6)  null,
    deleted_at     datetime(6)  null
)
;

create table survey_content
(
    survey_content_id  bigint auto_increment
        primary key,
    survey_category_id bigint       null,
    seq_no             int          null,
    content            varchar(255) null,
    created_at         datetime(6)  null,
    updated_at         datetime(6)  null,
    deleted_at         datetime(6)  null
)
;

create table tag
(
    tag_id          bigint auto_increment
        primary key,
    tag_category_id bigint       null,
    name            varchar(255) null,
    created_at      datetime(6)  null,
    updated_at      datetime(6)  null,
    deleted_at      datetime(6)  null
)
;

create table tag_category
(
    tag_category_id bigint auto_increment
        primary key,
    name            varchar(255) null,
    created_at      datetime(6)  null,
    updated_at      datetime(6)  null,
    deleted_at      datetime(6)  null
)
;

