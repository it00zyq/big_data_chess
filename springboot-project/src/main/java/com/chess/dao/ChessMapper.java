package com.chess.dao;

import com.chess.bean.ChessMove;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ChessMapper {
	
	List<ChessMove> queryString(String move);

	void insertDB(String res);

}
