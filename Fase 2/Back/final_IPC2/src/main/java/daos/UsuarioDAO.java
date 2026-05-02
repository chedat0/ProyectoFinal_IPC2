/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import otros.ConnectionMySQL;
import modelo.Usuario;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author jeffm
 */
public class UsuarioDAO {
    
    ConnectionMySQL connMySQL = new ConnectionMySQL ();
    Connection conn = null;
        
    public UsuarioDAO() {
        conn = connMySQL.conectar();
    }
    public Usuario ingresar(Usuario u) throws SQLException {
                
        String sql = "INSERT INTO usuario (nombre_completo, username, correo, password_hash, telefono, direccion, cui, fecha_nacimiento, tipo_usuario, activo) VALUES (?,?,?,?,?,?,?,?,?,TRUE)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNombreCompleto());
            ps.setString(2, u.getUsername());
            ps.setString(3, u.getCorreo());
            ps.setString(4, u.getPasswordHash());
            ps.setString(5, u.getTelefono());
            ps.setString(6, u.getDireccion());
            ps.setString(7, u.getCui());
            ps.setDate(8, Date.valueOf(u.getFechaNacimiento()));
            ps.setString(9, u.getTipoUsuario());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) u.setId(rs.getInt(1));
            }
        }
        return u;
    }

    public Usuario obtenerPorUsuario(String username) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public Usuario obtenerPorCorreo(String correo) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE correo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public Usuario obtenerPorId(int id) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public boolean existePorUsuario(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public boolean existePorCorreo(String correo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario WHERE correo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public boolean existePorDPI(String cui) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario WHERE cui = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cui);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public void actualizarUltimaSesion(int id) throws SQLException {
        String sql = "UPDATE usuario SET ultima_sesion = NOW() WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void toggleActivo(int id, boolean activo) throws SQLException {
        String sql = "UPDATE usuario SET activo = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, activo);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public List<Usuario> obtenerTodos(String tipo) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM usuario WHERE 1=1");
        if (tipo != null && !tipo.isEmpty()) sql.append(" AND tipo_usuario = ?");
        sql.append(" ORDER BY fecha_registro DESC");
        List<Usuario> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (tipo != null && !tipo.isEmpty()) ps.setString(1, tipo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    private Usuario map(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setNombreCompleto(rs.getString("nombre_completo"));
        u.setUsername(rs.getString("username"));
        u.setCorreo(rs.getString("correo"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setTelefono(rs.getString("telefono"));
        u.setDireccion(rs.getString("direccion"));
        u.setCui(rs.getString("cui"));
        Date fn = rs.getDate("fecha_nacimiento");
        if (fn != null) u.setFechaNacimiento(fn.toLocalDate());
        u.setTipoUsuario(rs.getString("tipo_usuario"));
        u.setActivo(rs.getBoolean("activo"));
        Timestamp fr = rs.getTimestamp("fecha_registro");
        if (fr != null) u.setFechaRegistro(fr.toLocalDateTime());
        Timestamp us = rs.getTimestamp("ultima_sesion");
        if (us != null) u.setUltimaSesion(us.toLocalDateTime());
        return u;
    }
}
