package com.ues.edu.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ues.edu.entities.Orders;
import com.ues.edu.entities.User;
import com.ues.edu.repositories.OrderRepository;

/**
 * Provee las funcionalidades para procesar Órdenes
 *
 * @author 
 */
@Service
public class OrderServices {

    @Autowired
    private OrderRepository orderRepository;

    public List<Orders> getOrders() {
        return this.orderRepository.findAll();
    }

    public Orders getOrderById(int id) {
        return this.orderRepository.findById(id).orElse(null);
    }

    public void saveOrder(Orders order) {
        if (order.getoPrice() > 0 && order.getoQuantity() > 0) {
            double total = Math.round((order.getoPrice() * order.getoQuantity()) * 100.0) / 100.0;
            order.setTotalAmmout(total);
        }
        this.orderRepository.save(order);
    }

    public void updateOrder(int id, Orders order) {
        Optional<Orders> existingOpt = this.orderRepository.findById(id);
        if (existingOpt.isPresent()) {
            Orders existing = existingOpt.get();
            existing.setoName(order.getoName());
            existing.setoPrice(order.getoPrice());
            existing.setoQuantity(order.getoQuantity());
            double total = Math.round((order.getoPrice() * order.getoQuantity()) * 100.0) / 100.0;
            existing.setTotalAmmout(total);
            this.orderRepository.save(existing);
        }
    }

    public void deleteOrder(int id) {
        if (this.orderRepository.existsById(id)) {
            this.orderRepository.deleteById(id);
        }
    }

    public List<Orders> getOrdersForUser(User user) {
        return this.orderRepository.findOrdersByUser(user);
    }

    public List<Orders> getOrdersForUserName(String username) {
        return orderRepository.findByUser_UnameContainingIgnoreCase(username);
    }

    public void toggleOrderStatus(int id) {
        Orders order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            order.setActivo(!order.isActivo());
            orderRepository.save(order);
        }
    }
}
