package dao;

import model.Medicine;
import java.sql.*;
import java.util.*;

public class MedicineDAO {

    public Medicine addMedicine(Medicine medicine) throws SQLException {
        String sql = "INSERT INTO medicines(name, unit, price, stock, min_stock, expiry_date, status) " +
                     "VALUES (?,?,?,?,?,?,?) RETURNING id, created_at, updated_at";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, medicine.getName());
            ps.setString(2, medicine.getUnit());
            ps.setDouble(3, medicine.getPrice());
            ps.setInt(4, medicine.getStock());
            ps.setInt(5, medicine.getMin_stock());
            ps.setDate(6, java.sql.Date.valueOf(medicine.getexpiry_date()));
            ps.setString(7, medicine.getStatus() != null ? medicine.getStatus() : "active");

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    medicine.setId(rs.getInt("id"));
                    medicine.setCreated_at(rs.getTimestamp("created_at").toString());
                    medicine.setUpdated_at(rs.getTimestamp("updated_at").toString());
                }
            }
        }
        return medicine;
    }

    public List<Medicine> getAllMedicines() throws SQLException {
        String sql = "SELECT * FROM medicines ORDER BY id";
        List<Medicine> medicines = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                medicines.add(mapRowToMedicine(rs));
            }
        }
        return medicines;
    }

    public List<Medicine> getActiveMedicines() throws SQLException {
        String sql = "SELECT * FROM medicines WHERE status='active' ORDER BY name";
        List<Medicine> medicines = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                medicines.add(mapRowToMedicine(rs));
            }
        }
        return medicines;
    }

    public boolean updateMedicine(int id, Medicine medicine) throws SQLException {
        String sql = "UPDATE medicines SET name=?, unit=?, price=?, stock=?, min_stock=?, expiry_date=?, status=?, updated_at=CURRENT_TIMESTAMP " +
                     "WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, medicine.getName());
            ps.setString(2, medicine.getUnit());
            ps.setDouble(3, medicine.getPrice());
            ps.setInt(4, medicine.getStock());
            ps.setInt(5, medicine.getMin_stock());
            ps.setDate(6, java.sql.Date.valueOf(medicine.getexpiry_date()));
            ps.setString(7, medicine.getStatus());
            ps.setInt(8, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deactivateMedicine(int id) throws SQLException {
        String sql = "UPDATE medicines SET status='inactive', updated_at=CURRENT_TIMESTAMP WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Medicine> getLowStockMedicines() throws SQLException {
        String sql = "SELECT * FROM medicines WHERE stock < min_stock AND status='active' ORDER BY stock ASC";
        List<Medicine> medicines = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                medicines.add(mapRowToMedicine(rs));
            }
        }
        return medicines;
    }

    public List<Medicine> getNearExpiryMedicines() throws SQLException {
        String sql = "SELECT * FROM medicines WHERE expiry_date BETWEEN CURRENT_DATE AND (CURRENT_DATE + INTERVAL '30 days') " +
                     "AND status='active' ORDER BY expiry_date ASC";
        List<Medicine> medicines = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                medicines.add(mapRowToMedicine(rs));
            }
        }
        return medicines;
    }

    public Medicine getMedicineById(int id) throws SQLException {
        String sql = "SELECT * FROM medicines WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToMedicine(rs);
                }
            }
        }
        return null;
    }

    public boolean deleteMedicine(int id) throws SQLException {
        String sql = "DELETE FROM medicines WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Medicine mapRowToMedicine(ResultSet rs) throws SQLException {
        Medicine m = new Medicine();
        m.setId(rs.getInt("id"));
        m.setName(rs.getString("name"));
        m.setUnit(rs.getString("unit"));
        m.setPrice(rs.getDouble("price"));
        m.setStock(rs.getInt("stock"));
        m.setMin_stock(rs.getInt("min_stock"));
        java.sql.Date d = rs.getDate("expiry_date");
        m.setexpiry_date(d != null ? d.toString() : null);
        m.setStatus(rs.getString("status"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        m.setCreated_at(createdAt != null ? createdAt.toString() : null);
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        m.setUpdated_at(updatedAt != null ? updatedAt.toString() : null);
        return m;
    }
}