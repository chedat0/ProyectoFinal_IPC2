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
