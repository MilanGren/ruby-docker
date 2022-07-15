package com.example.springboot;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface FrontaMapper {

    @Select("SELECT * FROM fronta0")
    List<FrontaMember> getAll();

    @Insert("INSERT INTO fronta0 (noderef, edid, davkaid, status) VALUES(#{noderef}, #{edid}, #{davkaid}, #{status})")
    void insert(FrontaMember frontaMember);

}
