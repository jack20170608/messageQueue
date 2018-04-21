package com.fangming.mq.activeMq.samples.nonExclusive.mapper;


import com.fangming.mq.activeMq.samples.nonExclusive.domain.City;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CityMapper {

    @Select("SELECT * FROM CITY2 WHERE state = #{state}")
    City findByState(String state);

    @Insert("insert into city (name, state, country) values ('San Francisco', #{state}, 'US');")
    void insert(String state);

    @Select("SELECT count(*) FROM CITY")
    int count();

}
