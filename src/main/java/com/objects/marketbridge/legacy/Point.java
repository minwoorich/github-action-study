//package com.objects.marketbridge.member.domain;
//
//import com.objects.marketbridge.member.dto.FindPointDto;
//import jakarta.persistence.*;
//import lombok.AccessLevel;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class Point extends BaseEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "point_id")
//    private Long id;
//
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id")
//    private Member member;
//
//    private Long inPoint;
//
//    private Long outPoint;
//
//    private Long balance;
//
//    private String comments;
//
//    private LocalDateTime createdAt;
//
//
//    @Builder //order_id 제거
//    private Point(Member member, Long inPoint, Long outPoint, Long balance, String comments) {
//        this.member = member;
//        this.inPoint = inPoint;
//        this.outPoint = outPoint;
//        this.balance = balance;
//        this.comments = comments;
//    }
//
//    // 연관관계 편의 메서드 -> Point 쪽에서 한번에 저장
//    public void setMember(Member member) {
//        this.member = member;
//        member.changePoint(this);
//    }
//
//    public static FindPointDto toDto(Member member){
//        return FindPointDto.builder()
//                .balance(member.getPoint().getBalance())
//                .inPoint(member.getPoint().getInPoint())
//                .outPoint(member.getPoint().getOutPoint())
//                .comments(member.getPoint().getComments())
//                .createdAt(member.getPoint().getCreatedAt())
//                .build();
//    }
//}
