package com.objects.marketbridge.common.utils;

import com.objects.marketbridge.domains.member.domain.BaseEntity;

import java.time.LocalDateTime;

public interface DateTimeHolder {

    LocalDateTime getTimeNow();

    LocalDateTime getCreateTime(BaseEntity entity);

    LocalDateTime getUpdateTime(BaseEntity entity);

    LocalDateTime getDeleteTime(BaseEntity entity);
}
