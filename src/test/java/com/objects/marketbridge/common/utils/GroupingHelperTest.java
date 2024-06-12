package com.objects.marketbridge.common.utils;

import static org.assertj.core.api.Assertions.assertThat;

class GroupingHelperTest {

//    @DisplayName("중복되지않은 키로 그룹핑이 돼야한다")
//    @Test
//    void groupByUniqueKey(){
//        //given
//        MockEntity entity1 = MockEntity.builder()
//                .id(1L)
//                .email("test1@email.com")
//                .name("홍길동")
//                .build();
//
//        MockEntity entity2 = MockEntity.builder()
//                .id(2L)
//                .email("test2@email.com")
//                .name("박길동")
//                .build();
//
//        MockEntity entity3 = MockEntity.builder()
//                .id(3L)
//                .email("test3@email.com")
//                .name("박길동")
//                .build();
//
//        List<MockEntity> entities = List.of(entity1, entity2, entity3);
//
//        //when
//        Map<Long, MockEntity> map =
//                GroupingHelper.groupingByKey(entities, MockEntity::getId);
//
//        //then
//        assertThat(map).hasSize(3);
//        assertThat(map.get(1L)).isEqualTo(entity1);
//        assertThat(map.get(2L)).isEqualTo(entity2);
//        assertThat(map.get(3L)).isEqualTo(entity3);
//    }
//
//    @DisplayName("키가 중복될 경우 마지막에 나오는 녀석이 실제 키가 된다.")
//    @Test
//    void groupByDuplicatedKey(){
//        //given
//        MockEntity entity1 = MockEntity.builder()
//                .id(1L)
//                .email("test1@email.com")
//                .name("홍길동")
//                .build();
//
//        MockEntity entity2 = MockEntity.builder()
//                .id(2L)
//                .email("test2@email.com")
//                .name("박길동")
//                .build();
//
//        MockEntity entity3 = MockEntity.builder()
//                .id(3L)
//                .email("test3@email.com")
//                .name("박길동")
//                .build();
//
//        List<MockEntity> entities = List.of(entity1, entity2, entity3);
//
//        //when
//        Map<String, MockEntity> map = GroupingHelper.groupingByKey(entities, MockEntity::getName);
//
//        //then
//        assertThat(map).hasSize(2);
//        assertThat(map.get("홍길동")).isEqualTo(entity1);
//        assertThat(map.get("박길동")).isEqualTo(entity3);
//        assertThat(map.get("박길동")).isNotEqualTo(entity2);
//    }
}