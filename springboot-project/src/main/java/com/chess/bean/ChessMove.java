package com.chess.bean;

import lombok.Data;

@Data
public class ChessMove {
	private String init;
	private String movelist;

	@Override
	public String toString() {
		return "ChessMove [init=" + init + ", movelist=" + movelist + "]";
	}
	
}
