package com.example.projetrecette.Recette;

public class RecipeModel {

    String Auteur, Nom_Recette, Recipe_Pic, Temps_Cuisson, Rating, Recipe_id;

    public RecipeModel(){}
    public RecipeModel(String Auteur, String Nom_Recette, String Recipe_Pic, String Temps_Cuisson, String Rating, String Recipe_id){
        this.Auteur = Auteur;
        this.Nom_Recette = Nom_Recette;
        this.Recipe_Pic = Recipe_Pic;
        this.Temps_Cuisson = Temps_Cuisson;
        this.Rating = Rating;
        this.Recipe_id = Recipe_id;
    }

    public void setRecipe_id(String recipe_id) {
        Recipe_id = recipe_id;
    }

    public void setAuteur(String auteur) {
        Auteur = auteur;
    }

    public void setNom_Recette(String nom_Recette) {
        Nom_Recette = nom_Recette;
    }

    public void setRecipe_Pic(String recipe_Pic) {
        Recipe_Pic = recipe_Pic;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public void setTemps_Cuisson(String temps_Cuisson) {
        Temps_Cuisson = temps_Cuisson;
    }

    public String getAuteur() {
        return Auteur;
    }

    public String getNom_Recette() {
        return Nom_Recette;
    }

    public String getRating() {
        return Rating;
    }

    public String getRecipe_Pic() {
        return Recipe_Pic;
    }

    public String getTemps_Cuisson() {
        return Temps_Cuisson;
    }

    public String getRecipe_id() {
        return Recipe_id;
    }
}