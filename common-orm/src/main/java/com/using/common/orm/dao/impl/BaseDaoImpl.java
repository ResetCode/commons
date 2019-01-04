package com.using.common.orm.dao.impl;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.using.common.orm.dao.BaseDao;
import com.using.common.orm.exception.DaoException;

public  class BaseDaoImpl<T>  implements BaseDao<T> {

    protected static final Logger LOG = LoggerFactory.getLogger(BaseDaoImpl.class);

    public static final String SQL_INSERT = "insert";
    public static final String SQL_BATCH_INSERT = "batchInsert";
    public static final String SQL_UPDATE_BY_ID = "updateByPrimaryKey";
    public static final String SQL_BATCH_UPDATE_BY_IDS = "batchUpdateByIds";
    public static final String SQL_BATCH_UPDATE_BY_COLUMN = "batchUpdateByColumn";
    public static final String SQL_SELECT_BY_ID = "selectByPrimaryKey";
    public static final String SQL_LIST_BY_COLUMN = "listByColumn";
    public static final String SQL_COUNT_BY_COLUMN = "getCountByColumn";
    public static final String SQL_DELETE_BY_ID = "deleteByPrimaryKey";
    public static final String SQL_BATCH_DELETE_BY_IDS = "batchDeleteByIds";
    public static final String SQL_BATCH_DELETE_BY_COLUMN = "batchDeleteByColumn";
    public static final String SQL_LIST_PAGE = "listPage";
    public static final String SQL_LIST_BY = "listBy";
    public static final String SQL_LIST_PAGE_COUNT = "listPageCount";
    public static final String SQL_COUNT_BY_PAGE_PARAM = "countByPageParam"; // 根据当前分页参数进行统计
    
    /**
     * 注入SqlSessionTemplate实例(要求Spring中进行SqlSessionTemplate的配置).
     * 可以调用sessionTemplate完成数据库操作.
     */
    @Autowired
    private SqlSessionTemplate sessionTemplate;

	/**
     * 单条插入数据.
     */
    @Override
    public int save(T entity) {
    	if(entity == null) {
    		return 0;
    	}
        int result = sessionTemplate.insert(getStatement(SQL_INSERT), entity);
        if (result <= 0) {
            throw DaoException.DB_INSERT_RESULT_0.newInstance("数据库操作,insert返回0.{%s}", getStatement(SQL_INSERT));
        }
        return result;
    }

    /**
     * 批量插入数据.
     */
    @Override
    public int batchSave(List<T> list) {
        if (list.isEmpty() || list.size() <= 0) {
            return 0;
        }
        int result = sessionTemplate.insert(getStatement(SQL_BATCH_INSERT), list);
        if (result <= 0) {
            throw DaoException.DB_INSERT_RESULT_0.newInstance("数据库操作,batchInsert返回0.{%s}", getStatement(SQL_BATCH_INSERT));
        }
        return result;
    }

    /**
     * 根据id单条更新数据.
     */
    @Override
    public int update(T entity) {
    	if(entity == null) {
    		return 0;
    	}
        int result = sessionTemplate.update(getStatement(SQL_UPDATE_BY_ID), entity);
        if (result <= 0) {
            throw DaoException.DB_UPDATE_RESULT_0.newInstance("数据库操作,updateByPrimaryKey返回0.{%s}", getStatement(SQL_UPDATE_BY_ID));
        }
        return result;
    }

    /**
     * 根据id批量更新数据.
     */
    @Override
    public int batchUpdate(List<T> list) {
        if (list.isEmpty() || list.size() <= 0) {
            return 0;
        }
        int result = sessionTemplate.update(getStatement(SQL_BATCH_UPDATE_BY_IDS), list);
        if (result <= 0) {
            throw DaoException.DB_UPDATE_RESULT_0.newInstance("数据库操作,batchUpdateByIds返回0.{%s}", getStatement(SQL_BATCH_UPDATE_BY_IDS));
        }
        return result;
    }

    /**
     * 根据column批量更新数据.
     */
    @Override
    public int update(Map<String, Object> paramMap) {
        if (paramMap == null) {
            return 0;
        }
        int result = sessionTemplate.update(getStatement(SQL_BATCH_UPDATE_BY_COLUMN), paramMap);
        if (result <= 0) {
            throw DaoException.DB_UPDATE_RESULT_0.newInstance("数据库操作,batchUpdateByColumn返回0.{%s}", getStatement(SQL_BATCH_UPDATE_BY_COLUMN));
        }
        return result;
    }

    /**
     * 根据id查询数据.
     */
    @Override
    public T getById(String id) {
    	if(id == null || id.length() == 0) {
    		 throw DaoException.DB_PARAM;
    	}
        return sessionTemplate.selectOne(getStatement(SQL_SELECT_BY_ID), id);
    }

    /**
     * 根据column查询数据.
     */
    @Override
    public T getByColumn(Map<String, Object> paramMap) {
        if (paramMap == null) {
            return null;
        }
        return sessionTemplate.selectOne(getStatement(SQL_LIST_BY_COLUMN), paramMap);
    }

    /**
     * 根据条件查询 getBy: selectOne <br/>
     * 
     * @param paramMap
     * @return
     */
    @Override
    public T getBy(Map<String, Object> paramMap) {
        if (paramMap == null) {
            return null;
        }
        return sessionTemplate.selectOne(getStatement(SQL_LIST_BY), paramMap);
    }
    
    /**
     * 根据条件查询列表数据.
     */
    @Override
    public List<T> listBy(Map<String, Object> paramMap) {
        if (paramMap == null) {
            return null;
        }
        return sessionTemplate.selectList(getStatement(SQL_LIST_BY), paramMap);
    }

    /**
     * 根据column查询列表数据.
     */
    @Override
    public List<T> listByColumn(Map<String, Object> paramMap) {
        if (paramMap == null) {
            return null;
        }
        return sessionTemplate.selectList(getStatement(SQL_LIST_BY_COLUMN), paramMap);
    }

    /**
     * 根据column查询记录数.
     */
    @Override
    public Long getCountByColumn(Map<String, Object> paramMap) {
        if (paramMap == null) {
            return null;
        }
        return sessionTemplate.selectOne(getStatement(SQL_COUNT_BY_COLUMN), paramMap);
    }

    /**
     * 根据id删除数据.
     */
    @Override
    public int delete(String id) {
        return (int) sessionTemplate.delete(getStatement(SQL_DELETE_BY_ID), id);
    }

    /**
     * 根据id批量删除数据.
     */
    @Override
    public int delete(List<T> list) {
        if (list.isEmpty() || list.size() <= 0) {
            return 0;
        } else {
            return (int) sessionTemplate.delete(getStatement(SQL_BATCH_DELETE_BY_IDS), list);
        }
    }

    /**
     * 根据column批量删除数据.
     */
    @Override
    public int delete(Map<String, Object> paramMap) {
        if (paramMap == null) {
            return 0;
        } else {
            return (int) sessionTemplate.delete(getStatement(SQL_BATCH_DELETE_BY_COLUMN), paramMap);
        }
    }


    /**
     * 函数功能说明 ： 获取Mapper命名空间. 
     * 
     * @参数：@param sqlId
     * @参数：@return
     * @return：String
     * @throws
     */
    public String getStatement(String sqlId) {
        String name = this.getClass().getName();
        // 单线程用StringBuilder，确保速度；多线程用StringBuffer,确保安全
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(".").append(sqlId);
        return sb.toString();
    }

}
