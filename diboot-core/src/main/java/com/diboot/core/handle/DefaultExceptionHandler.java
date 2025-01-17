package com.diboot.core.handle;

import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.S;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常统一处理的默认实现
 * （继承自该类并添加@ControllerAdvice注解即可自动支持兼容页面和JSON的异常处理）
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/07/19
 */
public class DefaultExceptionHandler {
    private final static Logger log = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    /**
     * 统一异常处理类
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Object handleException(HttpServletRequest request, Exception e) {
        HttpStatus status = getStatus(request);
        Map<String, Object> map = null;
        if(e instanceof BusinessException){
            BusinessException be = (BusinessException)e;
            map = be.toMap();
        }
        else{
            map = new HashMap<>();
            map.put("code", status.value());
            map.put("msg", e.getMessage());
        }
        if(isJsonRequest(request)) {
            log.warn("JSON请求异常", e);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
        else {
            //获取错误页面
            String viewName = getViewName(request, e);
            map.put("exception", e);
            map.put("status", status.value());
            map.put("message", map.get("msg"));
            map.put("timestamp", new Date());
            map.remove("msg");
            return new ModelAndView(viewName, map);
        }
    }

    /**
     * 获取默认的错误页面
     * @param request
     * @param ex
     * @return
     */
    protected String getViewName(HttpServletRequest request, Exception ex){
        return "error";
    }

    /**
     * 是否为JSON数据请求
     * @param request
     * @return
     */
    private boolean isJsonRequest(HttpServletRequest request){
        return S.contains(request.getHeader("Accept"),"json")
                || S.contains(request.getHeader("content-type"), "json");
    }

    /**
     * 获取状态码
     * @param request
     * @return
     */
    protected HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        }
        catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
