/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import otros.ConnectionMySQL;
import modelo.RecargaSaldo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeffm
 */
public class RecargaDAO {
    
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection con = null;
    
    public RecargaDAO(){
        con = connMySQL.conectar();
    }
    
    public RecargaSaldo create(RecargaSaldo r) throws SQLException {
        String sql = "INSERT INTO recarga_saldo (cliente_id, monto, metodo_pago, referencia, descripcion) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getClienteId());
            ps.setDouble(2, r.getMonto());
            ps.setString(3, r.getMetodoPago() != null ? r.getMetodoPago() : "TRANSFERENCIA");
            ps.setString(4, r.getReferencia());
            ps.setString(5, r.getDescripcion() != null ? r.getDescripcion() : "Recarga de saldo");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) r.setId(rs.getInt(1));
            }
        }
        return r;
    }

    public List<RecargaSaldo> obtenerPorCliente(int clienteId) throws SQLException {
        String sql = "SELECT * FROM recarga_saldo WHERE cliente_id = ? ORDER BY fecha DESC";
        List<RecargaSaldo> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, clienteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RecargaSaldo r = new RecargaSaldo();
                    r.setId(rs.getInt("id"));
                    r.setClienteId(rs.getInt("cliente_id"));
                    r.setMonto(rs.getDouble("monto"));
                    r.setMetodoPago(rs.getString("metodo_pago"));
                    r.setReferencia(rs.getString("referencia"));
                    Timestamp f = rs.getTimestamp("fecha");
                    if (f != null) r.setFecha(f.toLocalDateTime());
                    r.setDescripcion(rs.getString("descripcion"));
                    list.add(r);
                }
            }
        }
        return list;
    }
}
