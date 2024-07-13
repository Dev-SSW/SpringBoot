package Study.Spring_Login_JWT.Member;

import Study.Spring_Login_JWT.Member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByLoginId(String loginId); //로그인 ID를 갖는 객체를 존재하는지 -> 중복 검사를 위해 (존재하면 true)
    Member findByLoginId(String loginId); //로그인 ID를 갖는 객체 반환

    /*
    @Query("SELECT m FROM Member m WHERE m.loginId = :loginId")
    Member FindLoginId(@Param("loginId") String loginId);
    */

}
