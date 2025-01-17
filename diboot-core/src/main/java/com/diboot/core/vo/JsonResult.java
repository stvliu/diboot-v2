package com.diboot.core.vo;

import com.diboot.core.util.S;
import com.diboot.core.util.V;

import java.io.Serializable;

/**
 * JSON返回结果
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/01/01
 */
public class JsonResult implements Serializable {
    private static final long serialVersionUID = 1001L;

    /***
     * 状态码
     */
    private int code;
    /***
     * 消息内容
     */
    private String msg;
    /***
     * 返回结果数据
     */
    private Object data;

    /**
     * 默认成功，无返回数据
      */
    public JsonResult(){
    }

    /**
     * 默认成功，有返回数据（及附加提示信息）
     */
    public JsonResult(Object data, String... additionalMsg){
        this.code = Status.OK.code();
        this.msg = Status.OK.label();
        initMsg(additionalMsg);
        this.data = data;
    }

    /***
     * 非成功，指定状态（及附加提示信息）
     * @param status
     * @param additionalMsg
     */
    public JsonResult(Status status, String... additionalMsg){
        this.code = status.code();
        this.msg = status.label();
        initMsg(additionalMsg);
        this.data = null;
    }

    /**
     * 非成功，指定状态、返回数据（及附加提示信息）
      */
    public JsonResult(Status status, Object data, String... additionalMsg){
        this.code = status.code();
        this.msg = status.label();
        initMsg(additionalMsg);
        this.data = data;
    }

    /***
     * 自定义JsonResult
     * @param code
     * @param label
     * @param data
     */
    public JsonResult(int code, String label, Object data){
        this.code = code;
        this.msg = label;
        this.data = data;
    }

    /***
     * 绑定分页信息
     * @param pagination
     */
    public JsonResult bindPagination(Pagination pagination){
        return new PagingJsonResult(this, pagination);
    }

    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
    public Object getData() {
        return data;
    }

    /**
     * 赋值msg（去掉重复前缀以支持与BusinessException嵌套使用）
     * @param additionalMsg
     */
    private void initMsg(String... additionalMsg){
        if(V.notEmpty(additionalMsg)){
            if(S.startsWith(additionalMsg[0], this.msg)){
                this.msg = additionalMsg[0];
            }
            else{
                this.msg += ": " + additionalMsg[0];
            }
        }
    }

}