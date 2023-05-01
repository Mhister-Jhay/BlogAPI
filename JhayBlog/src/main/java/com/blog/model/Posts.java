package com.blog.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "posts")
public class Posts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,unique = true)
    private String title;
    @Column(nullable = false)
    private String caption;
    @Column(nullable = false)
    private String content;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "posts_categories",
            joinColumns = @JoinColumn(
                    name = "post_id",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "category_id",
                    referencedColumnName = "id"
            )
    )
    private Set<Category> categories;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id"
    )
    private Users user;
    private Long commentCount = 0L;
    @OneToMany(mappedBy = "post")
    private Set<Comment> comments;
    private Long likeCount = 0L;
    @OneToMany(mappedBy = "post",orphanRemoval = true)
    private Set<Likes> likes;
}
