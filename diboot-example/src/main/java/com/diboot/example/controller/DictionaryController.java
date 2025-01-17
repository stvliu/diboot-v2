package com.diboot.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.binding.RelationsBinder;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.entity.Dictionary;
import com.diboot.core.service.BaseService;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.example.service.DictionaryService;
import com.diboot.example.vo.DictionaryListVO;
import com.diboot.example.vo.DictionaryVO;
import com.diboot.shiro.authz.annotation.AuthorizationPrefix;
import com.diboot.shiro.authz.annotation.AuthorizationWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 数据字典类
 *
 * @author wee
 */
@RestController
@RequestMapping("/dictionary")
@AuthorizationPrefix(name = "数据字典", code = "dictionary", prefix = "dictionary")
public class DictionaryController extends BaseCrudRestController {
    private static final Logger logger = LoggerFactory.getLogger(DictionaryController.class);

    @Autowired
    private DictionaryService dictionaryService;

    @Override
    protected BaseService getService() {
        return dictionaryService;
    }

    /**
     * 获取列表页数据
     *
     * @param dictionary
     * @param pagination
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/list")
    @AuthorizationWrapper(value = @RequiresPermissions("list"), name = "列表")
    public JsonResult list(Dictionary dictionary, Pagination pagination, HttpServletRequest request) throws Exception {
        //构建查询条件
        QueryWrapper<Dictionary> queryWrapper = super.buildQueryWrapper(dictionary);
        queryWrapper.lambda().eq(Dictionary::getParentId, 0)
                .orderByAsc(Dictionary::getSortId);
        //获取实体list
        List<Dictionary> dictionaryList = dictionaryService.getEntityList(queryWrapper, pagination);
        //筛选出在列表页展示的字段
        List<DictionaryListVO> dicVoList = RelationsBinder.convertAndBind(dictionaryList, DictionaryListVO.class);
        //返回结果
        return new JsonResult(Status.OK, dicVoList).bindPagination(pagination);
    }

    /*
     * 获取entity详细数据
     * */
    @GetMapping("/{id}")
    @AuthorizationWrapper(value = @RequiresPermissions("read"), name = "读取")
    public JsonResult getEntity(@PathVariable("id") Long id, HttpServletRequest request) {
        DictionaryVO vo = dictionaryService.getViewObject(id, DictionaryVO.class);
        return new JsonResult(vo);
    }

    /*
     * 新建
     * */
    @PostMapping("/")
    @AuthorizationWrapper(value = @RequiresPermissions("create"), name = "新建")
    public JsonResult create(@RequestBody DictionaryVO entityVO, HttpServletRequest request) {
        boolean success = dictionaryService.createDictionary(entityVO);
        if (success) {
            return new JsonResult(Status.OK);
        } else {
            return new JsonResult(Status.FAIL_OPERATION);
        }
    }

    /*
     * 更新
     * */
    @PutMapping("/{id}")
    @AuthorizationWrapper(value = @RequiresPermissions("update"), name = "更新")
    public JsonResult update(@PathVariable("id") Long id, @RequestBody DictionaryVO entityVO, HttpServletRequest request) {
        entityVO.setId(id);
        boolean success = dictionaryService.updateDictionary(entityVO);
        if (success) {
            return new JsonResult(Status.OK);
        } else {
            return new JsonResult(Status.FAIL_OPERATION);
        }
    }

    /*
     * 删除
     * */
    @DeleteMapping("/{id}")
    @AuthorizationWrapper(value = @RequiresPermissions("delete"), name = "删除")
    public JsonResult delete(@PathVariable("id") Long id) {
        boolean success = dictionaryService.deleteDictionary(id);
        if (success) {
            return new JsonResult(Status.OK);
        } else {
            return new JsonResult(Status.FAIL_OPERATION);
        }
    }

    /*
     * 校验类型编码是否重复
     * */
    @GetMapping("/checkTypeRepeat")
    public JsonResult checkTypeRepeat(@RequestParam(required = false) Long id, @RequestParam String type, HttpServletRequest request) {
        if (V.notEmpty(type)) {
            LambdaQueryWrapper<Dictionary> wrapper = new LambdaQueryWrapper();
            wrapper.eq(Dictionary::getType, type)
                    .eq(Dictionary::getParentId, 0);
            if (V.notEmpty(id)) {
                wrapper.ne(Dictionary::getId, id);
            }
            List<Dictionary> dictionaryList = dictionaryService.getEntityList(wrapper);
            if (V.isEmpty(dictionaryList)) {
                return new JsonResult(Status.OK);
            }
            return new JsonResult(Status.FAIL_OPERATION, "类型编码已存在");
        }

        return new JsonResult(Status.OK);
    }

}
