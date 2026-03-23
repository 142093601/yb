package com.campusnews.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campusnews.entity.News;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NewsMapper extends BaseMapper<News> {
}
