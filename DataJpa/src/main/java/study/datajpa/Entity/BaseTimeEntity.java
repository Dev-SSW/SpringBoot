package study.datajpa.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseTimeEntity {
    //등록 시간
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    //수정 시간
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

}
