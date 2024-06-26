package jpabook.jpashop.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable //어딘가에 내장이 될 수 있다는 뜻
@Getter
public class Address {
    private String city;
    private String street;
    private String zipcode;

    protected Address() {
    } //생성자로 값을 모두 초기화해서 변경 불가능하도록 만든 것

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
