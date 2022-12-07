package com.example.fire.Models;

import java.util.List;

import lombok.Data;

@Data
public class Dishes {
    String dish;
    List<String> ingredients;
    List<String> description;
    List<String> instructions;
    List<String> origIngredients;
    String videoURL;
}
