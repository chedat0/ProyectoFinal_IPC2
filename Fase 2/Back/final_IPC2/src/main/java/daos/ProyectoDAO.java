/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import otros.ConnectionMySQL;
import modelo.Habilidad;
import modelo.Proyecto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeffm
 */
public class ProyectoDAO {
    
    ConnectionMySQL conMySQL = new ConnectionMySQL();
    Connection con = null;
    
    public ProyectoDAO(){
        con = conMySQL.conectar();
    }
     public Proyecto ingresar(Proyecto p, List<Integer> habilidadIds) throws SQLException {
        try (Connection con = conMySQL.conectar()) {
            con.setAutoCommit(false);
            try {
                String sql = "INSERT INTO proyecto (cliente_id, categoria_id, titulo, descripcion, presupuesto_maximo, fecha_limite, estado) VALUES (?,?,?,?,?,?,'ABIERTO')";
                try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, p.getClienteId());
                    ps.setInt(2, p.getCategoriaId());
                    ps.setString(3, p.getTitulo());
                    ps.setString(4, p.getDescripcion());
                    ps.setDouble(5, p.getPresupuestoMaximo());
                    ps.setDate(6, Date.valueOf(p.getFechaLimite()));
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) p.setId(rs.getInt(1));
                    }
                }
                insertHabilidades(p.getId(), habilidadIds, con);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
        return p;
    }

    public Proyecto obtenerPorId(int id) throws SQLException {
        String sql = "SELECT p.*, c.nombre_completo AS cliente_nombre, cat.nombre AS categoria_nombre, (SELECT COUNT(*) FROM propuesta WHERE proyecto_id = p.id AND estado = 'PENDIENTE') AS total_propuestas FROM proyecto p JOIN cliente cl ON p.cliente_id = cl.id JOIN usuario c ON cl.usuario_id = c.id JOIN categoria cat ON p.categoria_id = cat.id WHERE p.id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Proyecto proj = map(rs);
                    proj.setHabilidades(getHabilidades(id, con));
                    return proj;
                }
            }
        }
        return null;
    }

    public List<Proyecto> obtenerPorCliente(int clienteId, String estado) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT p.*, u.nombre_completo AS cliente_nombre, cat.nombre AS categoria_nombre, (SELECT COUNT(*) FROM propuesta WHERE proyecto_id = p.id AND estado = 'PENDIENTE') AS total_propuestas FROM proyecto p JOIN cliente cl ON p.cliente_id = cl.id JOIN usuario u ON cl.usuario_id = u.id JOIN categoria cat ON p.categoria_id = cat.id WHERE p.cliente_id = ?");
        if (estado != null) sql.append(" AND p.estado = ?");
        sql.append(" ORDER BY p.fecha_publicacion DESC");
        List<Proyecto> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            ps.setInt(1, clienteId);
            if (estado != null) ps.setString(2, estado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public List<Proyecto> obtenerAbiertos(Integer categoriaId, Integer habilidadId, String presupuestoMin, String presupuestoMax) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT p.*, u.nombre_completo AS cliente_nombre, cat.nombre AS categoria_nombre, (SELECT COUNT(*) FROM propuesta WHERE proyecto_id = p.id AND estado = 'PENDIENTE') AS total_propuestas FROM proyecto p JOIN cliente cl ON p.cliente_id = cl.id JOIN usuario u ON cl.usuario_id = u.id JOIN categoria cat ON p.categoria_id = cat.id WHERE p.estado = 'ABIERTO'");
        List<Object> params = new ArrayList<>();
        if (categoriaId != null) { sql.append(" AND p.categoria_id = ?"); params.add(categoriaId); }
        if (habilidadId != null) { sql.append(" AND EXISTS (SELECT 1 FROM proyecto_habilidad WHERE proyecto_id = p.id AND habilidad_id = ?)"); params.add(habilidadId); }
        if (presupuestoMin != null) { sql.append(" AND p.presupuesto_maximo >= ?"); params.add(new java.math.BigDecimal(presupuestoMin)); }
        if (presupuestoMax != null) { sql.append(" AND p.presupuesto_maximo <= ?"); params.add(new java.math.BigDecimal(presupuestoMax)); }
        sql.append(" ORDER BY p.fecha_publicacion DESC");
        List<Proyecto> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public void actualizarEstado(int proyectoId, String estado) throws SQLException {
        String sql = "UPDATE proyecto SET estado = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, proyectoId);
            ps.executeUpdate();
        }
    }

    public void actualizar(Proyecto p, List<Integer> habilidadIds) throws SQLException {
        try (Connection con = conMySQL.conectar()) {
            con.setAutoCommit(false);
            try {
                String sql = "UPDATE proyecto SET categoria_id=?, titulo=?, descripcion=?, presupuesto_maximo=?, fecha_limite=? WHERE id=? AND estado='ABIERTO'";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, p.getCategoriaId());
                    ps.setString(2, p.getTitulo());
                    ps.setString(3, p.getDescripcion());
                    ps.setDouble(4, p.getPresupuestoMaximo());
                    ps.setDate(5, Date.valueOf(p.getFechaLimite()));
                    ps.setInt(6, p.getId());
                    ps.executeUpdate();
                }
                try (PreparedStatement del = con.prepareStatement("DELETE FROM proyecto_habilidad WHERE proyecto_id = ?")) {
                    del.setInt(1, p.getId());
                    del.executeUpdate();
                }
                insertHabilidades(p.getId(), habilidadIds, con);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    private void insertHabilidades(int proyectoId, List<Integer> ids, Connection con) throws SQLException {
        if (ids == null || ids.isEmpty()) return;
        String sql = "INSERT INTO proyecto_habilidad (proyecto_id, habilidad_id) VALUES (?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (Integer hid : ids) {
                ps.setInt(1, proyectoId);
                ps.setInt(2, hid);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private List<Habilidad> getHabilidades(int proyectoId, Connection con) throws SQLException {
        List<Habilidad> list = new ArrayList<>();
        String sql = "SELECT h.* FROM habilidad h JOIN proyecto_habilidad ph ON h.id = ph.habilidad_id WHERE ph.proyecto_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, proyectoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Habilidad h = new Habilidad();
                    h.setId(rs.getInt("id"));
                    h.setNombre(rs.getString("nombre"));
                    list.add(h);
                }
            }
        }
        return list;
    }

    private Proyecto map(ResultSet rs) throws SQLException {
        Proyecto p = new Proyecto();
        p.setId(rs.getInt("id"));
        p.setClienteId(rs.getInt("cliente_id"));
        p.setCategoriaId(rs.getInt("categoria_id"));
        p.setTitulo(rs.getString("titulo"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setPresupuestoMaximo(rs.getDouble("presupuesto_maximo"));
        Date fl = rs.getDate("fecha_limite");
        if (fl != null) p.setFechaLimite(fl.toLocalDate());
        p.setEstado(rs.getString("estado"));
        Timestamp fp = rs.getTimestamp("fecha_publicacion");
        if (fp != null) p.setFechaPublicacion(fp.toLocalDateTime());
        p.setClienteNombre(rs.getString("cliente_nombre"));
        p.setCategoriaNombre(rs.getString("categoria_nombre"));
        try { p.setTotalPropuestas(rs.getInt("total_propuestas")); } catch (Exception ignored) {}
        return p;
    }
}
