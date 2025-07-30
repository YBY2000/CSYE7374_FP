package com.example.controller;

import com.example.common.Result;
import com.example.entity.Foods;
import com.example.service.FoodsService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/foods")
public class FoodsController {

    private final FoodsService foodsService = new FoodsService();

    /**
     * add
     */
    @PostMapping("/add")
    public Result add(@RequestBody Foods foods) {
        foodsService.add(foods);
        return Result.success();
    }

    /**
     * delete
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        foodsService.deleteById(id);
        return Result.success();
    }

    /**
     * batch delete
     */
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids) {
        foodsService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * update
     */
    @PutMapping("/update")
    public Result update(@RequestBody Foods foods) {
        foodsService.updateById(foods);
        return Result.success();
    }

    /**
     * query(select) single data by id
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Foods foods = foodsService.selectById(id);
        return Result.success(foods);
    }

    /**
     * query(select) all
     */
    @GetMapping("/selectAll")
    public Result selectAll(String name) {
        List<Foods> list = foodsService.selectAll(name);
        return Result.success(list);
    }

    /**
     * query(select) all with page
     */
    @GetMapping("/selectPage")
    public Result selectPage(
            String name,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Foods> pageInfo = foodsService.selectPage(name, pageNum, pageSize);
        return Result.success(pageInfo);
    }

}
