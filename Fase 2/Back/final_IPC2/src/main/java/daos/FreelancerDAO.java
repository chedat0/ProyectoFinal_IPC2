/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import otros.ConnectionMySQL;
import modelo.Habilidad;
import modelo.Freelancer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author jeffm
 */
public class FreelancerDAO {
    
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection con = null;
    
    public FreelancerDAO() {
        con = connMySQL.conectar();
    }
    public Freelancer ingresar(int usuarioId) throws SQLException {
        String sql = "INSERT INTO freelancer (usuario_id, saldo, calificacion_promedio, total_calificaciones, perfil_completo) VALUES (?,0,0,0,FALSE)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, usuarioId);
            ps.executeUpdate();
            Freelancer f = new Freelancer();
            f.setUsuarioId(usuarioId);
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) f.setId(rs.getInt(1));
            }
            return f;
        }
    }

    public Freelancer obtenerPorUsuarioId(int usuarioId) throws SQLException {
        String sql = "SELECT f.*, u.nombre_completo, u.username, u.correo, u.telefono " +
                     "FROM freelancer f JOIN usuario u ON f.usuario_id = u.id WHERE f.usuario_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Freelancer f = map(rs);
                    f.setHabilidades(obtenerHabilidades(f.getId(), con));
                    return f;
                }
            }
        }
        return null;
    }

    public Freelancer obtenerPorId(int id) throws SQLException {
        String sql = "SELECT f.*, u.nombre_completo, u.username, u.correo, u.telefono " +
                     "FROM freelancer f JOIN usuario u ON f.usuario_id = u.id WHERE f.id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Freelancer f = map(rs);
                    f.setHabilidades(obtenerHabilidades(id, con));
                    return f;
                }
            }
        }
        return null;
    }

    public void actualizarPerfil(Freelancer f) throws SQLException {
        String sql = "UPDATE freelancer SET especialidad=?, descripcion=?, nivel_experiencia=?, " +
                     "tarifa_hora=?, portafolio_url=?, pais_residencia=?, perfil_completo=TRUE WHERE usuario_id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, f.getEspecialidad());
            ps.setString(2, f.getDescripcion());
            ps.setString(3, f.getNivelExperiencia()); 
            ps.setDouble(4, f.getTarifaHora());
            ps.setString(5, f.getPortafolioUrl());
            ps.setString(6, f.getPaisResidencia());
            ps.setInt(7, f.getUsuarioId());
            ps.executeUpdate();
        }
    }

    public void setHabilidades(int freelancerId, List<Integer> habilidadIds) throws SQLException {
        try (Connection con = connMySQL.conectar()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement del = con.prepareStatement("DELETE FROM freelancer_habilidad WHERE freelancer_id = ?")) {
                    del.setInt(1, freelancerId);
                    del.executeUpdate();
                }
                if (habilidadIds != null && !habilidadIds.isEmpty()) {
                    try (PreparedStatement ps = con.prepareStatement("INSERT INTO freelancer_habilidad (freelancer_id, habilidad_id) VALUES (?,?)")) {
                        for (Integer hid : habilidadIds) {
                            ps.setInt(1, freelancerId);
                            ps.setInt(2, hid);
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public void acreditarSaldo(int freelancerId, Double monto) throws SQLException {
        String sql = "UPDATE freelancer SET saldo = saldo + ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, monto);
            ps.setInt(2, freelancerId);
            ps.executeUpdate();
        }
    }

    public void actualizarCalificacion(int freelancerId) throws SQLException {
        String sql = "UPDATE freelancer SET " +
                     "calificacion_promedio = (SELECT COALESCE(AVG(estrellas),0) FROM calificacion WHERE freelancer_id = ?), " +
                     "total_calificaciones  = (SELECT COUNT(*) FROM calificacion WHERE freelancer_id = ?) " +
                     "WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, freelancerId);
            ps.setInt(2, freelancerId);
            ps.setInt(3, freelancerId);
            ps.executeUpdate();
        }
    }

    private List<Habilidad> obtenerHabilidades(int freelancerId, Connection con) throws SQLException {
        List<Habilidad> list = new ArrayList<>();
        String sql = "SELECT h.*, c.nombre AS categoria_nombre FROM habilidad h " +
                     "JOIN freelancer_habilidad fh ON h.id = fh.habilidad_id " +
                     "JOIN categoria c ON h.categoria_id = c.id WHERE fh.freelancer_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, freelancerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Habilidad h = new Habilidad();
                    h.setId(rs.getInt("id"));
                    h.setNombre(rs.getString("nombre"));
                    h.setCategoriaId(rs.getInt("categoria_id"));
                    h.setCategoriaNombre(rs.getString("categoria_nombre"));
                    list.add(h);
                }
            }
        }
        return list;
    }

    private Freelancer map(ResultSet rs) throws SQLException {
        Freelancer f = new Freelancer();
        f.setId(rs.getInt("id"));
        f.setUsuarioId(rs.getInt("usuario_id"));
        f.setEspecialidad(rs.getString("especialidad"));
        f.setDescripcion(rs.getString("descripcion"));
        f.setNivelExperiencia(rs.getString("nivel_experiencia"));
        f.setTarifaHora(rs.getDouble("tarifa_hora"));
        f.setPortafolioUrl(rs.getString("portafolio_url"));
        f.setPaisResidencia(rs.getString("pais_residencia"));
        f.setSaldo(rs.getDouble("saldo"));
        f.setCalificacionPromedio(rs.getDouble("calificacion_promedio"));
        f.setTotalCalificaciones(rs.getInt("total_calificaciones"));
        f.setPerfilCompleto(rs.getBoolean("perfil_completo"));
        f.setNombreCompleto(rs.getString("nombre_completo"));
        f.setUsername(rs.getString("username"));
        f.setCorreo(rs.getString("correo"));
        f.setTelefono(rs.getString("telefono"));
        return f;
    }
}
