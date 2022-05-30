package com.atguigu.gmall.list.dao;

import com.atguigu.gmall.list.bean.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonEsDao extends CrudRepository<Person,Long> {
    List<Person> findAllByAddressLike(String address);

    //查询ID大于2,生日在5.28之后,住在南大街的
    List<Person> findAllByIdGreaterThanEqualOrAddressLike(Long id, String address);
}
