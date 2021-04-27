package com.chess.service.impl;

import com.chess.bean.ChessMove;
import com.chess.dao.ChessMapper;
import com.chess.service.ChessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author IT00ZYQ
 * @date 2021/4/27 8:57
 **/
@Service
@Slf4j
public class ChessServiceImpl implements ChessService {

	private final ChessMapper chessMapper;
	public ChessServiceImpl(ChessMapper chessMapper) {
		this.chessMapper = chessMapper;
	}


	@Override
	public String queryString(String move) {
		List<ChessMove> chessMoves = chessMapper.queryString(move);
		Map<String, Integer> countMap = new HashMap<String, Integer>();
		for (int i = 0; i < chessMoves.size(); i++) {
			String temp = chessMoves.get(i).getMovelist().substring(move.length(), move.length() + 4);
			if (countMap.containsKey(temp)) {
				int value = countMap.get(temp);
				countMap.put(temp, value + 1);
			} else {
				countMap.put(temp, 1);
			}
		}
		int max = 1;
		String result = "";
		for (Map.Entry<String, Integer> entry : countMap.entrySet())
		{
			if (max <= entry.getValue()) {
				max = entry.getValue();
				result = entry.getKey();
			}
		}
		log.info("【查询字符串】: {}, 【最佳走法】: {}, 【存在次数】: {}", move, result, max);
		return result;
	}

	@Override
	public void addNewPan(String res) {
		chessMapper.insertDB(res);
	}
}
