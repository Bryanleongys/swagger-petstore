/**
 * Copyright 2018 SmartBear Software
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.swagger.petstore.data;

import io.swagger.petstore.model.Order;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderData {
    private static List<Order> orders = new ArrayList<>();

    static {
        orders.add(createOrder(1, 1, 100, new Date(), "placed", true));
        orders.add(createOrder(2, 1, 50, new Date(), "approved", true));
        orders.add(createOrder(3, 1, 50, new Date(), "delivered", true));
    }

    public Order getOrderById(final long orderId) {
        for (final Order order : orders) {
            if (order.getId() == orderId) {
                return order;
            }
        }
        return null;
    }

    public Map<String, Integer> getCountByStatus() {

        final Map<String, Integer> countByStatus = new HashMap<>();

        for (final Order order : orders) {
            final String status = order.getStatus();
            if (countByStatus.containsKey(status)) {
                countByStatus.put(status, countByStatus.get(status) + order.getQuantity());
            } else {
                countByStatus.put(status, order.getQuantity());
            }
        }

        return countByStatus;
    }

    public void addOrder(final Order order) {
        if (orders.size() > 0) {
            orders.removeIf(orderN -> orderN.getId() == order.getId());
        }
        orders.add(order);
    }

    public void deleteOrderById(final Long orderId) {
        orders.removeIf(order -> order.getId() == orderId);
    }

    public List<Order> getPendingOrders() {
        final List<Order> pendingOrders = new ArrayList<>();
        for (final Order order : orders) {
            final String status = order.getStatus();
            if ("pending".equals(status) || "placed".equals(status)) {
                pendingOrders.add(order);
            }
        }
        return pendingOrders;
    }

    public List<Order> searchOrders(final String status, final Long petId) {
        final List<Order> result = new ArrayList<>();
        for (final Order order : orders) {
            boolean matches = true;

            if (status != null && !status.isEmpty()) {
                if (order.getStatus() == null || !order.getStatus().equals(status)) {
                    matches = false;
                }
            }

            if (petId != null && matches) {
                if (order.getPetId() != petId) {
                    matches = false;
                }
            }

            if (matches) {
                result.add(order);
            }
        }
        return result;
    }

    public Map<String, Object> getStoreStats() {
        final Map<String, Object> stats = new HashMap<>();
        final Map<String, Integer> ordersByStatus = new HashMap<>();
        int totalQuantity = 0;

        for (final Order order : orders) {
            // Count orders by status
            final String status = order.getStatus();
            if (status != null) {
                ordersByStatus.put(status, ordersByStatus.getOrDefault(status, 0) + 1);
            }

            // Sum total quantity
            if (order.getQuantity() != null) {
                totalQuantity += order.getQuantity();
            }
        }

        stats.put("totalOrders", orders.size());
        stats.put("totalQuantity", totalQuantity);
        stats.put("ordersByStatus", ordersByStatus);

        return stats;
    }

    public static Order createOrder(final long id, final long petId, final int quantity, final Date shipDate,
                                     final String status, final boolean complete) {
        final Order order = new Order();
        order.setId(id);
        order.setPetId(petId);
        order.setComplete(complete);
        order.setQuantity(quantity);
        order.setShipDate(shipDate);
        order.setStatus(status);
        return order;
    }
}
