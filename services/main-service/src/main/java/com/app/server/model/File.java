package com.app.server.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table
@Entity(name = "files")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long fileId;

    @Column(length = 128)
    private String fileName;

    @Column(length = 128)
    private String fileUrl;

    @Column(length = 128)
    private String fileType;

    private long fileSizeInBytes = 0;

    @Column(length = 64)
    private String fileExtension;

    @Column(length = 255)
    private String fileDescription;
}
