package Rejpabook.Rejpashop.Domain;

import Rejpabook.Rejpashop.Domain.Item.Item;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity@Getter@Setter
public class Category {
    @Id@GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany //Item 클래스와 매핑
    //테이블을 직접 만든다
    @JoinTable(name = "category_item", //사용할 조인 테이블의 테이블 명 설정
            joinColumns = @JoinColumn(name = "category_id"), // 현재 엔티티에서 참조할 PK
            inverseJoinColumns = @JoinColumn(name = "item_id")) // 반대 방향 엔티티에서 참조할 PK (item의 PK는 Category 클래스에 존재)
    private List<Item> items = new ArrayList<>();

    //parent와 child의 연관관계로 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    //==연관관계 메서드==//
    public void addChildCategory(Category child){
        this.child.add(child); //Category 클래스의 child를 연결
        child.setParent(this); //연결된 child의 부모를 Category 클래스의 parent로 연결
    }
}
