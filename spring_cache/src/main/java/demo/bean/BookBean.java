package demo.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class BookBean implements Serializable {
    private static final long serialVersionUID = -6585766340444705937L;
    private int bookId;
    private String bookName;
    private String author;
    private float price;
}
