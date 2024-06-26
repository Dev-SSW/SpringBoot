package study.datajpa.Repository.DataJpa;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.Dto.MemberDto;
import study.datajpa.Entity.Member;
import study.datajpa.Repository.MemberProjection;
import study.datajpa.Repository.UsernameOnly;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

//리포지토리 어노테이션 생략 가능
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom { //Entity, PK로 매핑된 타입

    // 예제
    // List<Member> findHelloBy();

    //메소드 이름으로 쿼리 생성하기
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    //(네임드 쿼리를 사용할 때에는 Member에 @NamedQuery을 써줘야 한다)
    //@Query(name = "Member.findByUsername") //이 내용이 없어도 네임드 쿼리로 자동 지정
    List<Member> findByUsername(@Param("username") String username);

    // 애플리케이션 로딩할 때 오류를 탐지할 수 있다 (namedQuery와 마찬가지로) //실무에서 많이 쓰는 기능 (두 개 이하일 때)
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    //리포지토리 메소드에 쿼리 정의하기 (네임드 쿼리 대신에)
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    //Dto로 값 조회하기
    @Query("select new study.datajpa.Dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    //파라미터 바인딩
    @Query("select m from Member m where m.username in :names") //:names와 같은 이름을 가진 Member를 가져옴
    List<Member> findByNames (@Param("names") Collection<String> names);

    //반환 타입
    List<Member> findListByUsername(String username); //컬렉션
    Member findMemberByUsername(String username); //단건
    Optional<Member> findOptionalByUsername(String username); //단건 Optional

    //스프링 데이터 JPA로 페이징과 정렬 //Top3로 앞에있는 세 건만 가져올 수 있도록 하는 방법이 있다
    //(주의 : page 인덱스는 0부터 시작이다)
    @Query(value = "select m from Member m left join m.team t"
            , countQuery = "select count(m) from Member m") //카운트 쿼리를 분리하는 방법 (성능이 느려지는 경우를 대비해서)
    Page<Member> findByAge(int age, Pageable pageable); //메소드 이름으로 쿼리 생성

    //벌크성 수정 쿼리
    @Modifying(clearAutomatically = true)
    //executeUpdate()가 없으면 SingleResult나 ResultList를 호출하기에 Modifying을 호출해야한다
    //clearAutomatically=true를 통해 클리어를 해줘야하는 일을 자동으로 해준다
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    //페치 조인 활용법
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    //@EntityGraph 사용
    @Override //findAll() 함수 재정의
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    //쿼리와 페치 조인 모두 사용
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    //메소드 명으로 쿼리 생성하고 페치 조인도 사용 (find...ByUsername
//    @EntityGraph(attributePaths = {"team"})
    @EntityGraph("Member.all") //네임드 엔티티 그래프 사용법 (잘 쓰진 않는다)
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    //JPA Hint (데이터를 변경하는 목적이 아니라 조회만 할 것인 엔티티에 적용하며 업데이트 했을 때 객체가 2개가 생기는 문제를 최적화
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    //Lock (DB에서 셀렉트할 때 다른 애들은 건들지 못하도록 Lock으로 막는 것)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

    //projections 사용방법 DTO를 편리하게 조회할 때 사용, 전체 엔티티가 아니라 이름만 조회하고 싶다면?
    List<UsernameOnly> findProjectionsByUsername(@Param("username") String username);

    //네이티브 쿼리
    @Query(value = "select * from member where username = ?" , nativeQuery = true)
    Member findByNativeQuery(String username);

    //네이티브 프로젝션 쿼리
    @Query(value = "select m.member_id as id, m.username, t.name as teamName " + "from member m left join team t",
    countQuery = "select count(*) from member",
    nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
