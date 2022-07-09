package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "member_id") // 칼럼 명 지정
    private Long id;

    private String name;

    @Embedded
    private Address address;

    @JsonIgnore // MemberApiController에서 전체 회원 조회 할때 이건 제외한다는 의미이다.
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

}
