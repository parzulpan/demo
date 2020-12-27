package cn.parzulpan.domain;

import java.util.Date;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 产品实体类
 */

public class Product {
    private Integer id; // 无意义 主键
    private String productNumber;   // 产品编号 唯一
    private String productName; // 产品名称
    private String departureCity;   // 出发城市
    private Date departureDate; // 出发时间
    private Float productPrice; // 产品价格
    private String productDesc; // 产品描述
    private Integer productStatus;  // 产品状态 0代表关闭 1代表开启

    public Product() {
    }

    public Product(Integer id, String productNumber, String productName, String departureCity, Date departureDate,
                   Float productPrice, String productDesc, Integer productStatus) {
        this.id = id;
        this.productNumber = productNumber;
        this.productName = productName;
        this.departureCity = departureCity;
        this.departureDate = departureDate;
        this.productPrice = productPrice;
        this.productDesc = productDesc;
        this.productStatus = productStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public Float getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Float productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public Integer getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(Integer productStatus) {
        this.productStatus = productStatus;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", productNumber='" + productNumber + '\'' +
                ", productName='" + productName + '\'' +
                ", departureCity='" + departureCity + '\'' +
                ", departureDate=" + departureDate +
                ", productPrice=" + productPrice +
                ", productDesc='" + productDesc + '\'' +
                ", productStatus=" + productStatus +
                '}';
    }
}
