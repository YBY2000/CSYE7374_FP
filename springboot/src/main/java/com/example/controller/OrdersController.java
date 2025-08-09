package com.example.controller;

import com.example.common.Result;
import com.example.entity.*;
import com.example.service.OrdersService;
import com.example.service.FoodsService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;
    private final FoodsService foodsService = new FoodsService();

    /**
     * add
     */
    @PostMapping("/add")
    public Result add(@RequestBody Orders orders) {
        ordersService.add(orders);
        return Result.success();
    }

    /**
     * Create order from items with decorators, compute total and content on server
     */
    @PostMapping("/addWithItems")
    public Result addWithItems(@RequestBody CreateOrderRequest req) {
        if (req == null || req.getItems() == null || req.getItems().isEmpty()) {
            return Result.error("Empty items");
        }

        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        StringBuilder contentBuilder = new StringBuilder();

        for (OrderItemRequest item : req.getItems()) {
            Foods food = foodsService.selectById(item.getFoodId());
            if (food == null) {
                return Result.error("Food not found: " + item.getFoodId());
            }
            com.example.entity.FoodItem decorated = food;
            java.util.List<String> decs = item.getDecorators();
            if (decs != null) {
                for (String code : decs) {
                    Decorator d = Decorator.fromCode(code);
                    if (d != null) {
                        decorated = d.apply(decorated);
                    }
                }
            }

            java.math.BigDecimal linePrice = decorated.getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity()));
            total = total.add(linePrice);

            contentBuilder.append(food.getName())
                .append(" * ")
                .append(item.getQuantity());
            if (decs != null && !decs.isEmpty()) {
                contentBuilder.append(" (").append(String.join(", ", decs)).append(")");
            }
            contentBuilder.append(", ");
        }

        String content = contentBuilder.length() > 1 ? contentBuilder.substring(0, contentBuilder.length() - 2) : "";

        Orders order = new Orders();
        order.setContent(content);
        // DB column is DECIMAL(10,0); round to integer to avoid truncation
        order.setTotal(total.setScale(0, java.math.RoundingMode.HALF_UP));
        order.setUserId(req.getUserId());

        ordersService.add(order);
        return Result.success();
    }

    /**
     * delete
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        ordersService.deleteById(id);
        return Result.success();
    }

    /**
     * batch delete
     */
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids) {
        ordersService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * update
     */
    @PutMapping("/update")
    public Result update(@RequestBody Orders orders) {
        ordersService.updateById(orders);
        return Result.success();
    }

    /**
     * query(select) single data by id
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
       Orders orders = ordersService.selectById(id);
        return Result.success(orders);
    }

    /**
     * query(select) all
     */
    @GetMapping("/selectAll")
    public Result selectAll(String userName, Integer userId) {
        List<Orders> list = ordersService.selectAll(userName, userId);
        return Result.success(list);
    }

    /**
     * query(select) all with page
     */
    @GetMapping("/selectPage")
    public Result selectPage(
            String userName,
            Integer userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Orders> pageInfo = ordersService.selectPage(userName, userId, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    @PutMapping("/{id}/state/{state}")
    public Result changeOrderState(@PathVariable Integer id, @PathVariable String state) {
        ordersService.processOrderWithState(id, state);
        return Result.success();
    }

    /**
     * Cancel order (can be called by user or admin)
     */
    @PutMapping("/{id}/cancel")
    public Result cancelOrder(@PathVariable Integer id) {
        ordersService.cancelOrder(id);
        return Result.success("Order cancelled successfully");
    }

    /**
     * Confirm order by admin (change to PREPARING)
     */
    @PutMapping("/{id}/confirm")
    public Result confirmOrder(@PathVariable Integer id) {
        ordersService.confirmOrder(id);
        return Result.success("Order confirmed successfully");
    }

    /**
     * Complete order (change to COMPLETED)
     */
    @PutMapping("/{id}/complete")
    public Result completeOrder(@PathVariable Integer id) {
        ordersService.completeOrder(id);
        return Result.success("Order completed successfully");
    }

    @PostMapping("/{id}/clone")
    public Result cloneOrder(@PathVariable Integer id) {
        Orders clonedOrder = ordersService.cloneOrder(id);
        return Result.success(clonedOrder);
    }

    @PostMapping("/{id}/observer")
    public Result addObserver(@PathVariable Integer id, @RequestParam String observerType) {
        OrderObserver observer = null;
        switch (observerType.toLowerCase()) {
            case "user":
                observer = new UserNotifier();
                break;
            case "admin":
                observer = new AdminNotifier();
                break;
            default:
                return Result.error("Unknown observer type: " + observerType);
        }
        ordersService.addObserverToOrder(id, observer);
        return Result.success();
    }
}
