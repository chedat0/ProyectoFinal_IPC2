/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import otros.ConnectionMySQL;
import modelo.Propuesta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeffm
 */
public class PropuestaDAO {
    
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection con = null;
    
    public PropuestaDAO(){
        con = connMySQL.conectar();
    }
    
    public Propuesta ingresar(Propuesta p) throws SQLException {
        String sql = "INSERT INTO propuesta (proyecto_id, freelancer_id, monto_ofertado, plazo_dias, carta_presentacion, estado) VALUES (?,?,?,?,?,'PENDIENTE')";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getProyectoId());
            ps.setInt(2, p.getFreelancerId());
            ps.setDouble(3, p.getMontoOfertado());
            ps.setInt(4, p.getPlazoDias());
            ps.setString(5, p.getCartaPresentacion());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) p.setId(rs.getInt(1)); }
        }
        return p;
    }
    public Propuesta obtenerPorId(int id) throws SQLException {
        String sql = "SELECT pr.*, u.nombre_completo AS freelancer_nombre, f.calificacion_promedio AS freelancer_calificacion, proy.titulo AS proyecto_titulo FROM propuesta pr JOIN freelancer f ON pr.freelancer_id = f.id JOIN usuario u ON f.usuario_id = u.id JOIN proyecto proy ON pr.proyecto_id = proy.id WHERE pr.id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return map(rs); }
        }
        return null;
    }
    public List<Propuesta> obtenerPorProyecto(int proyectoId) throws SQLException {
        String sql = "SELECT pr.*, u.nombre_completo AS freelancer_nombre, f.calificacion_promedio AS freelancer_calificacion, proy.titulo AS proyecto_titulo FROM propuesta pr JOIN freelancer f ON pr.freelancer_id = f.id JOIN usuario u ON f.usuario_id = u.id JOIN proyecto proy ON pr.proyecto_id = proy.id WHERE pr.proyecto_id = ? ORDER BY pr.fecha_envio DESC";
        List<Propuesta> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, proyectoId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }
    public List<Propuesta> obtenerporFreelancer(int freelancerId) throws SQLException {
        String sql = "SELECT pr.*, u.nombre_completo AS freelancer_nombre, f.calificacion_promedio AS freelancer_calificacion, proy.titulo AS proyecto_titulo FROM propuesta pr JOIN freelancer f ON pr.freelancer_id = f.id JOIN usuario u ON f.usuario_id = u.id JOIN proyecto proy ON pr.proyecto_id = proy.id WHERE pr.freelancer_id = ? ORDER BY pr.fecha_envio DESC";
        List<Propuesta> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, freelancerId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }
    public boolean existePropuesta(int proyectoId, int freelancerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM propuesta WHERE proyecto_id = ? AND freelancer_id = ? AND estado NOT IN ('RETIRADA','RECHAZADA')";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, proyectoId); 
            ps.setInt(2, freelancerId);
            try (ResultSet rs = ps.executeQuery()) { 
                if (rs.next()) 
                    return rs.getInt(1) > 0; 
            }
        }
        return false;
    }
    public void actualizarEstado(int id, String estado) throws SQLException {
        String sql = "UPDATE propuesta SET estado = ?, fecha_respuesta = NOW() WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado); 
            ps.setInt(2, id); 
            ps.executeUpdate();
        }
    }
    private Propuesta map(ResultSet rs) throws SQLException {
        Propuesta p = new Propuesta();
        p.setId(rs.getInt("id"));
        p.setProyectoId(rs.getInt("proyecto_id")); 
        p.setFreelancerId(rs.getInt("freelancer_id"));
        p.setMontoOfertado(rs.getDouble("monto_ofertado")); 
        p.setPlazoDias(rs.getInt("plazo_dias"));
        p.setCartaPresentacion(rs.getString("carta_presentacion")); 
        p.setEstado(rs.getString("estado"));
        Timestamp fe = rs.getTimestamp("fecha_envio"); if (fe != null) p.setFechaEnvio(fe.toLocalDateTime());
        Timestamp fr = rs.getTimestamp("fecha_respuesta"); if (fr != null) p.setFechaRespuesta(fr.toLocalDateTime());
        p.setFreelancerNombre(rs.getString("freelancer_nombre"));
        p.setFreelancerCalificacion(rs.getDouble("freelancer_calificacion"));
        p.setProyectoTitulo(rs.getString("proyecto_titulo"));
        return p;
    }
}
