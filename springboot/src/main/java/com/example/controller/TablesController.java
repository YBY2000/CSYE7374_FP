package com.example.controller;

import com.example.common.Result;
import com.example.entity.Tables;
import com.example.service.TablesService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tables")
public class TablesController {

    private final TablesService tablesService = new TablesService();

    @PostMapping("/add")
    public Result add(@RequestBody Tables tables) {
        tablesService.add(tables);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        tablesService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids) {
        tablesService.deleteBatch(ids);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Tables tables) {
        tablesService.updateById(tables);
        return Result.success();
    }

    @PutMapping("/addOrder")
    public Result addOrder(@RequestBody Tables tables) {
        tablesService.addOrder(tables);
        return Result.success();
    }

    @PutMapping("/removeOrder")
    public Result removeOrder(@RequestBody Tables tables) {
        tablesService.removeOrder(tables);
        return Result.success();
    }

    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Tables tables = tablesService.selectById(id);
        return Result.success(tables);
    }

    @GetMapping("/selectByUserId/{userId}")
    public Result selectByUserId(@PathVariable Integer userId) {
        Tables tables = tablesService.selectByUserId(userId);
        return Result.success(tables);
    }

    @GetMapping("/selectAll")
    public Result selectAll(String name) {
        List<Tables> list = tablesService.selectAll(name);
        return Result.success(list);
    }

    @GetMapping("/selectPage")
    public Result selectPage(
            String name,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Tables> pageInfo = tablesService.selectPage(name, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    @PutMapping("/updateStatus/{id}")
    public Result updateStatus(@PathVariable Integer id, @RequestParam String status) {
        tablesService.updateStatus(id, status);
        return Result.success();
    }

    @PutMapping("/assign/{tableId}")
    public Result assignTable(@PathVariable Integer tableId, @RequestParam Integer userId) {
        tablesService.assignTable(tableId, userId);
        return Result.success();
    }

    @PutMapping("/release/{tableId}")
    public Result releaseTable(@PathVariable Integer tableId) {
        tablesService.releaseTable(tableId);
        return Result.success();
    }

    @PutMapping("/reserve/{tableId}")
    public Result reserveTable(@PathVariable Integer tableId) {
        tablesService.reserveTable(tableId);
        return Result.success();
    }

    @GetMapping("/getState/{id}")
    public Result getTableState(@PathVariable Integer id) {
        Tables table = tablesService.selectById(id);
        if (table != null) {
            return Result.success(table.getCurrentStateName());
        }
        return Result.error("Table not found");
    }
}
