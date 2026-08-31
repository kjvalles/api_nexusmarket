package application.domain.models;

import application.domain.valueobjects.CartItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ShoppingCart {

    private String id;
    private String buyerId;
    private List<CartItem> items;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

}
