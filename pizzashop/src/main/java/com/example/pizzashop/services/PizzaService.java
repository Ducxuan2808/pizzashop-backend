package com.example.pizzashop.services;

import com.example.pizzashop.dtos.PizzaDTO;
import com.example.pizzashop.dtos.PizzaImageDTO;
import com.example.pizzashop.exceptions.DataNotFoundException;
import com.example.pizzashop.exceptions.InvalidParamException;
import com.example.pizzashop.kafka.KafkaEventPublisher;
import com.example.pizzashop.kafka.KafkaTopics;
import com.example.pizzashop.kafka.events.PizzaEvent;
import com.example.pizzashop.models.Pizza;
import com.example.pizzashop.models.PizzaImage;
import com.example.pizzashop.repositories.OrderDetailRepository;
import com.example.pizzashop.repositories.PizzaImageRepository;
import com.example.pizzashop.repositories.PizzaRepository;
import com.example.pizzashop.responses.PizzaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PizzaService implements IPizzaService{
    private final PizzaRepository pizzaRepository;

    private final PizzaImageRepository pizzaImageRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final KafkaEventPublisher kafkaEventPublisher;
    @Override
    @Transactional
    public Pizza createPizza(PizzaDTO pizzaDTO) throws Exception {
        Pizza newPizza = Pizza.builder()
                .name(pizzaDTO.getName())
                .title(pizzaDTO.getTitle())
                .basePrice(pizzaDTO.getBasePrice())
                .thumbnail(pizzaDTO.getThumbnail())
                .description(pizzaDTO.getDescription())
                .active(true)
                .build();
        Pizza savedPizza = pizzaRepository.save(newPizza);
        kafkaEventPublisher.publish(KafkaTopics.PIZZA_EVENTS, savedPizza.getId(),
                PizzaEvent.of(PizzaEvent.CREATED, savedPizza));
        return savedPizza;
    }

    @Override
    public Pizza getPizzaById(long pizzaID) throws Exception {
        Optional<Pizza> optionalProduct = pizzaRepository.getDetailPizza(pizzaID);
        if(optionalProduct.isPresent()){
            return optionalProduct.get();
        }
        throw new DataNotFoundException("Cannot find product with id = "+ pizzaID);
    }

    @Override
    public Page<PizzaResponse> getAllPizzas(String sortBy, Float minPrice, Float maxPrice, String keyword, PageRequest pageRequest) {
        Page<Pizza> productsPage;
        productsPage = pizzaRepository.searchPizzas(sortBy, minPrice, maxPrice, keyword, pageRequest);
        return productsPage.map(PizzaResponse::fromPizza);
    }

    @Override
    @Transactional
    public Pizza updatePizza(long id, PizzaDTO pizzaDTO) throws Exception {
        Pizza existingPizza = getPizzaById(id);
        if(existingPizza!=null){
            existingPizza.setName(pizzaDTO.getName());
            existingPizza.setTitle(pizzaDTO.getTitle());
            existingPizza.setBasePrice(pizzaDTO.getBasePrice());
            existingPizza.setDescription(pizzaDTO.getDescription());
            existingPizza.setThumbnail(pizzaDTO.getThumbnail());
            Pizza savedPizza = pizzaRepository.save(existingPizza);
            kafkaEventPublisher.publish(KafkaTopics.PIZZA_EVENTS, savedPizza.getId(),
                    PizzaEvent.of(PizzaEvent.UPDATED, savedPizza));
            return savedPizza;
        }
        return null;
    }

    @Override
    public void deletePizza(long id) throws Exception  {
        Pizza existingPizza = getPizzaById(id);
        if(existingPizza!=null){
           existingPizza.setActive(false);
           pizzaRepository.save(existingPizza);
           kafkaEventPublisher.publish(KafkaTopics.PIZZA_EVENTS, existingPizza.getId(),
                   PizzaEvent.of(PizzaEvent.DELETED, existingPizza));
        }
    }

    @Override
    public boolean existByName(String name) {
        return pizzaRepository.existsByName(name);
    }

    @Override
    @Transactional
    public PizzaImage createPizzaImage(Long pizzaId, PizzaImageDTO pizzaImageDTO) throws Exception {
        Pizza existingPizza = pizzaRepository.findById(pizzaId)
                .orElseThrow(()->new DataNotFoundException("Cannot find product with id: "+pizzaImageDTO.getPizzaId()));
        PizzaImage newPizzaImage = PizzaImage.builder()
                .pizza(existingPizza)
                .image_url(pizzaImageDTO.getImageUrl())
                .build();
        //Khong cho insert qua 5 anh cho 1 san pham
        int size = pizzaImageRepository.findByPizzaId(pizzaId).size();
        if(size >=PizzaImage.MAXIMUM_IMAGES_PER_PRODUCT){
            throw  new InvalidParamException("Number of images must be <="
                    + PizzaImage.MAXIMUM_IMAGES_PER_PRODUCT);
        }
        return pizzaImageRepository.save(newPizzaImage);
    }

    @Override
    public List<Pizza> findPizzasByIds(List<Long> pizzaIds) {
        return pizzaRepository.findPizzasByIds(pizzaIds);
    }

    @Override
    public Page<PizzaResponse> bestSellPizzas(PageRequest pageRequest) {
        Page<Pizza> productsPage;
        productsPage = pizzaRepository.bestSellPizzas(pageRequest);
        return productsPage.map(PizzaResponse::fromPizza);
    }

    @Override
    public Page<PizzaResponse> worstSellPizzas(PageRequest pageRequest) {
        Page<Pizza> productsPage;
        productsPage = pizzaRepository.worstSellPizzas(pageRequest);
        return productsPage.map(PizzaResponse::fromPizza);
    }

    @Override
    public Integer getCountSoldByPizzaId(long pizzaId) {
        return orderDetailRepository.getCountSoldByPizzaId(pizzaId);
    }
}
