package cn.parzulpan.mapper;

import cn.parzulpan.bean.Department;
import org.apache.ibatis.annotations.Select;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc :
 */

public interface DepartmentMapper {

    @Select("select * from department where id = #{id}")
    public Department getDeptById(Integer id);
}
