/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import otros.ConnectionMySQL;
import modelo.Cliente;

import java.sql.*;

/**
 *
 * @author jeffm
 */
public class ClienteDAO {
    
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection conn = null;
    
    public ClienteDAO() {
        conn = connMySQL.conectar();                
    }

    public Cliente ingresar(int usuarioId) throws SQLException {
        String sql = "INSERT INTO cliente (usuario_id, saldo_disponible, saldo_bloqueado, perfil_completo) VALUES (?,0,0,FALSE)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, usuarioId);
            ps.executeUpdate();
            Cliente c = new Cliente();
            c.setUsuarioId(usuarioId);
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    c.setId(rs.getInt(1));
                }
            }
            return c;
        }
    }

    public Cliente obtenerPorUsuarioId(int usuarioId) throws SQLException {
        String sql = "SELECT c.*, u.nombre_completo, u.username, u.correo, u.telefono "
                + "FROM cliente c JOIN usuario u ON c.usuario_id = u.id WHERE c.usuario_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public Cliente obtenerPorId(int id) throws SQLException {
        String sql = "SELECT c.*, u.nombre_completo, u.username, u.correo, u.telefono "
                + "FROM cliente c JOIN usuario u ON c.usuario_id = u.id WHERE c.id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public void actualizarPerfil(Cliente c) throws SQLException {
        String sql = "UPDATE cliente SET nombre_empresa=?, descripcion=?, sector=?, sitio_web=?, pais=?, perfil_completo=TRUE WHERE usuario_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNombreEmpresa());
            ps.setString(2, c.getDescripcion());
            ps.setString(3, c.getSector());
            ps.setString(4, c.getSitioWeb());
            ps.setString(5, c.getPais());
            ps.setInt(6, c.getUsuarioId());
            ps.executeUpdate();
        }
    }

    public void recargarSaldo(int clienteId, Double monto) throws SQLException {
        String sql = "UPDATE cliente SET saldo_disponible = saldo_disponible + ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, monto);
            ps.setInt(2, clienteId);
            ps.executeUpdate();
        }
    }

    public void bloquearSaldo(int clienteId, Double monto) throws SQLException {
        String sql = "UPDATE cliente SET saldo_disponible = saldo_disponible - ?, saldo_bloqueado = saldo_bloqueado + ? WHERE id = ? AND saldo_disponible >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, monto);
            ps.setDouble(2, monto);
            ps.setInt(3, clienteId);
            ps.setDouble(4, monto);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Saldo insuficiente");
            }
        }
    }

    public void liberarSaldoBloqueado(int clienteId, Double monto) throws SQLException {
        String sql = "UPDATE cliente SET saldo_bloqueado = saldo_bloqueado - ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, monto);
            ps.setInt(2, clienteId);
            ps.executeUpdate();
        }
    }

    public void descontarSaldoBloqueado(int clienteId, Double monto) throws SQLException {
        String sql = "UPDATE cliente SET saldo_bloqueado = saldo_bloqueado - ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, monto);
            ps.setInt(2, clienteId);
            ps.executeUpdate();
        }
    }

    private Cliente map(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        c.setUsuarioId(rs.getInt("usuario_id"));
        c.setNombreEmpresa(rs.getString("nombre_empresa"));
        c.setDescripcion(rs.getString("descripcion"));
        c.setSector(rs.getString("sector"));
        c.setSitioWeb(rs.getString("sitio_web"));
        c.setPais(rs.getString("pais"));
        c.setSaldoDisponible(rs.getDouble("saldo_disponible"));
        c.setSaldoBloqueado(rs.getDouble("saldo_bloqueado"));
        c.setPerfilCompleto(rs.getBoolean("perfil_completo"));
        c.setNombreCompleto(rs.getString("nombre_completo"));
        c.setUsername(rs.getString("username"));
        c.setCorreo(rs.getString("correo"));
        c.setTelefono(rs.getString("telefono"));
        return c;
    }
}
