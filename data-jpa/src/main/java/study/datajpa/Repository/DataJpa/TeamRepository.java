package study.datajpa.Repository.DataJpa;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.Entity.Team;

//리포지토리 어노테이션 생략 가능
public interface TeamRepository extends JpaRepository<Team, Long> { //Entity, PK로 매핑된 타입

}
