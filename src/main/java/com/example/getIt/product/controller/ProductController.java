package com.example.getIt.product.controller;

import com.example.getIt.product.DTO.ProductDTO;
import com.example.getIt.product.service.ProductService;
import com.example.getIt.util.BaseException;
import com.example.getIt.util.BaseResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/products")
public class ProductController {
    private ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }
    @ResponseBody
    @GetMapping("/list")
    public BaseResponse<List<ProductDTO.GetProduct>> getProducts(){
        try {
            List<ProductDTO.GetProduct> getProducts = this.productService.getProductAll();
            return new BaseResponse<>(getProducts);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    //์ํ ์กฐํ
    @ResponseBody
    @GetMapping("/{productIdx}")
    public BaseResponse<String> getProduct(@PathVariable("productIdx") String productIdx){
            try {
               productService.getProduct(productIdx);
                return new BaseResponse<>("5");
            } catch (BaseException e) {
                return new BaseResponse<>(e.getStatus());
            } catch (IOException e) {
                e.printStackTrace();
                return new BaseResponse<>(null);
            }

        }

    @ResponseBody
    @GetMapping("/category")
    public BaseResponse<List<ProductDTO.GetProductList>> getCategory(@RequestBody ProductDTO.GetCategoryRes getCategoryRes){
        try {
            List<ProductDTO.GetProductList> getProductList = productService.getCategoryList(getCategoryRes);
            return new BaseResponse<>(getProductList);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/all")
    public BaseResponse<List<ProductDTO.GetProductList>> getAll(){
        try {
            List<ProductDTO.GetProductList> getProductList = productService.getAll();
            return new BaseResponse<>(getProductList);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /*
    * ์๋ฆฌ : review
    * */
    @ResponseBody
    @PostMapping("/review")
    public BaseResponse<String> postReview(Principal principal, @RequestBody ProductDTO.GetProductReview product){
        try {
            productService.postReview(principal, product);
            return new BaseResponse<>("๋ฆฌ๋ทฐ ์์ฑ์ ์๋ฃํ์ต๋๋ค.");
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/setLike")
    public BaseResponse<String> postLike(Principal principal, @RequestBody ProductDTO.PostsetLike product){
        try {
            productService.postLike(principal, product);
            return new BaseResponse<>("์ข์์ ๋ชฉ๋ก์ ์ถ๊ฐ๋์์ต๋๋ค.");
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
