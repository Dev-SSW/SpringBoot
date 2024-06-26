package study.ORM.Entity.Item;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import study.ORM.Entity.Order.OrderItem;
import study.ORM.Exception.NotEnoughStockException;

import java.util.List;

@Entity @Getter @Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) //관련 엔티티를 이 테이블에 모두 넣겠다 (앨범, 무비,북 등)
public class Item {
    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    @OneToMany(mappedBy = "item")
    private List<OrderItem> orderItems;

    private String name;
    private int price;
    private int stockQuantity;

    //==비즈니스 로직==//
    public void addStock(int Quantity) { this.stockQuantity += Quantity;}

    public void removeStock(int Quantity) {
        int restStock = this.stockQuantity - Quantity; //남은 수량
        if(restStock < 0){
            throw new NotEnoughStockException("need more stock"); //직접 만든 예외
        }
        this.stockQuantity = restStock; //남은 수량으로 최신화
    }
}
