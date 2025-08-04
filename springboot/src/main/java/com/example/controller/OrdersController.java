package com.example.controller;

import com.example.common.Result;
import com.example.entity.*;
import com.example.service.OrdersService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    /**
     * add
     */
    @PostMapping("/add")
    public Result add(@RequestBody Orders orders) {
        ordersService.add(orders);
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

    @PostMapping("/{id}/clone")
    public Result cloneOrder(@PathVariable Integer id) {
        Orders clonedOrder = ordersService.cloneOrder(id);
        return Result.success(clonedOrder);
    }

    @PostMapping("/{id}/handle/{type}")
    public Result handleOrder(@PathVariable Integer id, @PathVariable String type) {
        ordersService.handleOrderWithType(id, type);
        return Result.success();
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
