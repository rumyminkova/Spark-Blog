package com.teamtreehouse.blog.model;

import com.github.slugify.Slugify;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BlogEntry {

    private String title;
    private LocalDateTime dateCreated;
    private String body;
    private List<Comment> comments;
    private String slug;
    private List<String> blogEntryTags;



    public BlogEntry(String title){
        this.title = title;
        this.dateCreated = LocalDateTime.now();
        this.comments=new ArrayList<>();
        Slugify slugify=new Slugify();
        slug=slugify.slugify(title);
        this.blogEntryTags=new ArrayList<>();

    }

    public String getSlug() {
        return slug;
    }

    public boolean addComment(Comment comment) {
        // Store these comments!
        return (comments.add(comment));
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public List<Comment> getComments() {
        return new ArrayList<>(comments);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getBlogEntryTags() {
        return blogEntryTags;
    }

    public void addTag(String tag){
        blogEntryTags.add(tag);
    }

    public void addTags(ArrayList<String> tags){
        blogEntryTags.addAll(tags);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlogEntry blogEntry = (BlogEntry) o;

        if (title != null ? !title.equals(blogEntry.title) : blogEntry.title != null) return false;
        return dateCreated.equals(blogEntry.dateCreated);
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + dateCreated.hashCode();
        return result;
    }
}
