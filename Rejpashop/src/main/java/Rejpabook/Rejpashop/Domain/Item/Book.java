package Rejpabook.Rejpashop.Domain.Item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity@Getter@Setter
@DiscriminatorValue("B") //Item에 종속적
public class Book extends Item{
    private String author;
    private String isbn;
}
