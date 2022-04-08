package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // 테이블 명 지정
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // OneToOne, ManyToOne은 디폴트가 즉시로딩(EAGER)이기 때문에 지연로딩(LAZY)로 바꿔준다.
    @JoinColumn(name = "member_id") // 연관관계의 주인, 외래키 관리
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // cascade를 사용함으로써 Order 엔티티를 persist하면 orderItems들도 저절로 persist됨(모든 엔티티는 각각 persist 해야하는데 이걸 사용하므로 안그래도 된다)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; // 주문 시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // enum 타입, 주문 상태 [ORDER, CANCEL]

    //== 연관관계 편의 메서드==// (양방향 연관관계인 경우만)
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

}
