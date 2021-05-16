package com.litvak.cloudstorage.entities;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor

@Entity
@Table(name = "files")
public class FileApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column(name = "name_system")
    private String nameSystem;

    @Column
    private Long size;

    @Column
    private String strsize;

    @Column
    private String time;

    @Column
    private String date;

    @ManyToOne
    @JoinColumn(name = "dir_id")
    private DirApp dirApp;

}
