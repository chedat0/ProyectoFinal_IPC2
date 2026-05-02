/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import otros.ConnectionMySQL;
import modelo.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeffm
 */
public class CategoriaDAO {
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection con = null;
    
    public CategoriaDAO(){
        con = connMySQL.conectar();
    }
    
    public Categoria ingresar(Categoria c) throws SQLException {
        String sql = "INSERT INTO categoria (nombre, descripcion, activa) VALUES (?,?,TRUE)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNombre()); 
            ps.setString(2, c.getDescripcion()); 
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { 
                if (rs.next()) 
                    c.setId(rs.getInt(1)); 
            }
        }
        return c;
    }
    
    public Categoria obtenerPorId(int id) throws SQLException {
        String sql = "SELECT * FROM categoria WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id); 
            try (ResultSet rs = ps.executeQuery()) { 
                if (rs.next()) 
                    return map(rs); 
            }
        }
        return null;
    }
    
    public List<Categoria> obtenerTodas(Boolean soloActivas) throws SQLException {
        String sql = soloActivas != null && soloActivas ? "SELECT * FROM categoria WHERE activa = TRUE ORDER BY nombre" : "SELECT * FROM categoria ORDER BY nombre";
        List<Categoria> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql); 
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }
    
    public void actualizar(Categoria c) throws SQLException {
        String sql = "UPDATE categoria SET nombre=?, descripcion=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre()); 
            ps.setString(2, c.getDescripcion()); 
            ps.setInt(3, c.getId()); 
            ps.executeUpdate();
        }
    }
    
    public void toggleActiva(int id, boolean activa) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("UPDATE categoria SET activa=? WHERE id=?")) {
            ps.setBoolean(1, activa);
            ps.setInt(2, id); 
            ps.executeUpdate();
        }
    }
    
    private Categoria map(ResultSet rs) throws SQLException {
        Categoria c = new Categoria();
        c.setId(rs.getInt("id")); 
        c.setNombre(rs.getString("nombre")); 
        c.setDescripcion(rs.getString("descripcion")); 
        c.setActiva(rs.getBoolean("activa"));
        Timestamp fc = rs.getTimestamp("fecha_creacion"); 
        if (fc != null) c.setFechaCreacion(fc.toLocalDateTime());
        return c;
    }
}
