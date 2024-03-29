package com.example.getIt.product.DTO;

import lombok.*;
import org.json.JSONObject;

import java.util.List;

@NoArgsConstructor
public class ProductDTO {

    @Setter
    @Getter
    @AllArgsConstructor
    @Builder
    public static class GetProduct {
        private Long productIdx;
        private String name;
        private String brand;
        private String type;
        private String image;
        private String lowestprice;
        private String productId;
        private String productUrl;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class GetProductReview {
        private String productId;
        private String review;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class GetCategoryRes {
        private String type;
        private String requirement;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class GetProductList {
        private String imgUrl;
        private String name;
        private String lprice;
        private String productId;

        public GetProductList(JSONObject jsonObject) {
            this.imgUrl = jsonObject.getString("image");
            this.name = jsonObject.getString("title");
            this.lprice = jsonObject.getString("lprice");
            this.productId = jsonObject.getString("productId");
        }
    }
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PostsetLike {
        private String productId;
    }
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class GetIsLike {
        private Boolean isLike;
    }

    @Data
    @Getter
    @Setter
    @ToString
    public static class GetDetail {
        private String productIdx;
        private String lprice;
        private String name;
        private String link;
        private List<String> photolist;
        private String brand;
        private String date;
        private String cpu;
        private String cpurate;
        private String core;
        private String size;
        private String ram;
        private String weight;
        private String type;
        private String innermemory; // 내장메모리
        private String communication; // 통신 규격
        private String os; // 운영 체제
        private String ssd;
        private String hdd;
        private String output; // 출력
        private String terminal; // 단자
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReviewList{
        private Long reviewIdx;
        private String productId;
        private String nickName;
        private String review;
        private String profileImgUrl;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Recommend{
        private String keyword;

    }
    @Getter
    @NoArgsConstructor
    public static class GetSpecResultList {
        private String title;
        private String link;
        private String image;
        private String lprice;
        private String brand;


        public GetSpecResultList(JSONObject itemJson) {
            this.title = itemJson.getString("title");
            this.link = itemJson.getString("link");
            this.image = itemJson.getString("image");
            this.lprice = itemJson.getString("lprice");
            this.brand = itemJson.getString("maker");
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class GetProductItemList{
        private List<GetProductList> products;
    }
    @Getter
    @Setter
    @AllArgsConstructor
    public static class GetRecommItemList{
        private String topic;
        private List<GetProductList> products;
    }
}
