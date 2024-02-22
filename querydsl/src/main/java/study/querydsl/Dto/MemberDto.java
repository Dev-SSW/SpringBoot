package study.querydsl.Dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberDto {
    private String username;
    private int age;

    @QueryProjection //프로젝션 결과 반환, DTO도 Q 파일로 생성된다, 컴파일JAVA 해주어야함
    public MemberDto(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
