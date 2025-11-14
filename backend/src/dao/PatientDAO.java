package dao;

import model.Patient;
import java.sql.*;
import java.util.*;

public class PatientDAO {

    public Patient addPatient(Patient patient) throws SQLException {
        String sql = "INSERT INTO patients(full_name, phone, address) " +
                     "VALUES (?,?,?) RETURNING id, created_at, updated_at";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patient.getFull_name());
            ps.setString(2, patient.getPhone());
            ps.setString(3, patient.getAddress());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    patient.setId(rs.getInt("id"));
                    patient.setCreated_at(rs.getTimestamp("created_at").toString());
                    patient.setUpdated_at(rs.getTimestamp("updated_at").toString());
                }
            }
        }
        return patient;
    }

    public List<Patient> getAllPatients() throws SQLException {
        String sql = "SELECT * FROM patients ORDER BY full_name";
        List<Patient> patients = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                patients.add(mapRowToPatient(rs));
            }
        }
        return patients;
    }

    public boolean updatePatient(int id, Patient patient) throws SQLException {
        String sql = "UPDATE patients SET full_name=?, phone=?, address=?, updated_at=CURRENT_TIMESTAMP " +
                     "WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patient.getFull_name());
            ps.setString(2, patient.getPhone());
            ps.setString(3, patient.getAddress());
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        }
    }

    public Patient getPatientById(int id) throws SQLException {
        String sql = "SELECT * FROM patients WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPatient(rs);
                }
            }
        }
        return null;
    }

    public boolean deletePatient(int id) throws SQLException {
        String sql = "DELETE FROM patients WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Patient> searchByName(String name) throws SQLException {
        String sql = "SELECT * FROM patients WHERE LOWER(full_name) LIKE LOWER(?) ORDER BY full_name";
        List<Patient> patients = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    patients.add(mapRowToPatient(rs));
                }
            }
        }
        return patients;
    }

    private Patient mapRowToPatient(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setId(rs.getInt("id"));
        p.setFull_name(rs.getString("full_name"));
        p.setPhone(rs.getString("phone"));
        p.setAddress(rs.getString("address"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        p.setCreated_at(createdAt != null ? createdAt.toString() : null);
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        p.setUpdated_at(updatedAt != null ? updatedAt.toString() : null);
        return p;
    }
}