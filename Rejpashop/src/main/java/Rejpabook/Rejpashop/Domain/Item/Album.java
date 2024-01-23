package Rejpabook.Rejpashop.Domain.Item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity@Getter@Setter
@DiscriminatorValue("A") //Item에 종속적
public class Album extends Item{
    private String artist;
    private String etc;
}
