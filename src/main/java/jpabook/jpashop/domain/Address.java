package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {

    private String city;

    private String street;

    private String zipcode;

    protected Address() { // jpa의 구현체 특성상 기본 생성자도 꼭 선언을 해야하는데 이때는 protected로 선언하면 좋다(new로 인스턴스를 생성하는 것을 방지)
    }

    public Address(String city, String street, String zipcode) { // 값타입은 값을 변경 불가능하게 설계해야한다 => @setter 대신 생성자 사용해야함
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

}
