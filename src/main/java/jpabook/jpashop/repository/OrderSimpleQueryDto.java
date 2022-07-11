package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderSimpleQueryDto { // OrderSimpleApiController의 orderV4를 위해 만든 DTO이다.

    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;

    public OrderSimpleQueryDto(Long orderId, String name, LocalDateTime localDateTime, OrderStatus orderStatus, Address address) { // 생성자
        this.orderId = orderId;
        this.name = name;
        this.orderDate = localDateTime;
        this.orderStatus = orderStatus;
        this.address = address;
    }

}
