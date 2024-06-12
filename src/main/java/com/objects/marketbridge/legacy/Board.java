//package com.objects.marketbridge.member.domain;
//
//import jakarta.persistence.*;
//import lombok.AccessLevel;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class Board extends BaseEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "board_id")
//    private Long id;
//
//    @Enumerated(EnumType.STRING)
//    private ContentType contentType;
//
//    private String content;
//
//    @Builder
//    private Board(ContentType contentType, String content) {
//        this.contentType = contentType;
//        this.content = content;
//    }
//}
