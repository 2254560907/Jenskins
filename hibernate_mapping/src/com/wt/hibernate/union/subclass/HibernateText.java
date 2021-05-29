package com.wt.hibernate.union.subclass;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HibernateText {

	private SessionFactory sessionFactory;
	private Session session;
	private Transaction transaction;

	@Before
	public void init() {
		// 测试用
		// System.out.println("init");

		Configuration configuration = new Configuration().configure();
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder()
				.applySettings(configuration.getProperties())
				.buildServiceRegistry();

		sessionFactory = configuration.buildSessionFactory(serviceRegistry);

		session = sessionFactory.openSession();

		transaction = session.beginTransaction();
	}

	@After
	public void destroy() {
		// 测试用
		// System.out.println("destroy");

		transaction.commit();
		session.close();
		sessionFactory.close();
	}
	
	/**
	 * 使用 union-Subclass 的 优点：
	 * 1. 不需要使用辨别者列
	 * 2. 子类独有的字段能添加非空约束
	 * 
	 * 使用 union-Subclass 的 缺点：
	 * 1. 存在冗余的字段
	 * 2. 若更新父表的字段， 则更新的效率较低
	 * 
	 */
	
	@Test
	public void testUpdate() {
		
		String hql = "UPDATE Person p SET p.age = 20";
		
		session.createQuery(hql).executeUpdate();
	}
	
	/**
	 * 查询：
	 * 1. 查询父类记录，需把父表和子表汇总到一起在做查询，性能稍差，
	 * 2. 对于子类记录，也只需要查询一张数据表
	 */
	@Test
	public void testQuery() {
		List<Person> persons = session.createQuery("FROM Person").list();
		System.out.println(persons.size());
		

		List<Student> students = session.createQuery("FROM Student").list();
		System.out.println(students.size());
	}
	
	/**
	 *  插入操作：
	 *  1. 对于子类对象只需把记录插入到一张数据表中
	 */
	@Test
	public void testSave(){
		
		Person person = new Person();
		person.setName("AA");
		person.setAge(11);
		
		session.save(person);
		
		Student student = new Student();
		student.setName("BB");
		student.setAge(22);
		student.setSchool("QDU");
		
		session.save(student); 
		
	}
	
}
