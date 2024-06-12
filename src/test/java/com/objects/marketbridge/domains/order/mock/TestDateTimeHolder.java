package com.objects.marketbridge.domains.order.mock;

import com.objects.marketbridge.domains.member.domain.BaseEntity;
import com.objects.marketbridge.common.utils.DateTimeHolder;
import lombok.Builder;

import java.time.LocalDateTime;

public class TestDateTimeHolder implements DateTimeHolder {

    private LocalDateTime now;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime deleteTime;

    @Builder
    public TestDateTimeHolder(LocalDateTime now, LocalDateTime createTime, LocalDateTime updateTime, LocalDateTime deleteTime) {
        this.now = now;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.deleteTime = deleteTime;
    }

    @Override
    public LocalDateTime getTimeNow() {
        return now;
    }

    @Override
    public LocalDateTime getCreateTime(BaseEntity entity) {
        return createTime;
    }

    @Override
    public LocalDateTime getUpdateTime(BaseEntity entity) {
        return updateTime;
    }

    @Override
    public LocalDateTime getDeleteTime(BaseEntity entity) {
        return deleteTime;
    }
}
