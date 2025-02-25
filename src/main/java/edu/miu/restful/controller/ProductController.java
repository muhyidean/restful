package edu.miu.restful.controller;

import edu.miu.restful.entity.Product;
import edu.miu.restful.entity.Review;
import edu.miu.restful.entity.dto.ProductDetailDto;
import edu.miu.restful.entity.dto.ProductDto;
import edu.miu.restful.repo.ProductRepo;
import edu.miu.restful.service.ProductService;
import edu.miu.restful.service.impl.ProductServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/products")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping // GET - localhost:8080/api/v1/products
    public List<ProductDto> getAll() {
        return productService.findAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/xml", produces = MediaType.APPLICATION_XML_VALUE )
    public List<ProductDto> getAllx() {
        var products = productService.findAllPriceGreaterThan(1060);
        System.out.println(products);
        return products;
    }

//    @ResponseStatus(HttpStatus.OK)
//    @GetMapping() // GET /api/v1/products
//    public List<ProductDto> getAll(@RequestParam(value = "filter-price-greater" ,required = false) Integer price) {
//        return price==null?
//                productService.findAll():
//                productService.findAllPriceGreaterThan(price);
//    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping // POST - /api/v1/products
    public void save(@RequestBody Product p) { // Json --> Java
        productService.save(p);
    }

    // GET /api/v1/products/111
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ProductDto>> getById(@PathVariable("id") int id) {
        var product = productService.getById(id);

        EntityModel<ProductDto> resource = EntityModel.of(product);

        // Self-link
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getById(id))
                .withSelfRel());

        // Add all products
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductController.class).getAll())
                .withRel("all-products"));

        // Add review link
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductController.class).getReviewsByProductId(id))
                .withRel("product-reviews"));

        CacheControl cacheControl = CacheControl.maxAge(1, TimeUnit.DAYS)
                .cachePublic(); // Use cachePrivate() if it should only be cached by browser, not proxies

        return ResponseEntity.ok()
                .cacheControl(cacheControl)
                .body(resource);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        productService.delete(id);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable("id") int productId, @RequestBody ProductDto p) {
        productService.update(productId, p);
    }

    @GetMapping("/{id}/reviews") // /api/v1/products/111/reviews
    public ProductDetailDto getReviewsByProductId(@PathVariable int id) {
        return productService.getReviewsByProductId(id);
    }

    @GetMapping("/{id}/reviews/{reviewId}") // WITHOUT DTO
    public Review getReviewByProductId(@PathVariable("id") int pId, @PathVariable("reviewId") int reviewId) {
        return productService.getReviewByProductId(pId, reviewId);
    }


    // FOR DEMO PURPOSES
    @GetMapping("/{productId}/{reviewId}")
    public Review mapDemo(@PathVariable Map<String, Integer> pathVariables) {
        int pid = pathVariables.get("productId");
        int reviewId = pathVariables.get("reviewId");
        return null;
    }

    // FOR DEMO PURPOSES
    @GetMapping(value =
            {
                    "/handlingMultipleEndpoints",
                    "/handlingMultipleEndpoints/{id}"
            })
    public String multipleEndpointsDemo(@PathVariable(required = false) String id) {
        if (id != null) {
            return "ID: " + id;
        } else {
            return "ID missing";
        }
    }

    // FOR DEMO PURPOSES
    @GetMapping("/h/{id}")
    public EntityModel<ProductDto> getByIdH(@PathVariable int id) {

        ProductDto product = productService.getById(id);
        EntityModel<ProductDto> resource = EntityModel.of(product);
        WebMvcLinkBuilder linkTo = WebMvcLinkBuilder
                .linkTo(
                        WebMvcLinkBuilder.methodOn(this.getClass()).getAll());
        resource.add(linkTo.withRel("all-products"));

        return resource;
    }

    @GetMapping("/map-test/{author}/{title}")
    public String mapInPathVariable(@PathVariable Map<String, String> vals) {

        return "author: " + vals.get("author") + "   " + "title: " + vals.get("title");
    }


}
