package edu.miu.restful.repo.impl;

import edu.miu.restful.entity.Product;
import edu.miu.restful.entity.Review;
import edu.miu.restful.entity.dto.ProductDto;
import edu.miu.restful.repo.ProductRepo;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ProductRepoImpl implements ProductRepo {
    private static List<Product> products;
    private static int productId = 300;
    static {
        products = new ArrayList<>();
        Review r1a = new Review(123,"I love it",5);
        Review r1b = new Review(145,"It is ordinary",4);
        List<Review> reviews1 = Arrays.asList(r1a,r1b);

        Review r2a = new Review(223,"Not that good",3);
        Review r2b = new Review(245,"It does the job",4);
        List<Review> reviews2 = Arrays.asList(r2a,r2b);

        Product p1 = new Product(111,"iPhone13",1100,reviews1);
        Product p2 = new Product(112,"galaxy",1050,reviews2);
        products.add(p1);
        products.add(p2);
    }


    public List<Product> findAll(){
        return products;
    }

    public void save(Product p) {
            p.setId(productId); // We are auto generating the id for DEMO purposes, (Normally, do not change your parameters)
            productId++;
            products.add(p);
        }


        @Override
    public void delete(int id) {
        var product =products
                .stream()
                .filter(l -> l.getId() == id)
                .findFirst().get();
        products.remove(product);
    }

    @Override
    public void update(int id, Product p) {
        Product toUpdate = getById(id);
        toUpdate.setName(p.getName());
        toUpdate.setPrice(p.getPrice());
    }

    public Product getById(int id) {
        return products
                .stream()
                .filter(l -> l.getId() == id)
                .findFirst()
                .orElse(null);

    }

    @Override
    public Review getReviewByProductId(int pId, int reviewId){

        return getById(pId).getReviews().stream()
                .filter(l -> l.getId() == reviewId)
                .findFirst()
                .orElse(null);

        // =============Just to clarify what's going on ==========================
        //        var p = getById(pId);
        //        var review = p.getReviews().stream()
        //                .filter(l -> l.getId() == reviewId)
        //                .findFirst()
        //                .orElse(null);

    }

    @Override
    public List<Product>findAllPriceGreaterThan(int price){
        return products.stream()
                .filter(p -> p.getPrice()>=price)
                .collect(Collectors.toList());
    }



}
