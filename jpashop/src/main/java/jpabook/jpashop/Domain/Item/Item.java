package jpabook.jpashop.Domain.Item;

import jpabook.jpashop.Domain.Category;
import jpabook.jpashop.Exception.NotEnoughStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity@Getter@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) //이 테이블에 다 때려 박겠다 (앨범, 북, 무비 등을)
public class Item {
    @Id@GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items") //Item의 PK는 Category 클래스에 존재하기에
    private List<Category> categories = new ArrayList<>();

    //==비즈니스 로직==//
    public void addStock(int Quantity) { //수량이 늘어나면 Quantity의 수로 올려줌
        this.stockQuantity += Quantity;
    }

    public void removeStock(int Quantity) { //수량이 줄어들면 Quantity의 수를 빼줌
        int restStock = this.stockQuantity - Quantity;
        if(restStock < 0) { //남은 수량이 0 미만이라면 더 많은 수량이 필요하다는 예외로 보낸다
            throw new NotEnoughStockException("need more stock"); //직접 만든 예외로 던진다
        }
        this.stockQuantity = restStock; //남은 수량을 현재 수량에 넣어준다
    }
}
