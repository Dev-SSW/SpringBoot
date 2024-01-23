package Rejpabook.Rejpashop.Service;

import Rejpabook.Rejpashop.Domain.Item.Item;
import Rejpabook.Rejpashop.Repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) //읽기전용 트랜잭션
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository; //final 필드 생성자 자동 생성 requiredargsconstructor

    @Transactional //읽기전용이 아니기에 어노테이션 추가
    public void saveItem(Item item) {
        itemRepository.save(item); //itemRepository에서 영속화 됨
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }

    @Transactional //merge 대신 변경 감지를 활용하기 위한 코드 (ItemController에서 사용)
    public void updateItem(Long id, String name, int price, int stockQuantity)
    {
        Item item = itemRepository.findOne(id);
        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
    }
}
