package bank.transaction.service.expedition;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import javax.inject.Singleton;

@Singleton
public class OrderDetail {

    public OrderDetail(){

    }
    public OrderDetail(int id, String sku, String uom, String info, String slug, String image, String weight, String category, String subCategory, String childCategory, int notWholesalePrice, String name, String code, int price, int quantity, int total, Order order
                       ){
        this.order = order;
        this.id = id;
        this.sku = sku;
        this.uom = uom;
        this.info = info;
        this.slug = slug;
        this.image = image;
        this.weight = weight;
        this.category = category;
        this.childCategory = childCategory;
        this.notWholesalePrice = notWholesalePrice;
        this.name = name;
        this.code = code;
        this.price = price;
        this.quantity = quantity;
        this.total = total;
        this.subCategory = subCategory;
    }

    @JsonIgnore
    @JsonProperty("id")
    private int id;

    @JsonIgnore
    @JsonProperty("sku")
    private String sku;

    @JsonIgnore
    @JsonProperty("uom")
    private String uom;

    @JsonIgnore
    @JsonProperty("info")
    private String info;

    @JsonIgnore
    @JsonProperty("slug")
    private String slug;

    @JsonIgnore
    @JsonProperty("image")
    private String image;

    @JsonIgnore
    @JsonProperty("weight")
    private String weight;

    @JsonIgnore
    @JsonProperty("category")
    private String category;

//    @JsonIgnore
    @JsonProperty("child_category")
    private String childCategory;

    @JsonProperty("sub_category")
    private String subCategory;

//    @JsonIgnore
    @JsonProperty("not_wholesale_price")
    private int notWholesalePrice;

    @JsonProperty("name")
    private String name;

    @JsonProperty("code")
    private String code;

    @JsonProperty("price")
    private int price;

    @JsonProperty("quantity")
    private int quantity;

    @JsonAlias("total")
    @JsonProperty("total_price")
    private int total;

    private Order order;

    public void setId(int id) { this.id = id; }
    public int getId() { return id; }

    public void setSku(String sku) { this.sku = sku; }
    public String getSku() { return sku; }

    public void setUom(String uom) { this.uom = uom; }
    public String getUom() { return uom; }

    public void setInfo(String info) { this.info = info; }
    public String getInfo() { return info; }

    public void setSlug(String slug) { this.slug = slug; }
    public String getSlug() { return slug; }

    public void setImage(String image) { this.image = image; }
    public String getImage() { return image; }

    public void setWeight(String weight) { this.weight = weight; }
    public String getWeight() { return weight; }

    public void setCategory(String category) { this.category = category; }
    public String getCategory() { return category; }

    public void setChildCategory(String childCategory) { this.childCategory = childCategory; }
    public String getChildCategory() { return childCategory; }

    public void setNotWholesalePrice(int notWholesalePrice) { this.notWholesalePrice = notWholesalePrice; }
    public int getNotWholesalePrice() { return notWholesalePrice; }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public void setCode(String code) { this.code = code; }
    public String getCode() { return code; }

    public void setPrice(int price) { this.price = price; }
    public int getPrice() { return price; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getQuantity() { return quantity; }

    public void setTotal(int total) { this.total = total; }
    public int getTotal() { return total; }

    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }
    public String getSubCategory() { return subCategory; }

    public void setOrder(Order order) { this.order = order; }
    public Order getOrder() { return order; }

    @Override
    public String toString() {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
