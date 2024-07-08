package study.Spring_Login.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.Spring_Login.Domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByLoginId(String loginId); //로그인 ID를 갖는 객체를 존재하는지 -> 중복 검사를 위해 (존재하면 true)
    Member findByLoginId(String loginId); //로그인 ID를 갖는 객체 반환
}
