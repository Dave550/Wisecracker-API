//package edu.citadel.dal.model;
//
//import java.sql.Timestamp;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.Table;
//import lombok.Data;
//import org.hibernate.annotations.CreationTimestamp;
//
//@Data
//@Entity
//@Table(name = "accounts")
//public class Account {
//  @Id
//  @GeneratedValue(strategy= GenerationType.IDENTITY)
//  private Long user_id;
//  private String username;
//  private String password;
//  private String email;
//  @CreationTimestamp
//  private Timestamp created_on;
//  @CreationTimestamp
//  private Timestamp last_login;
//}
