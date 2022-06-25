package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 상속관계 전략 지정 (싱글테이블 사용할 것임)
@DiscriminatorColumn(name = "dtype")
public abstract class Item { // 추상 클래스로 만듦, 상속관계 매핑 할 것임

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "item_id")
    private Long id;

    private String name;

    private int price;

    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //== 비즈니스 로직 ==// 응집도를 위해 여기다 작성함
    public void addStock(int quantity) { // 재고 증가
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity) { // 재고 감소
        int restStock = this.stockQuantity - quantity;

        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }

        this.stockQuantity = restStock;
   }

}
