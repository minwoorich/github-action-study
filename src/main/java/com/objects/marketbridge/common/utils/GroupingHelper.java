package com.objects.marketbridge.common.utils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* @package : com.objects.marketbridge.global.utils
* @name : GroupingHelper
* @date : 2024-01-12 오전 1:36
* @auothor : minwo
* @description : List 타입의 엔티티들을 id, name 과 같이 특정 컬럼으로
 *              묶고 싶을 때 사용합니다. (DB의 GROUP BY 와 비슷)
 *              만일 key가 중복된다면 마지막 값이 key가 됩니다.
 *
 *              ※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※
 *              ※※※ key로 사용할 필드는 반드시 getter 가 있어야합니다!! ※※※
 *              ※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※
 *
 *              사용 예시)
 *              class Product{
 *                     private Long id;
 *                     private String name;
 *                     private Integer price;
 *              }
 *
 *              Map<Long, Product> map =
 *                  GroupingHelper.groupingByKey(products, Product::getId);
 *
 *              Map<String, Product> map =
 *                 GroupingHelper.groupingByKey(products, Product::getName);
 *
 *
**/
public interface GroupingHelper  {
    static <K, V> Map <K, V> groupingByKey(List<V> list, Function<V, K> keyExtractor) {
        return list.stream()
                .collect(Collectors.toMap(
                        keyExtractor,
                        Function.identity(),
                        (existing, replacement) -> replacement)
                );
    }
}
