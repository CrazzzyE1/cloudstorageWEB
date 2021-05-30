package com.litvak.cloudstorage.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor

@Entity
@Table(name = "directories")
public class DirApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "dir_parent_id")
    private Integer dirId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "dirApp")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<FileApp> files;
}
