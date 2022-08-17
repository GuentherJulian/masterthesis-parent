package io.github.guentherjulian.masterthesis.grammargeneration.analysis;

public class ANTLRParseException extends RuntimeException {

	private int line;

	private int charPositionInLine;

	private String msg;

	/**
	 * @param line
	 * @param charPositionInLine
	 * @param msg
	 */
	public ANTLRParseException(int line, int charPositionInLine, String msg) {
		this.line = line;
		this.charPositionInLine = charPositionInLine;
		this.msg = msg;
	}

	/**
	 * Returns the field 'line'
	 * 
	 * @return value of line
	 */
	public int getLine() {
		return line;
	}

	/**
	 * Returns the field 'charPositionInLine'
	 * 
	 * @return value of charPositionInLine
	 */
	public int getCharPositionInLine() {
		return charPositionInLine;
	}

	/**
	 * Returns the field 'msg'
	 * 
	 * @return value of msg
	 */
	public String getMsg() {
		return msg;
	}

}
