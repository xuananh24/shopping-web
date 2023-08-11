package com.example.cartservice.service.impl;

import com.example.cartservice.common.Constant.ApiKeyValue;
import com.example.cartservice.common.Constant.PathConstant;
import com.example.cartservice.model.entity.Cart;
import com.example.cartservice.model.entity.Item;
import com.example.cartservice.model.entity.User;
import com.example.cartservice.model.mapper.CartMapper;
import com.example.cartservice.model.mapper.ItemMapper;
import com.example.cartservice.model.request.ItemInfoRequest;
import com.example.cartservice.model.response.CartInfoResponse;
import com.example.cartservice.model.response.Product;
import com.example.cartservice.repository.CartRepository;
import com.example.cartservice.repository.ItemRepository;
import com.example.cartservice.service.CartService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final WebClient webClient;

    public CartServiceImpl(CartRepository cartRepository, ItemRepository itemRepository, WebClient webClient) {
        this.cartRepository = cartRepository;
        this.itemRepository = itemRepository;
        this.webClient = webClient;
    }

    @Override
    public void addItem(Long id, ItemInfoRequest itemInfoRequest) {
        Cart cart = cartRepository.findById(id).orElse(null);
        Item item = ItemMapper.toEntity(itemInfoRequest);
        if (cart != null) {
            item.setCart(cart);
            itemRepository.save(item);
        }
    }

    @Override
    public void removeItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public void removeCart(Long id) {
        cartRepository.deleteById(id);
    }


    @KafkaListener(topics = "${user-service-register.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void kafkaUserRegisterTopicConsumer(User user) {
        cartRepository.save(Cart.builder().userId(user.getId()).build());
    }

    @Override
    public CartInfoResponse showCartUser(Long userId) {
        CartInfoResponse cartInfoResponse = cartRepository.findCartByUserId(userId).map(CartMapper::toDto).get();
        cartInfoResponse.getItems().forEach(o -> {
            Product product = webClient.get()
                    .uri(PathConstant.API_PRODUCT_INTERNAL_SHOW_URL + o.getProductId())
                    .header("API-Key", ApiKeyValue.PRODUCT_SERVICE_KEY)
                    .retrieve()
                    .bodyToMono(Product.class)
                    .block();
            o.setProductName(product.getName());
            o.setProductDescription(product.getDescription());
        });
        return cartInfoResponse;
    }
}
