package com.chess.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author IT00ZYQ
 * @Date 2020/12/23 22:22
 **/
@Data
@AllArgsConstructor
public class ResultVO {
    private Integer code;
    private String data;

    public static  ResultVO success(Integer code, String object){
        return new ResultVO(code, object);
    }
}
