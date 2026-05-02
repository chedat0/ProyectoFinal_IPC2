/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import otros.ConnectionMySQL;

import java.sql.*;
import java.util.*;

/**
 *
 * @author jeffm
 */
public class ReporteDAO {
    
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection con = null;
    
    public ReporteDAO(){
        con = connMySQL.conectar();        
    }
    
    public List<Map<String,Object>> topFreelancers(String fechaInicio, String fechaFin) throws SQLException {
        String sql = "SELECT u.nombre_completo, COUNT(c.id) AS contratos, SUM(c.monto) AS total_generado, SUM(c.comision_monto) AS comision_plataforma FROM contrato c JOIN freelancer f ON c.freelancer_id = f.id JOIN usuario u ON f.usuario_id = u.id WHERE c.estado='COMPLETADO' AND c.fecha_fin BETWEEN ? AND ? GROUP BY f.id, u.nombre_completo ORDER BY total_generado DESC LIMIT 5";
        return query(sql, fechaInicio + " 00:00:00", fechaFin + " 23:59:59");
    }
    public List<Map<String,Object>> topCategorias(String fechaInicio, String fechaFin) throws SQLException {
        String sql = "SELECT cat.nombre, COUNT(c.id) AS contratos, SUM(c.comision_monto) AS total_comisiones FROM contrato c JOIN proyecto p ON c.proyecto_id = p.id JOIN categoria cat ON p.categoria_id = cat.id WHERE c.estado='COMPLETADO' AND c.fecha_fin BETWEEN ? AND ? GROUP BY cat.id, cat.nombre ORDER BY contratos DESC LIMIT 5";
        return query(sql, fechaInicio + " 00:00:00", fechaFin + " 23:59:59");
    }
    public Map<String,Object> ingresosTotales(String fechaInicio, String fechaFin) throws SQLException {
        String sql = "SELECT COUNT(id) AS contratos_completados, COALESCE(SUM(comision_monto),0) AS total_comisiones FROM contrato WHERE estado='COMPLETADO' AND fecha_fin BETWEEN ? AND ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, fechaInicio + " 00:00:00"); ps.setString(2, fechaFin + " 23:59:59");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String,Object> r = new LinkedHashMap<>();
                    r.put("contratos_completados", rs.getInt("contratos_completados"));
                    r.put("total_comisiones", rs.getDouble("total_comisiones")); return r;
                }
            }
        }
        return new HashMap<>();
    }
    public List<Map<String,Object>> historialProyectosCliente(int clienteId, String fi, String ff) throws SQLException {
        String sql = "SELECT p.titulo, p.estado, c.monto, c.fecha_fin, u.nombre_completo AS freelancer FROM proyecto p LEFT JOIN contrato c ON c.proyecto_id = p.id LEFT JOIN freelancer f ON c.freelancer_id = f.id LEFT JOIN usuario u ON f.usuario_id = u.id WHERE p.cliente_id = ? AND p.fecha_publicacion BETWEEN ? AND ? ORDER BY p.fecha_publicacion DESC";
        return query(sql, clienteId, fi + " 00:00:00", ff + " 23:59:59");
    }
    public List<Map<String,Object>> gastosPorCategoria(int clienteId, String fi, String ff) throws SQLException {
        String sql = "SELECT cat.nombre AS categoria, COUNT(c.id) AS contratos, SUM(c.monto) AS total_gastado FROM contrato c JOIN proyecto p ON c.proyecto_id = p.id JOIN categoria cat ON p.categoria_id = cat.id WHERE c.cliente_id = ? AND c.estado='COMPLETADO' AND c.fecha_fin BETWEEN ? AND ? GROUP BY cat.id, cat.nombre ORDER BY total_gastado DESC";
        return query(sql, clienteId, fi + " 00:00:00", ff + " 23:59:59");
    }
    public List<Map<String,Object>> contratosCompletadosFreelancer(int freelancerId, String fi, String ff) throws SQLException {
        String sql = "SELECT p.titulo, u.nombre_completo AS cliente, c.monto, cal.estrellas, c.fecha_fin FROM contrato c JOIN proyecto p ON c.proyecto_id = p.id JOIN cliente cl ON c.cliente_id = cl.id JOIN usuario u ON cl.usuario_id = u.id LEFT JOIN calificacion cal ON cal.contrato_id = c.id WHERE c.freelancer_id = ? AND c.estado='COMPLETADO' AND c.fecha_fin BETWEEN ? AND ? ORDER BY c.fecha_fin DESC";
        return query(sql, freelancerId, fi + " 00:00:00", ff + " 23:59:59");
    }
    public List<Map<String,Object>> topCategoriasFreelancer(int freelancerId) throws SQLException {
        String sql = "SELECT cat.nombre, COUNT(c.id) AS contratos, SUM(c.monto) AS total_ingresos FROM contrato c JOIN proyecto p ON c.proyecto_id = p.id JOIN categoria cat ON p.categoria_id = cat.id WHERE c.freelancer_id = ? AND c.estado='COMPLETADO' GROUP BY cat.id, cat.nombre ORDER BY contratos DESC LIMIT 5";
        return query(sql, freelancerId);
    }
    private List<Map<String,Object>> query(String sql, Object... params) throws SQLException {
        List<Map<String,Object>> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i+1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int cols = meta.getColumnCount();
                while (rs.next()) {
                    Map<String,Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= cols; i++) row.put(meta.getColumnLabel(i), rs.getObject(i));
                    list.add(row);
                }
            }
        }
        return list;
    }
    
}
