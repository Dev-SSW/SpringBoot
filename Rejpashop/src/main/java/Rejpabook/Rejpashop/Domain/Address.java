package Rejpabook.Rejpashop.Domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable @Getter //Setter를 제거해서 변경 불가능하게 설계해야 한다
public class Address {
    private String city;
    private String street;
    private String zipcode;

    protected Address() {} //생성장에서 값을 모두 초기화하여 변경 불가능한 클래스를 만든다
    //JPA 스펙상 엔티티나 임베디드 타입은 protected로 설정해야 한다 (자바 기본 생성자를)
    //JPA의 이런 규칙은 JPA 구현 라이브러리가 객체를 생성할 때 리플렉션과 같은 기술을 사용할 수 있도록 지원해야하기 때문이다
    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
