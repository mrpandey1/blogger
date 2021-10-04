package com.example.blogger;

public class Post {
    public String name;
    public String title;
    public String description;
    public String location;
    public String img;
    public String author;
    public Post(){

    }
    public Post(String name,String title,String description,String location,String img,String author){
        this.name=name;
        this.title=title;
        this.description=description;
        this.location=location;
        this.img=img;
        this.author=author;
    }
}
