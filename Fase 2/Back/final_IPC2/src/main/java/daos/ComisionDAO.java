/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import otros.ConnectionMySQL;
import modelo.ConfiguracionComision;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeffm
 */
public class ComisionDAO {
    
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection con = null;
    
    public ComisionDAO(){
        con = connMySQL.conectar();        
    }
    
    public Double obtenerComisionActual() throws SQLException {
        String sql = "SELECT porcentaje FROM configuracion_comision WHERE activa = TRUE ORDER BY fecha_inicio DESC LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql); 
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble("porcentaje");
        }
        return 10.00; // Default 10%
    }
    
    public void setComision(int adminId, Double porcentaje) throws SQLException {
        try (Connection con = connMySQL.conectar()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement("UPDATE configuracion_comision SET activa=FALSE, fecha_fin=NOW() WHERE activa=TRUE")) { ps.executeUpdate(); }
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO configuracion_comision (admin_id, porcentaje, activa) VALUES (?,?,TRUE)")) {
                    ps.setInt(1, adminId); 
                    ps.setDouble(2, porcentaje); 
                    ps.executeUpdate();
                }
                con.commit();
            } catch (SQLException e) { 
                con.rollback(); 
                throw e; 
            } finally { con.setAutoCommit(true); }
        }
    }
    
    public List<ConfiguracionComision> obtenerHistorial() throws SQLException {
        String sql = "SELECT cc.*, u.nombre_completo AS admin_nombre FROM configuracion_comision cc JOIN administrador a ON cc.admin_id = a.id JOIN usuario u ON a.usuario_id = u.id ORDER BY cc.fecha_inicio DESC";
        List<ConfiguracionComision> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ConfiguracionComision c = new ConfiguracionComision();
                c.setId(rs.getInt("id")); 
                c.setAdminId(rs.getInt("admin_id")); 
                c.setPorcentaje(rs.getDouble("porcentaje")); 
                c.setActiva(rs.getBoolean("activa"));
                Timestamp fi = rs.getTimestamp("fecha_inicio"); if (fi != null) c.setFechaInicio(fi.toLocalDateTime());
                Timestamp ff = rs.getTimestamp("fecha_fin"); if (ff != null) c.setFechaFin(ff.toLocalDateTime());
                c.setAdminNombre(rs.getString("admin_nombre")); 
                list.add(c);
            }
        }
        return list;
    }
    
    public Double getSaldoGlobal() throws SQLException {
        String sql = "SELECT COALESCE(SUM(comision_monto),0) FROM contrato WHERE estado='COMPLETADO'";
        try (PreparedStatement ps = con.prepareStatement(sql); 
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        }
        return 0.0;
    }
}
