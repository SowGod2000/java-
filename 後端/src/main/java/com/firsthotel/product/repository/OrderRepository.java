package com.firsthotel.product.repository;

import com.firsthotel.member.bean.Member;
import com.firsthotel.product.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Order findByMerchantTradeNo(String tradeNo);

    List<Order> findByCustomerNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String customerName, String email);

    List<Order> findByPaymentStatusAndOrderTimeBefore(String status, LocalDateTime time);
    
   

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.paymentStatus = 'PAID' AND o.orderTime BETWEEN :start AND :end")
    BigDecimal sumTotalAmountByDate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.paymentStatus = 'PAID' AND YEAR(o.orderTime) = :year AND MONTH(o.orderTime) = :month")
    Long countPaidOrdersInMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.paymentStatus = 'PAID'")
    BigDecimal sumTotalRevenue();

    @Query("SELECT NEW map(FORMAT(o.orderTime, 'yyyy-MM') as month, SUM(o.totalAmount) as total) " +
           "FROM Order o WHERE o.paymentStatus = 'PAID' GROUP BY FORMAT(o.orderTime, 'yyyy-MM') ORDER BY month")
    List<Map<String, Object>> getMonthlySales();

    // ✅ 商品分類銷售分佈（圓餅圖用）- 已修正 p.pid
    @Query(value = """
    	    SELECT c.name AS category, SUM(oi.price * oi.quantity) AS total
    	    FROM dbo.orders o
    	    JOIN dbo.order_items oi ON o.id = oi.order_id
    	    JOIN dbo.products p ON oi.productId = p.pid
    	    JOIN dbo.categories c ON p.category_id = c.id
    	    WHERE o.paymentStatus = 'PAID'
    	    GROUP BY c.name
    	    """, nativeQuery = true)
    	List<Map<String, Object>> getCategorySales();
    
    List<Order> findByEmail(String email);
    
    List<Order> findByMemberOrderByOrderTimeDesc(Member member);
    
    @Query(value = """
    	    SELECT t.name AS tag, SUM(oi.price * oi.quantity) AS total
    	    FROM dbo.orders o
    	    JOIN dbo.order_items oi ON o.id = oi.order_id
    	    JOIN dbo.products p ON oi.productId = p.pid
    	    JOIN dbo.tags t ON ',' + p.ptags + ',' LIKE '%,' + t.name + ',%'
    	    WHERE o.paymentStatus = 'PAID'
    	    GROUP BY t.name
    	""", nativeQuery = true)
    	List<Map<String, Object>> getTagSales();



}
