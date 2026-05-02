/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import otros.ConnectionMySQL;
import modelo.SolicitudCategoria;
import modelo.SolicitudHabilidad;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeffm
 */
public class SolicitudDAO {
    
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection con = null;
    
    public SolicitudDAO(){
        con = connMySQL.conectar();
    }
    
    public SolicitudHabilidad crearHabilidad(SolicitudHabilidad s) throws SQLException {
        String sql = "INSERT INTO solicitud_habilidad (freelancer_id, nombre, descripcion, estado) VALUES (?,?,?,'PENDIENTE')";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, s.getFreelancerId()); 
            ps.setString(2, s.getNombre()); 
            ps.setString(3, s.getDescripcion());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { 
                if (rs.next()) s.setId(rs.getInt(1)); 
            }
        }
        return s;
    }
    
    public SolicitudCategoria crearCategoria(SolicitudCategoria s) throws SQLException {
        String sql = "INSERT INTO solicitud_categoria (cliente_id, nombre, descripcion, estado) VALUES (?,?,?,'PENDIENTE')";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, s.getClienteId()); 
            ps.setString(2, s.getNombre()); 
            ps.setString(3, s.getDescripcion()); 
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { 
                if (rs.next()) s.setId(rs.getInt(1)); 
            }
        }
        return s;
    }
    
    public List<SolicitudHabilidad> obtenerHabilidadesPendientes() throws SQLException {
        String sql = "SELECT sh.*, u.nombre_completo AS freelancer_nombre FROM solicitud_habilidad sh JOIN freelancer f ON sh.freelancer_id = f.id JOIN usuario u ON f.usuario_id = u.id WHERE sh.estado='PENDIENTE' ORDER BY sh.fecha_solicitud DESC";
        List<SolicitudHabilidad> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql); 
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) { 
                SolicitudHabilidad s = new SolicitudHabilidad(); 
                s.setId(rs.getInt("id")); 
                s.setFreelancerId(rs.getInt("freelancer_id")); 
                s.setNombre(rs.getString("nombre")); 
                s.setDescripcion(rs.getString("descripcion")); 
                s.setEstado(rs.getString("estado")); 
                Timestamp fs = rs.getTimestamp("fecha_solicitud"); 
                if (fs != null) s.setFechaSolicitud(fs.toLocalDateTime()); 
                s.setFreelancerNombre(rs.getString("freelancer_nombre")); 
                list.add(s); }
        }
        return list;
    }
    
    public List<SolicitudCategoria> obtenerCategoriasPendientes() throws SQLException {
        String sql = "SELECT sc.*, u.nombre_completo AS cliente_nombre FROM solicitud_categoria sc JOIN cliente c ON sc.cliente_id = c.id JOIN usuario u ON c.usuario_id = u.id WHERE sc.estado='PENDIENTE' ORDER BY sc.fecha_solicitud DESC";
        List<SolicitudCategoria> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql); 
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) { 
                SolicitudCategoria s = new SolicitudCategoria(); 
                s.setId(rs.getInt("id")); 
                s.setClienteId(rs.getInt("cliente_id")); 
                s.setNombre(rs.getString("nombre")); 
                s.setDescripcion(rs.getString("descripcion")); 
                s.setEstado(rs.getString("estado")); 
                Timestamp fs = rs.getTimestamp("fecha_solicitud"); 
                if (fs != null) s.setFechaSolicitud(fs.toLocalDateTime()); 
                s.setClienteNombre(rs.getString("cliente_nombre")); 
                list.add(s); }
        }
        return list;
    }
    
    public void responderHabilidad(int id, int adminId, String estado) throws SQLException {
        String sql = "UPDATE solicitud_habilidad SET estado=?, admin_id=?, fecha_respuesta=NOW() WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado); 
            ps.setInt(2, adminId); 
            ps.setInt(3, id); 
            ps.executeUpdate();
        }
    }
    public void responderCategoria(int id, int adminId, String estado) throws SQLException {
        String sql = "UPDATE solicitud_categoria SET estado=?, admin_id=?, fecha_respuesta=NOW() WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado); 
            ps.setInt(2, adminId); 
            ps.setInt(3, id); 
            ps.executeUpdate();
        }
    }
}
