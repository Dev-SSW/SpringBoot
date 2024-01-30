package Rejpabook.Rejpashop.Exception;
//예외를 직접 만들어 준다
public class NotEnoughStockException extends RuntimeException {

    public NotEnoughStockException(){};
    public NotEnoughStockException(String message) { super(message); }
    public NotEnoughStockException(String message, Throwable cause) { super(message,cause); }
    public NotEnoughStockException(Throwable cause) { super(cause); }
}
