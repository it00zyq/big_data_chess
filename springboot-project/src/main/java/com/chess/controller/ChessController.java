package com.chess.controller;

import com.chess.service.impl.ChessServiceImpl;
import com.chess.vo.ResultVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChessController {
	private final ChessServiceImpl service;
	public ChessController(ChessServiceImpl service) {
		this.service = service;
	}

	/**
	 * 根据前端传过来的前面所有步骤，返回下一步最佳走法
	 * @param s 前面所有步骤
	 * @return 下一步最佳走法
	 */
	@PostMapping("/next")
	public ResultVO queryString(String s) {
		return ResultVO.success(200, service.queryString(s));
	}


	/**
	 * 新增棋谱
	 * @param s 棋谱
	 * @return success
	 */
	@PostMapping("/end")
	public ResultVO end(String s) {
		try {
			service.addNewPan(s);
		} catch (Exception e) {
			System.out.println("新增失败");
		}
		return ResultVO.success(200, "");
	}

}
