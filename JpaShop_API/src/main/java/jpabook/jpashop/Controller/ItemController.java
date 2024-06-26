package jpabook.jpashop.Controller;

import jpabook.jpashop.Controller.Form.BookForm;
import jpabook.jpashop.Domain.Item.Book;
import jpabook.jpashop.Domain.Item.Item;
import jpabook.jpashop.Service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    // items/new 주소 요청을 받으면 createItemForm.html으로 이동 ->
    // PostMapping을 통해 상품 정보를 저장 후 "/items" 페이지로 보냄 (상품 목록 페이지)
    // items 주소 요청을 받으면 itemsList.html로 이동 ->
    // 목록 확인

    //**상품 등록**//
    @GetMapping(value = "/items/new")
    public String createForm(Model model){
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping(value = "/items/new")
    public String create(BookForm form) {
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setIsbn(form.getIsbn());
        book.setAuthor(form.getAuthor());

        itemService.saveItem(book);
        return "redirect:/items";
    }

    //**상품 목록 조회**//
    @GetMapping(value = "/items")
    public String List(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList"; //모델에 담아둔 상품 목록인 items를 꺼내서 상품 정보를 출력
    }

    //**상품 수정 폼**//
    @GetMapping(value = "/items/{itemId}/edit") //수정 버튼 선택시 URL을 GET 방식으로 요청
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model){ //경로 변수를 추출하여 매개변수에 할당하는 PathVariable
        Book item = (Book) itemService.findOne(itemId); //수정할 상품을 조회해서 form에 저장
        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());
        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    //**상품 수정**//
    @PostMapping(value = "/items/{itemId}/edit") //수정 화면에서 수정 버튼 클릭시 Post 방식으로 여기로 들어옴
    public String updateItem(@PathVariable("itemId") Long itemId, @ModelAttribute("form") BookForm form) {
        //메소드 파라미터나 리턴 값을 변수로 사용하기 위해 ModelAttribute
        //merger대신 변경 감지를 사용하기 위한 코드, 본래는 ItemRepository의 save를 사용했다
        itemService.updateItem(itemId, form.getName(),form.getPrice(),form.getStockQuantity());
        return "redirect:/items";
    }
}
