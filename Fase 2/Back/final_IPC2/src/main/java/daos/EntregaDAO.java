/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import otros.ConnectionMySQL;
import modelo.Entrega;
import modelo.EntregaArchivo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeffm
 */
public class EntregaDAO {
    
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection con = null;
    
    public EntregaDAO(){
        con = connMySQL.conectar();
    }
    
    public Entrega ingresar(Entrega e, List<EntregaArchivo> archivos) throws SQLException {
        try (Connection con = connMySQL.conectar()) {
            con.setAutoCommit(false);
            try {
                String sql = "INSERT INTO entrega (contrato_id, descripcion, estado) VALUES (?,?,'PENDIENTE')";
                try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, e.getContratoId()); 
                    ps.setString(2, e.getDescripcion()); 
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) { 
                        if (rs.next()) e.setId(rs.getInt(1)); 
                    }
                }
                if (archivos != null) {
                    String sqlA = "INSERT INTO entrega_archivo (entrega_id, url_archivo, nombre_archivo) VALUES (?,?,?)";
                    try (PreparedStatement ps = con.prepareStatement(sqlA)) {
                        for (EntregaArchivo a : archivos) {
                            ps.setInt(1, e.getId()); 
                            ps.setString(2, a.getUrlArchivo()); 
                            ps.setString(3, a.getNombreArchivo()); 
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }
                }
                con.commit();
            } catch (SQLException ex) { con.rollback(); throw ex; } finally { con.setAutoCommit(true); }
        }
        return e;
    }
    public List<Entrega> obtenerPorContrato(int contratoId) throws SQLException {
        String sql = "SELECT * FROM entrega WHERE contrato_id = ? ORDER BY fecha_entrega DESC";
        List<Entrega> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, contratoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { Entrega e = map(rs); e.setArchivos(getArchivos(e.getId(), con)); list.add(e); }
            }
        }
        return list;
    }
    public Entrega obtenerPorId(int id) throws SQLException {
        String sql = "SELECT * FROM entrega WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { 
                    Entrega e = map(rs); 
                    e.setArchivos(getArchivos(id, con)); 
                    return e; 
                }
            }
        }
        return null;
    }
    public void actualizarEstado(int id, String estado, String motivoRechazo) throws SQLException {
        String sql = "UPDATE entrega SET estado=?, motivo_rechazo=?, fecha_revision=NOW() WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado); 
            ps.setString(2, motivoRechazo); 
            ps.setInt(3, id); 
            ps.executeUpdate();
        }
    }
    private List<EntregaArchivo> getArchivos(int entregaId, Connection con) throws SQLException {
        List<EntregaArchivo> list = new ArrayList<>();
        String sql = "SELECT * FROM entrega_archivo WHERE entrega_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, entregaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EntregaArchivo a = new EntregaArchivo();
                    a.setId(rs.getInt("id"));
                    a.setEntregaId(rs.getInt("entrega_id")); 
                    a.setUrlArchivo(rs.getString("url_archivo")); 
                    a.setNombreArchivo(rs.getString("nombre_archivo")); 
                    list.add(a);
                }
            }
        }
        return list;
    }
    private Entrega map(ResultSet rs) throws SQLException {
        Entrega e = new Entrega();
        e.setId(rs.getInt("id")); 
        e.setContratoId(rs.getInt("contrato_id")); 
        e.setDescripcion(rs.getString("descripcion")); 
        e.setEstado(rs.getString("estado")); 
        e.setMotivoRechazo(rs.getString("motivo_rechazo"));
        Timestamp fe = rs.getTimestamp("fecha_entrega"); if (fe != null) e.setFechaEntrega(fe.toLocalDateTime());
        Timestamp fr = rs.getTimestamp("fecha_revision"); if (fr != null) e.setFechaRevision(fr.toLocalDateTime());
        return e;
    }
}
