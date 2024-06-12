package com.objects.marketbridge.common.utils;


import com.objects.marketbridge.common.exception.exceptions.CannotConvertNestedStructureException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

// 계층형 구조로 바꿔주는 helper
public class NestedConvertHelper<K, E, D> {

    private List<E> entities;
    private Function<E, D> toDto;
    private Function<E, E> getParent;
    private Function<E, K> getKey;    // id 값
    private Function<D, List<D>> getChildren;


    public static <K, E, D> NestedConvertHelper newInstance(List<E> entities, Function<E, D> toDto, Function<E, E> getParent, Function<E, K> getKey, Function<D, List<D>> getChildren) {
        return new NestedConvertHelper<K, E, D>(entities, toDto, getParent, getKey, getChildren);
    }

    private NestedConvertHelper(List<E> entities, Function<E, D> toDto, Function<E, E> getParent, Function<E, K> getKey, Function<D, List<D>> getChildren) {
        this.entities = entities;
        this.toDto = toDto;
        this.getParent = getParent;
        this.getKey = getKey;
        this.getChildren = getChildren;
    }

    public List<D> convert() {
        try {
            return convertInternal();
        } catch (NullPointerException e) {
            throw new CannotConvertNestedStructureException(e.getMessage());
        }
    }

    private List<D> convertInternal() {
        Map<K, D> map = new HashMap<>();
        List<D> roots = new ArrayList<>();

        for (E e : entities) {
            // entity Dto 변환
            D dto = toDto(e);
            map.put(getKey(e), dto);
            if (hasParent(e)) {
                E parent = getParent(e);
                K parentKey = getKey(parent);
                D parentDto = map.get(parentKey);
                // 부모 DTO의 children 값에 지금 dto 추가해줌
                getChildren(parentDto).add(dto);
            } else {
                // 부모가 없으면 root 엔티티
                roots.add(dto);
            }
        }
        return roots;
    }

    private boolean hasParent(E e) {
        // 부모가 null이 아니거나 null이 아닌 조건 중에 부모 id랑 자신의 id가 다르면 부모가 있는거임
        AtomicBoolean b = new AtomicBoolean(false);
        if (getParent(e) != null && (getKey(getParent(e)) != getKey(e))) b.set(true);
        else {
            b.set(false);
        }
        return b.get();
    }

    private E getParent(E e) {
        return getParent.apply(e);
    }

    private D toDto(E e) {
        return toDto.apply(e);
    }

    private K getKey(E e) {
        return getKey.apply(e);
    }

    private List<D> getChildren(D d) {
        return getChildren.apply(d);
    }
}
