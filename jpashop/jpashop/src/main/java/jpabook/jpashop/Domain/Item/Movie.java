package jpabook.jpashop.Domain.Item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity@Getter@Setter
@DiscriminatorValue("M") //Item에 종속적
public class Movie extends Item {
    private String director;
    private String actor;
}
