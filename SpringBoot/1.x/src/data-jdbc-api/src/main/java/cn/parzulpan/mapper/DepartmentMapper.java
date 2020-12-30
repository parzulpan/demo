package cn.parzulpan.mapper;

import cn.parzulpan.bean.Department;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 操作 Department 表的 Mapper
 */

//@Repository
//@Mapper
public interface DepartmentMapper {

    @Select("select * from department where id=#{id}")
    public Department getDeptById(Integer id);

    @Delete("delete from department where id=#{id}")
    public int deleteDeptById(Integer id);

    // 自增
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into department(departmentName)values(#{departmentName})")
    public int insertDept(Department department);

    @Update("update department set departmentName=#{departmentName} where id=#{id}")
    public int updateDept(Department department);

}
