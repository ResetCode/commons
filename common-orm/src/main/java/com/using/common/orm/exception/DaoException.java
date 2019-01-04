package com.using.common.orm.exception;

public class DaoException extends RuntimeException{

	private static final long serialVersionUID = 5474633465344084619L;

	/**
	 * 数据库操作，输入参数有误
	 */
	public static final DaoException DB_PARAM = new DaoException(000200, "参数有误");
	/**
     * 数据库操作,insert返回0
     */
    public static final DaoException DB_INSERT_RESULT_0 = new DaoException(
    		000300, "数据库操作,insert返回0");

    /**
     * 数据库操作,update返回0
     */
    public static final DaoException DB_UPDATE_RESULT_0 = new DaoException(
    		000301, "数据库操作,update返回0");

    /**
     * 数据库操作,selectOne返回null
     */
    public static final DaoException DB_SELECTONE_IS_NULL = new DaoException(
    		000302, "数据库操作,selectOne返回null");

    /**
     * 数据库操作,list返回null
     */
    public static final DaoException DB_LIST_IS_NULL = new DaoException(
    		000303, "数据库操作,list返回null");

    /**
     * 异常信息
     */
    protected String msg;

    /**
     * 具体异常码
     */
    protected int code;

    public DaoException(int code, String msgFormat, Object... args) {
        super(String.format(msgFormat, args));
        this.code = code;
        this.msg = String.format(msgFormat, args);
    }

    public DaoException() {
        super();
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

    public DaoException(Throwable cause) {
        super(cause);
    }

    public DaoException(String message) {
        super(message);
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }

    /**
     * 实例化异常
     * 
     * @param msgFormat
     * @param args
     * @return
     */
    public DaoException newInstance(String msgFormat, Object... args) {
        return new DaoException(this.code, msgFormat, args);
    }
}
