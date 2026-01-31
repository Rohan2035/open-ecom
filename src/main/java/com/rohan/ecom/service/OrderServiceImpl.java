package com.rohan.ecom.service;

import com.rohan.ecom.dto.OrderNativeSqlResponseDTO;
import com.rohan.ecom.dto.OrderProductResponseDTO;
import com.rohan.ecom.dto.OrderRequestDTO;
import com.rohan.ecom.dto.OrderResponseDTO;
import com.rohan.ecom.dto.ViewOrderRequestDTO;
import com.rohan.ecom.dto.ViewOrderResponseDTO;
import com.rohan.ecom.dto.compositekey.OrderKey;
import com.rohan.ecom.entity.Order;
import com.rohan.ecom.entity.Product;
import com.rohan.ecom.entity.User;
import com.rohan.ecom.exceptions.OpenEcomException;
import com.rohan.ecom.exceptions.ProductQuantityExceededException;
import com.rohan.ecom.exceptions.UserDetailsNotFoundException;
import com.rohan.ecom.repository.OrderRepository;
import com.rohan.ecom.repository.ProductRepository;
import com.rohan.ecom.repository.UserDetailsRepository;
import com.rohan.ecom.repository.ViewOrderDetailsRepository;
import com.rohan.ecom.util.Codes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderServiceImpl.class);
    private static final String USER_NOT_FOUND_MSG = "User Details not found for email: ";
    private static final String PRODUCT_QUANTITY_MSG = "Product Quantity Exceeded for product: ";
    private static final String PRODUCT_NOT_FOUND = "Product not found: ";
    private static final String ORDER_NOT_FOUND = "Sorry, No Orders Found!";
    private static final String EMPTY_STRING = "";
    private static final String SUCCESS = "SUCCESS";
    private static final String FAIL = "FAIL";

    private final OrderRepository orderRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final ProductRepository productRepository;
    private final ViewOrderDetailsRepository viewOrderDetailsRepository;
    private final PaymentService paymentService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, UserDetailsRepository userDetailsRepository,
                            ProductRepository productRepository, ViewOrderDetailsRepository viewOrderDetailsRepository,
                            PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.productRepository = productRepository;
        this.viewOrderDetailsRepository = viewOrderDetailsRepository;
        this.paymentService = paymentService;
    }

    @Override
    @Transactional
    public String addOrder(OrderRequestDTO orderRequestDTO) {
        // Fetch User
        User user = fetchUser(orderRequestDTO.getUserEmail());

        LOG.info("Creating Product List");
        Set<String> productNames = orderRequestDTO.getOrderRequests().stream()
                .map(OrderRequestDTO.InnerOrderRequestDTO::getProductName)
                .collect(Collectors.toSet());

        // Fetch the product map
        Map<String, Product> productMap = fetchProductMap(productNames);

        LOG.info("Generating Order Code");
        String orderCode = generateOrderCode(user.getUserName());

        List<Order> orders = new ArrayList<>();
        for(OrderRequestDTO.InnerOrderRequestDTO orderRequests : orderRequestDTO.getOrderRequests()) {
            Product product = productMap.get(orderRequests.getProductName());

            LOG.info("Reserve Product Quantities");
            reserveProductQuantities(product.getProductId(), orderRequests.getProductQuantity(), product.getProductName());

            LOG.info("Mapping Orders");
            Order order = this.mapOrder(orderRequests, user, product.getProductId(), orderCode, orderRequestDTO.getAddress());
            orders.add(order);
        }

        // Payment Gateway Logic
        LOG.info("Initiating Payment");
        String response = paymentService.makePayment();

        if(response.equals(SUCCESS)) {
            try {
                LOG.info("Confirming Order");
                confirmOrder(productMap, orderRequestDTO.getOrderRequests());
            } catch(Exception e) {
                response = FAIL;
                releaseReservedQuantities(productMap, orderRequestDTO.getOrderRequests());
                // Refund Payment
                paymentService.refundPayment();
                return response;
            }

            try {
                LOG.info("Saving Orders");
                orderRepository.saveAll(orders);
            } catch(Exception e) {
                response = FAIL;
                releaseReservedQuantities(productMap, orderRequestDTO.getOrderRequests());
                // refund payment
                paymentService.refundPayment();
                return response;
            }
        } else {
            releaseReservedQuantities(productMap, orderRequestDTO.getOrderRequests());
            response = FAIL;
        }

        return response;
    }

    @Override
    public ViewOrderResponseDTO viewOrder(ViewOrderRequestDTO orderRequestDTO) {
        ViewOrderResponseDTO responseDTO;

        LOG.info("Fetching Orders");
        long startTime = System.currentTimeMillis();
        List<OrderNativeSqlResponseDTO> sqlResponse = viewOrderDetailsRepository.fetchOrders(
                orderRequestDTO.getUserEmail(),
                orderRequestDTO.getOrderDate(),
                orderRequestDTO.getOrderCode()
        );
        long endTime = System.currentTimeMillis();
        LOG.info("Order Fetched in {} ms", (endTime - startTime));

        if(CollectionUtils.isEmpty(sqlResponse)) {
            LOG.info("Order is Empty");
            responseDTO = new ViewOrderResponseDTO();
            responseDTO.setStatus(Codes.FAIL.getCode());
            responseDTO.setStatusMessage(ORDER_NOT_FOUND);
            responseDTO.setOrders(List.of());
            responseDTO.setOrderedBy(EMPTY_STRING);
            return responseDTO;
        }

        responseDTO = this.mapViewOrderDTO(sqlResponse);
        responseDTO.setStatus(Codes.SUCCESS.getCode());
        responseDTO.setStatusMessage("Successfully Fetched " + responseDTO.getOrders().size() + " Orders");
        return responseDTO;
    }

    protected Order mapOrder(OrderRequestDTO.InnerOrderRequestDTO orderRequestDTO, User user,
            Integer productId, String orderCode, String address) {

        Order order = new Order();
        order.setUser(user);
        order.setOrderCode(orderCode);
        order.setOrderAddress(address);
        order.setProductId(productId);
        order.setProductQuantity(orderRequestDTO.getProductQuantity());
        order.setProductPrice(orderRequestDTO.getProductPrice().multiply(BigDecimal.valueOf(orderRequestDTO.getProductQuantity())));
        order.setOrderDate(LocalDate.now());
        order.setOrderTime(LocalDateTime.now());
        return order;
    }

    private static String generateOrderCode(String username) {
        String randomCode = UUID.randomUUID().toString()
                .substring(0, 3)
                .replace("_", "");

        String localDate = LocalDate.now().toString().replace("-", "");

        if(username.length() > 5) {
            username = username.substring(0, 3);
        }

        return username.toLowerCase() + localDate + randomCode;
    }

    protected ViewOrderResponseDTO mapViewOrderDTO(List<OrderNativeSqlResponseDTO> sqlResponse) {
        long startTime = System.currentTimeMillis();
        ViewOrderResponseDTO responseDTO = new ViewOrderResponseDTO();
        if(sqlResponse == null || sqlResponse.isEmpty()) {
            return null;
        }

        responseDTO.setOrderedBy(sqlResponse.get(0).getOrderedBy());
        Map<OrderKey, List<OrderNativeSqlResponseDTO>> orderMap = sqlResponse.stream()
                .collect(Collectors.groupingBy(order -> new OrderKey(
                        order.getOrderCode(), order.getOrderedOn())));

        List<OrderResponseDTO> orderResponseList = new ArrayList<>();
        for(OrderKey orderKey : orderMap.keySet()) {
            OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
            orderResponseDTO.setOrderCode(orderKey.orderCode());
            orderResponseDTO.setOrderDate(orderKey.orderDate());

            mapAndSetOrderProductResponseDTO(orderMap.get(orderKey), orderResponseDTO);

            orderResponseList.add(orderResponseDTO);
        }

        responseDTO.setOrders(orderResponseList);
        long endTime = System.currentTimeMillis();
        LOG.info("Time taken to map the orders: {} ms", (endTime - startTime));
        return responseDTO;
    }

    private void mapAndSetOrderProductResponseDTO(List<OrderNativeSqlResponseDTO> sqlResponse, OrderResponseDTO orderResponseDTO) {
        List<OrderProductResponseDTO> productList = new ArrayList<>();
        for(OrderNativeSqlResponseDTO product : sqlResponse) {
            OrderProductResponseDTO responseDTO = new OrderProductResponseDTO();
            responseDTO.setProductName(product.getProductName());
            responseDTO.setProductPrice(product.getPrice().toString());
            responseDTO.setProductQuantity(product.getQuantity());
            responseDTO.setProductCategory(product.getProductCategory());
            responseDTO.setProductDescription(product.getProductDescription());
            productList.add(responseDTO);
        }
        orderResponseDTO.setProducts(productList);
    }

    protected User fetchUser(String userEmail) {
        LOG.info("Fetching User");
        long startTime = System.currentTimeMillis();
        User user = userDetailsRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserDetailsNotFoundException(USER_NOT_FOUND_MSG +
                        userEmail));
        long endTime = System.currentTimeMillis();
        LOG.info("User Fetched in {} ms", (endTime - startTime));

        return user;
    }

    protected Map<String, Product> fetchProductMap(Set<String> productNames) {
        LOG.info("Fetching Products");
        long startTime = System.currentTimeMillis();
        Map<String, Product> productMap = productRepository.findByProductNameIn(productNames)
                .orElseThrow(() -> new OpenEcomException(PRODUCT_NOT_FOUND))
                .stream()
                .collect(Collectors.toMap(Product::getProductName, Function.identity()));
        long endTime = System.currentTimeMillis();
        LOG.info("Products Fetched in {} ms", (endTime - startTime));

        return productMap;
    }

    protected void reserveProductQuantities(Integer id, Integer quantity, String productName) {
        int checkUpdates = productRepository.reserveProductQuantity(id, quantity);
        if(checkUpdates == 0) {
            throw new ProductQuantityExceededException(PRODUCT_QUANTITY_MSG + productName);
        }
    }

    protected void confirmOrder(Map<String, Product> productMap, List<OrderRequestDTO.InnerOrderRequestDTO> requestDTOList) {
        for(OrderRequestDTO.InnerOrderRequestDTO requestDTO : requestDTOList) {
            Product product = productMap.get(requestDTO.getProductName());

            int checkUpdates = productRepository.confirmOrder(product.getProductId(),
                    requestDTO.getProductQuantity());

            if(checkUpdates == 0) {
                LOG.error("Product with name: {} and id: {} encountered error while confirming the order",
                        product.getProductName(), product.getProductId());
                throw new OpenEcomException("Error occurred while confirming order");
            }
        }
    }

    protected void releaseReservedQuantities(Map<String, Product> productMap, List<OrderRequestDTO.InnerOrderRequestDTO> requestDTOList) {
        for(OrderRequestDTO.InnerOrderRequestDTO requestDTO : requestDTOList) {
            Product product = productMap.get(requestDTO.getProductName());

            int checkUpdates = productRepository.releaseReservedQuantities(product.getProductId(),
                    requestDTO.getProductQuantity());

            if(checkUpdates == 0) {
                LOG.error("Product with name: {} and id: {} encountered error while releasing reserved quantities",
                        product.getProductName(), product.getProductId());
                throw new OpenEcomException("Error occurred while confirming order");
            }
        }
    }
}
