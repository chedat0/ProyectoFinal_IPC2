/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import otros.ConnectionMySQL;
import modelo.Administrador;

import java.sql.*;

/**
 *
 * @author jeffm
 */
public class AdministradorDAO {
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection conn = null;
    
    public AdministradorDAO(){
        conn = connMySQL.conectar();
    }
    public Administrador obtenerPorUsuarioId(int usuarioId) throws SQLException {
        String sql = "SELECT a.*, u.username, u.nombre_completo, u.correo FROM administrador a " +
                     "JOIN usuario u ON a.usuario_id = u.id WHERE a.usuario_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }
    
    public java.util.List<Administrador> obtenerTodos() throws SQLException {
        String sql = "SELECT a.*, u.username, u.nombre_completo, u.correo FROM administrador a " +
                     "JOIN usuario u ON a.usuario_id = u.id ORDER BY a.id";
        java.util.List<Administrador> list = new java.util.ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public void actualizarAdmin(int usuarioId, String nombreCompleto, String correo,
                                String telefono, String nivelAcceso, String passwordHash) throws SQLException {       
        StringBuilder sql = new StringBuilder("UPDATE usuario SET nombre_completo=?, correo=?, telefono=?");
        if (passwordHash != null) sql.append(", password_hash=?");
        sql.append(" WHERE id=?");
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setString(1, nombreCompleto);
            ps.setString(2, correo);
            ps.setString(3, telefono);
            if (passwordHash != null) {
                ps.setString(4, passwordHash);
                ps.setInt(5, usuarioId);
            } else {
                ps.setInt(4, usuarioId);
            }
            ps.executeUpdate();
        }    
        String sqlAdmin = "UPDATE administrador SET nivel_acceso=? WHERE usuario_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sqlAdmin)) {
            ps.setString(1, nivelAcceso);
            ps.setInt(2, usuarioId);
            ps.executeUpdate();
        }
    }

    public Administrador ingresar(int usuarioId, String nivelAcceso) throws SQLException {
        String sql = "INSERT INTO administrador (usuario_id, nivel_acceso) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, usuarioId);
            ps.setString(2, nivelAcceso != null ? nivelAcceso : "ESTANDAR");
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return obtenerPorUsuarioId(usuarioId);
        }
        return null;
    }

    private Administrador mapRow(ResultSet rs) throws SQLException {
        Administrador a = new Administrador();
        a.setId(rs.getInt("id"));
        a.setUsuarioId(rs.getInt("usuario_id"));
        a.setNivelAcceso(rs.getString("nivel_acceso"));
        a.setNombreCompleto(rs.getString("nombre_completo"));
        a.setUsername(rs.getString("username"));
        a.setCorreo(rs.getString("correo"));
        return a;
    }
}
