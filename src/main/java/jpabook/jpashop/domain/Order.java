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
    @JoinColumn(name = "member_id") // 다쪽이 연관관계의 주인, 외래키 관리
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // cascade를 사용함으로써 Order 엔티티를 persist하면 orderItems들도 저절로 persist됨(모든 엔티티는 각각 persist 해야하는데 이걸 사용하므로 안그래도 된다)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // cascade 때문에 order가 persist되면 delivery도 persist된다.
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

    //== 생성 메서드 ==// (엔티티에 비즈니스 로직을 몰아 넣는 것을 도메인 모델 패턴이라고 한다. - 반대는 트랜잭션 스크립트 패턴이라고 함)
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) { // ...은 여러개를 넘긴다는 의미이다.
        Order order = new Order();

        order.setMember(member);
        order.setDelivery(delivery);

        for(OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());

        return order;
    }

    //== 비즈니스 로직 ==//
    public void cancel() { // 주문 취소
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송 완료된 상품은 취소가 불가능 합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);

        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    //== 조회 로직 ==//
    public int getTotalPrice() { // 전체 주문 가격 조회
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }

        return totalPrice;
    }



}
