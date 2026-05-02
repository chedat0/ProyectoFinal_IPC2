/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import otros.ConnectionMySQL;
import modelo.Habilidad;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeffm
 */
public class HabilidadDAO {
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection conn = null;
    
    public HabilidadDAO(){
        conn = connMySQL.conectar();
    }
    
    public Habilidad ingresar(Habilidad h) throws SQLException {
        String sql = "INSERT INTO habilidad (categoria_id, nombre, descripcion, activa) VALUES (?,?,?,TRUE)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, h.getCategoriaId()); 
            ps.setString(2, h.getNombre()); 
            ps.setString(3, h.getDescripcion()); 
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { 
                if (rs.next()) h.setId(rs.getInt(1)); 
            }
        }
        return h;
    }
    
    public List<Habilidad> obtenerTodas(Integer categoriaId, Boolean soloActivas) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT h.*, c.nombre AS categoria_nombre FROM habilidad h JOIN categoria c ON h.categoria_id = c.id WHERE 1=1");
        if (categoriaId != null) sql.append(" AND h.categoria_id = ?");
        if (soloActivas != null && soloActivas) sql.append(" AND h.activa = TRUE");
        sql.append(" ORDER BY h.nombre");
        List<Habilidad> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int i = 1; if (categoriaId != null) ps.setInt(i++, categoriaId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }
    
    public Habilidad obtenerPorId(int id) throws SQLException {
        String sql = "SELECT h.*, c.nombre AS categoria_nombre FROM habilidad h JOIN categoria c ON h.categoria_id = c.id WHERE h.id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); 
            try (ResultSet rs = ps.executeQuery()) { 
                if (rs.next()) 
                    return map(rs); 
            }
        }
        return null;
    }
    
    public void actualizar(Habilidad h) throws SQLException {
        String sql = "UPDATE habilidad SET nombre=?, descripcion=?, categoria_id=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, h.getNombre()); 
            ps.setString(2, h.getDescripcion()); 
            ps.setInt(3, h.getCategoriaId()); 
            ps.setInt(4, h.getId()); 
            ps.executeUpdate();
        }
    }
    public void toggleActiva(int id, boolean activa) throws SQLException {
        String sql = "UPDATE habilidad SET activa=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, activa); 
            ps.setInt(2, id); 
            ps.executeUpdate();
        }
    }
    private Habilidad map(ResultSet rs) throws SQLException {
        Habilidad h = new Habilidad();
        h.setId(rs.getInt("id")); 
        h.setCategoriaId(rs.getInt("categoria_id")); 
        h.setNombre(rs.getString("nombre")); 
        h.setDescripcion(rs.getString("descripcion")); 
        h.setActiva(rs.getBoolean("activa"));
        try { h.setCategoriaNombre(rs.getString("categoria_nombre")); } catch (Exception ignored) {}
        Timestamp fc = rs.getTimestamp("fecha_creacion"); if (fc != null) h.setFechaCreacion(fc.toLocalDateTime());
        return h;
    }
}
