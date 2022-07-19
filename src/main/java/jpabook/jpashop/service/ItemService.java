package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }

    public void updateItem(Long itemId, String name, int price, int stockQuantity, String author, String isbn) { // 리포지토리에 따로 save를 하지 않아도 변경감지 덕분에 @transactional의 커밋시점에 자동으로 변경된다.
        Book item = (Book) itemRepository.findOne(itemId); // new로 Book 객체를 새로 만들면 준영속 상태인데 리포지토리에서 가져오면 영속상태의 객체이다. 따라서 persist를 할필요 없이 변경감지로 변경시킬 수 있다.
        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
        item.setAuthor(author);
        item.setIsbn(isbn);
    }
}
