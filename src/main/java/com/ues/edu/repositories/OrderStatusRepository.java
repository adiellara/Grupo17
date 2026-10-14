package com.ues.edu.repositories;

import com.ues.edu.entities.Order;
import com.ues.edu.entities.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Carlos Alex
 */
@Repository
public interface OrderStatusRepository extends JpaRepository<Order, Integer> {

    Order findOrderById(int id);

//    void updateOrderStatus(int id, OrderStatus newStatus);

}
