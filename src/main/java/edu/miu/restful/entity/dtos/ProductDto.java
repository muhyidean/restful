package edu.miu.restful.entity.dtos;

import lombok.Data;

@Data
public class ProductDto {


    private int id;
    private String name;
    private float price;

    private String category;
    private int discount;
    private int starCount;


}
