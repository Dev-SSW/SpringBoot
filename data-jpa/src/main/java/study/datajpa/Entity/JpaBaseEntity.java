package study.datajpa.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass //실제 상속 관계가 아니고 데이터만을 공유한다는 의미를 정의
public class JpaBaseEntity {

    @Column(updatable = false) //createdDate가 수정 불가능 하도록
    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    @PrePersist //persist하기 전에 실행
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    }

    @PreUpdate //update하기 전에 실행
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
