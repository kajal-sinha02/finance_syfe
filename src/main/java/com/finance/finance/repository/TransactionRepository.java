package com.finance.finance.repository;

import com.finance.finance.entity.Transaction;
import com.finance.finance.entity.User;
import com.finance.finance.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);
    List<Transaction> findByUser(User user);
     @Query("SELECT COUNT(t) > 0 FROM Transaction t WHERE t.category.id = :categoryId")
boolean existsByCategoryId(@Param("categoryId") Long categoryId);
@Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user = :user AND t.date >= :startDate AND UPPER(t.category.type) = :type")
BigDecimal sumByUserAndDateAfter(@Param("user") User user,
                                  @Param("startDate") LocalDate startDate,
                                  @Param("type") String type);
                                  
@Query("SELECT t.category.name, SUM(t.amount) FROM Transaction t " +
       "WHERE t.user = :user AND t.date BETWEEN :start AND :end AND UPPER(t.category.type) = UPPER(:type) " +
       "GROUP BY t.category.name")
List<Object[]> sumAmountByCategoryAndType(
        @Param("user") User user,
        @Param("start") LocalDate start,
        @Param("end") LocalDate end,
        @Param("type") String type);
    
}
