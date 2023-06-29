package com.example.server.models;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "messages")
@Setter
@Getter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "receiver_id")
    private Long receiverId;

    @Column(name = "text")
    private String text;

    @Column(name = "image")
    private String image;

    @Column(name = "status")
    private String status;

    @Column(nullable = true,name = "timestamp")
    @CreationTimestamp
    private Timestamp timestamp ;

}
