/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import otros.ConnectionMySQL;
import modelo.Contrato;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeffm
 */
public class ContratoDAO {
    
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection con = null;
    
    public ContratoDAO(){
        con = connMySQL.conectar();
    }
    
    public Contrato ingresar(Contrato c) throws SQLException {
        String sql = "INSERT INTO contrato (propuesta_id, proyecto_id, cliente_id, freelancer_id, monto, porcentaje_comision, comision_monto, estado) VALUES (?,?,?,?,?,?,?,'ACTIVO')";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getPropuestaId()); 
            ps.setInt(2, c.getProyectoId()); 
            ps.setInt(3, c.getClienteId());
            ps.setInt(4, c.getFreelancerId()); 
            ps.setDouble(5, c.getMonto()); 
            ps.setDouble(6, c.getPorcentajeComision());
            ps.setDouble(7, c.getComisionMonto()); 
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { 
                if (rs.next()) c.setId(rs.getInt(1)); 
            }
        }
        return c;
    }
    
    public Contrato obtenerPorId(int id) throws SQLException {
        String sql = buildSelect() + " WHERE c.id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id); 
            try (ResultSet rs = ps.executeQuery()) { 
                if (rs.next()) return map(rs); 
            }
        }
        return null;
    }
    
    public Contrato obtenerPorProyecto(int proyectoId) throws SQLException {
        String sql = buildSelect() + " WHERE c.proyecto_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, proyectoId); 
            try (ResultSet rs = ps.executeQuery()) { 
                if (rs.next()) return map(rs); 
            }
        }
        return null;
    }
    public List<Contrato> obtenerPorCliente(int clienteId, String estado) throws SQLException {
        StringBuilder sql = new StringBuilder(buildSelect() + " WHERE c.cliente_id = ?");
        if (estado != null) sql.append(" AND c.estado = ?");
        sql.append(" ORDER BY c.fecha_inicio DESC");
        List<Contrato> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            ps.setInt(1, clienteId); 
            if (estado != null) 
            ps.setString(2, estado);
            try (ResultSet rs = ps.executeQuery()) { 
                while (rs.next()) list.add(map(rs)); 
            }
        }
        return list;
    }
    public List<Contrato> obtenerPorFreelancer(int freelancerId, String estado) throws SQLException {
        StringBuilder sql = new StringBuilder(buildSelect() + " WHERE c.freelancer_id = ?");
        if (estado != null) sql.append(" AND c.estado = ?");
        sql.append(" ORDER BY c.fecha_inicio DESC");
        List<Contrato> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            ps.setInt(1, freelancerId); 
            if (estado != null) ps.setString(2, estado);
            try (ResultSet rs = ps.executeQuery()) { 
                while (rs.next()) list.add(map(rs)); 
            }
        }
        return list;
    }
    
    public void completar(int id) throws SQLException {
        String sql = "UPDATE contrato SET estado='COMPLETADO', fecha_fin=NOW() WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id); ps.executeUpdate();
        }
    }
    public void cancelar(int id, String motivo) throws SQLException {
        String sql = "UPDATE contrato SET estado='CANCELADO', fecha_fin=NOW(), motivo_cancelacion=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, motivo); 
            ps.setInt(2, id); 
            ps.executeUpdate();
        }
    }
    private String buildSelect() {
        return "SELECT c.*, p.titulo AS proyecto_titulo, uc.nombre_completo AS cliente_nombre, uf.nombre_completo AS freelancer_nombre FROM contrato c JOIN proyecto p ON c.proyecto_id = p.id JOIN cliente cl ON c.cliente_id = cl.id JOIN usuario uc ON cl.usuario_id = uc.id JOIN freelancer fl ON c.freelancer_id = fl.id JOIN usuario uf ON fl.usuario_id = uf.id";
    }
    private Contrato map(ResultSet rs) throws SQLException {
        Contrato c = new Contrato();
        c.setId(rs.getInt("id")); 
        c.setPropuestaId(rs.getInt("propuesta_id")); 
        c.setProyectoId(rs.getInt("proyecto_id"));
        c.setClienteId(rs.getInt("cliente_id")); 
        c.setFreelancerId(rs.getInt("freelancer_id"));
        c.setMonto(rs.getDouble("monto")); 
        c.setPorcentajeComision(rs.getDouble("porcentaje_comision"));
        c.setComisionMonto(rs.getDouble("comision_monto")); 
        c.setEstado(rs.getString("estado"));
        c.setMotivoCancelacion(rs.getString("motivo_cancelacion"));
        Timestamp fi = rs.getTimestamp("fecha_inicio"); if (fi != null) c.setFechaInicio(fi.toLocalDateTime());
        Timestamp ff = rs.getTimestamp("fecha_fin"); if (ff != null) c.setFechaFin(ff.toLocalDateTime());
        c.setProyectoTitulo(rs.getString("proyecto_titulo")); 
        c.setClienteNombre(rs.getString("cliente_nombre")); 
        c.setFreelancerNombre(rs.getString("freelancer_nombre"));
        return c;
    }
}
