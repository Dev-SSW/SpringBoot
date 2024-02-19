package study.querydsl.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

//queryDSL 시험용
@Entity
@Getter@Setter
public class Hello {
    @Id @GeneratedValue
    private Long id;
}
