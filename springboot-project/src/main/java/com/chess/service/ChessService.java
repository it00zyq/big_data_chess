package com.chess.service;

import com.chess.bean.ChessMove;

import java.util.List;

/**
 * @author IT00ZYQ
 * @date 2021/4/27 8:57
 **/
public interface ChessService {

    /**
     * 获取下一步走法
     * @param move 当前所有已走步骤
     * @return 下一步走法
     */
    String queryString(String move);

    /**
     * 学习新的棋盘
     * @param res 棋盘
     */
    void addNewPan(String res);

}
