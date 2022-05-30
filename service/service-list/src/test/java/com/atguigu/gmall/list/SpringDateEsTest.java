package com.atguigu.gmall.list;

import com.atguigu.gmall.list.bean.Person;
import com.atguigu.gmall.list.dao.PersonEsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.util.*;

@SpringBootTest
public class SpringDateEsTest {
    @Autowired
    PersonEsDao personEsDao;

    @Test
    void  esQueryTest() throws ParseException {
        //根据ID查询
//        Optional<Person> person = personEsDao.findById(4L);
//        System.out.println("person = " + person.get());

        //批量查询
//        List<Person> address = personEsDao.findAllByAddressLike("西大街");
//        for (Person address1 : address) {
//            System.out.println("address1 = " + address1 );
//        }

        //根据条件查询
//        Date parse = new SimpleDateFormat("yyyy-MM-dd").parse("2022-05-28");
        List<Person> all = personEsDao.findAllByIdGreaterThanEqualOrAddressLike(2L,  "中");
        for (Person person1 : all) {
            System.out.println("person1 = " + person1);
        }
    }

    @Test
    void esInsert(){
        //保存单个
//        Person person = new Person(1L,"飞龙","湖北武汉",new Date());
//        personEsDao.save(person);
        //批量保存

        List<Person> personList = Arrays.asList(new Person(1L, "飞龙", "南", new Date()),
                new Person(2L, "文超", "中", new Date()),
                new Person(3L, "蒋同学", "湖北武汉东大街", new Date()),
                new Person(4L, "邬治", "湖北武汉西大街", new Date()));

        personEsDao.saveAll(personList);
    }
}
