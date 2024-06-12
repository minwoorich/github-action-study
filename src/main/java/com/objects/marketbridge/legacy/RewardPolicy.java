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
//public class RewardPolicy extends BaseEntity{
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "reward_id")
//    private Long id;
//
//    private String type;
//    private Long rate;
//
//    @Builder
//    public RewardPolicy(String type, Long rate) {
//        this.type = type;
//        this.rate = rate;
//    }
//}
