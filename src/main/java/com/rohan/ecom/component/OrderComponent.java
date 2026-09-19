package com.openecom.ecom.component;

import com.openecom.ecom.dto.OrderRequestDTO;
import com.openecom.ecom.exceptions.OpenEcomException;
import com.openecom.ecom.exceptions.ProductQuantityExceededException;
import com.openecom.ecom.repository.QuantityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
public class OrderComponent {

    @Autowired
    private QuantityRepository quantityRepository;

    @Transactional
    public void reserveProductQuantities(Long id, Integer quantity) {
        int rowsUpdated = quantityRepository.reserveProductQuantity(id, quantity);

        if(rowsUpdated == 0) {
            log.info("Quantity Exception product id: {}", id);
            throw new ProductQuantityExceededException("Quantity exceeded");
        }

        log.info("Item quantity reserved for product id: {}", id);
    }

    @Transactional
    public void confirmProductQuantity(List<OrderRequestDTO.InnerOrderRequestDTO> orderRequestDTOS) {
        for(OrderRequestDTO.InnerOrderRequestDTO requests : orderRequestDTOS) {
            int rowsUpdated = quantityRepository.confirmProductQuantity(requests.getProductId(), requests.getProductQuantity());

            if(rowsUpdated == 0) {
                log.info("Exception product id while confirming quantity for product id: {}", requests.getProductId());
                throw new OpenEcomException("Exception occurred while confirming ordered quantity");
            }

            log.info("Item quantity confirmed for product id: {}", requests.getProductId());
        }
    }

    @Transactional
    public void releaseProductQuantity(List<OrderRequestDTO.InnerOrderRequestDTO> orderRequestDTOS) {
        for(OrderRequestDTO.InnerOrderRequestDTO requests : orderRequestDTOS) {
            quantityRepository.releaseProductQuantity(requests.getProductId(), requests.getProductQuantity());
            log.info("Item quantity released for product id: {}", requests.getProductId());
        }
    }

    @Transactional
    public void rollbackQuantity(List<OrderRequestDTO.InnerOrderRequestDTO> requests) {
        for(OrderRequestDTO.InnerOrderRequestDTO request : requests) {
            quantityRepository.rollbackQuantity(request.getProductId(), request.getProductQuantity());
            log.info("Item Quantity released for product id: {}", request.getProductId());
        }
    }
}
