//package com.objects.marketbridge.order.domain;
//
//import com.objects.marketbridge.member.domain.BaseEntity;
//import jakarta.persistence.*;
//import lombok.AccessLevel;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class EstimatedTime extends BaseEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "estimated_time_id")
//    private Long id;
//
//    private Long hours;
//
//    private Long addDay;
//
//    @Builder
//    private EstimatedTime(Long hour, Long addDay) {
//        this.hours = hours;
//        this.addDay = addDay;
//    }
//}
