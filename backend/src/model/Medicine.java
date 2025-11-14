package model;

public class Medicine {
    private int id;
    private String name;
    private String unit;
    private double price;
    private int stock;
    private int min_stock;       // wajib
    private String expiry_date;  // wajib
    private String status;
    private String created_at;
    private String updated_at;

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getMin_stock() { return min_stock; }      // penting
    public void setMin_stock(int min_stock) { this.min_stock = min_stock; }

    public String getexpiry_date() { return expiry_date; }  // penting
    public void setexpiry_date(String expiry_date) { this.expiry_date = expiry_date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }

    public String getUpdated_at() { return updated_at; }
    public void setUpdated_at(String updated_at) { this.updated_at = updated_at; }
}
