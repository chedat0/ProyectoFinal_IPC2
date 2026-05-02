/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import otros.ConnectionMySQL;
import modelo.Calificacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeffm
 */
public class CalificacionDAO {
    
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection con = null;
    
    public CalificacionDAO(){
        con = connMySQL.conectar();
    }
    
    public Calificacion ingresar(Calificacion c) throws SQLException {
        String sql = "INSERT INTO calificacion (contrato_id, cliente_id, freelancer_id, estrellas, comentario) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getContratoId()); 
            ps.setInt(2, c.getClienteId()); 
            ps.setInt(3, c.getFreelancerId()); 
            ps.setInt(4, c.getEstrellas()); 
            ps.setString(5, c.getComentario());
            ps.executeUpdate(); 
            try (ResultSet rs = ps.getGeneratedKeys()) { 
                if (rs.next()) c.setId(rs.getInt(1)); 
            }
        }
        return c;
    }
    
    public boolean existsPorContrato(int contratoId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM calificacion WHERE contrato_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, contratoId); 
            try (ResultSet rs = ps.executeQuery()) { 
                if (rs.next()) return rs.getInt(1) > 0; 
            }
        }
        return false;
    }
    
    public List<Calificacion> obtenerPorFreelancer(int freelancerId) throws SQLException {
        String sql = "SELECT cal.*, u.nombre_completo AS cliente_nombre, p.titulo AS proyecto_titulo FROM calificacion cal JOIN cliente cl ON cal.cliente_id = cl.id JOIN usuario u ON cl.usuario_id = u.id JOIN contrato c ON cal.contrato_id = c.id JOIN proyecto p ON c.proyecto_id = p.id WHERE cal.freelancer_id = ? ORDER BY cal.fecha DESC";
        List<Calificacion> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, freelancerId); 
            try (ResultSet rs = ps.executeQuery()) { 
                while (rs.next()) 
                    list.add(map(rs)); 
            }
        }
        return list;
    }
    private Calificacion map(ResultSet rs) throws SQLException {
        Calificacion c = new Calificacion();
        c.setId(rs.getInt("id")); 
        c.setContratoId(rs.getInt("contrato_id")); 
        c.setClienteId(rs.getInt("cliente_id")); 
        c.setFreelancerId(rs.getInt("freelancer_id")); 
        c.setEstrellas(rs.getInt("estrellas")); 
        c.setComentario(rs.getString("comentario"));
        Timestamp f = rs.getTimestamp("fecha"); if (f != null) c.setFecha(f.toLocalDateTime());
        try { c.setClienteNombre(rs.getString("cliente_nombre")); } catch (Exception ignored) {}
        try { c.setProyectoTitulo(rs.getString("proyecto_titulo")); } catch (Exception ignored) {}
        return c;
    }
}
