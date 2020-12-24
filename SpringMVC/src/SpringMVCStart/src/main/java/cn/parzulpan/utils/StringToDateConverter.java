package cn.parzulpan.utils;

import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 自定义类型转换器
 */

public class StringToDateConverter implements Converter<String, Date> {

    @Override
    public Date convert(String s) {
        if (s.equals("")) {
            throw new RuntimeException("请输入数据");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(s);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
