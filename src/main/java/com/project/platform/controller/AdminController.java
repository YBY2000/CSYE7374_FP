package com.project.platform.controller;

import com.project.platform.entity.Admin;
import com.project.platform.service.AdminService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Administer
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Resource
    private AdminService adminService;

    /**
     * query by page
     *
     * @param query
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("page")
    public ResponseVO<PageVO<Admin>> page(@RequestParam Map<String, Object> query, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<Admin> page = adminService.page(query, pageNum, pageSize);
        return ResponseVO.ok(page);

    }

    /**
     * query by id
     *
     * @param id
     * @return
     */
    @GetMapping("selectById/{id}")
    public ResponseVO<Admin> selectById(@PathVariable("id") Integer id) {
        Admin entity = adminService.selectById(id);
        return ResponseVO.ok(entity);
    }


    /**
     * list
     *
     * @return
     */
    @GetMapping("list")
    public ResponseVO<List<Admin>> list() {
        return ResponseVO.ok(adminService.list());
    }


    /**
     * add
     *
     * @param entity
     * @return
     */
    @PostMapping("add")
    public ResponseVO add(@RequestBody Admin entity) {
        adminService.insert(entity);
        return ResponseVO.ok();
    }

    /**
     * update
     *
     * @param entity
     * @return
     */
    @PutMapping("update")
    public ResponseVO update(@RequestBody Admin entity) {
        adminService.updateById(entity);
        return ResponseVO.ok();
    }

    /**
     * batch delete
     *
     * @param ids
     * @return
     */
    @DeleteMapping("delBatch")
    public ResponseVO delBatch(@RequestBody List<Integer> ids) {
        adminService.removeByIds(ids);
        return ResponseVO.ok();
    }
}
