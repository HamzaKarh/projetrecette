package com.example.projetrecette.NavigationBar.Recipe;

public class Recipe {
    public String name;
    public String author;
    public String time;
    public String ingredients;

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setCooking_time(String cooking_time) {
        this.cooking_time = cooking_time;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    public String picture;

    public String getIngredients() {
        return ingredients;
    }

    public String getPicture() {
        return picture;
    }

    public String getCooking_time() {
        return cooking_time;
    }

    public String getRecipe() {
        return recipe;
    }

    public String cooking_time;
    public String recipe;
    public int rating;

    public Recipe(){

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getAuthor() {
        return author;
    }

    public String getTime() {
        return time;
    }

    public int getRating() {
        return rating;
    }


    public String getName() {
        return name;
    }



    public Recipe(String name, String author, String time, int rating) {
        this.name = name;
        this.author = author;
        this.time = time;
        this.rating = rating;
    }
}
