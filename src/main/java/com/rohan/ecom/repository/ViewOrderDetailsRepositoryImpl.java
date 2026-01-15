package com.rohan.ecom.repository;

import com.rohan.ecom.dto.OrderNativeSqlResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ViewOrderDetailsRepositoryImpl implements ViewOrderDetailsRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<OrderNativeSqlResponseDTO> fetchOrders(String userEmail, LocalDate orderDate, String orderCode) {
        Map<String, Object> params = new HashMap<>();

        StringBuilder sqlBuilder = new StringBuilder();
        buildSelectClause(sqlBuilder);

        sqlBuilder.append("""
                    FROM order_details order_d
                    JOIN product_details product on product.product_id = order_d.product_id
                    JOIN user_details user_d on user_d.user_id = order_d.user_id
                """).append("\n");

        sqlBuilder.append("""
                    WHERE user_d.USER_EMAIL = :email
                """).append("\n");
        params.put("email", userEmail);

        if(orderDate != null) {
            sqlBuilder.append("""
                        AND order_d.ORDER_DATE = :orderDate
                    """).append("\n");
            params.put("orderDate", orderDate);
        }

        if(StringUtils.hasLength(orderCode)) {
            sqlBuilder.append("""
                        AND order_d.ORDER_CODE = :orderCode
                    """).append("\n");
            params.put("orderCode", orderCode);
        }

        sqlBuilder.append("""
                    LIMIT 100;
                """).append("\n");

        RowMapper<OrderNativeSqlResponseDTO> rowMapper = (resultSet, rowNum) -> {
            OrderNativeSqlResponseDTO responseDTO = new OrderNativeSqlResponseDTO();
            responseDTO.setOrderedBy(resultSet.getString("USER_NAME"));
            responseDTO.setOrderCode(resultSet.getString("ORDER_CD"));
            responseDTO.setOrderedOn(resultSet.getDate("ORDER_DATE").toLocalDate());
            responseDTO.setOrderStatus("PENDING");
            responseDTO.setProductName(resultSet.getNString("PRODUCT_NAME"));
            responseDTO.setPrice(resultSet.getBigDecimal("PRODUCT_PRICE"));
            responseDTO.setProductCategory(resultSet.getString("PRODUCT_CATEGORY"));
            responseDTO.setProductDescription(resultSet.getString("PRODUCT_DESCRIPTION"));
            responseDTO.setQuantity(resultSet.getInt("PRODUCT_QUANTITY"));
            return responseDTO;
        };

        return jdbcTemplate.query(sqlBuilder.toString(), params, rowMapper);
    }

    protected void buildSelectClause(StringBuilder sqlBuilder) {
        sqlBuilder.append("""
                    SELECT
                        user_d.USER_NAME,
                        order_d.ORDER_DATE,
                        order_d.ORDER_CD,
                        order_d.ADDRESS,
                        product.PRODUCT_NAME,
                        product.PRODUCT_CATEGORY,
                        product.PRODUCT_DESCRIPTION,
                        order_d.PRODUCT_PRICE,
                        order_d.PRODUCT_QUANTITY
                """).append("\n");
    }
}
