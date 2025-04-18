package br.com.github.pedrossjr;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.json.JSONArray;
import org.json.JSONObject;

public class LambdaPostgresConnector implements RequestHandler<Object, String> {

    @Override
    public String handleRequest(Object input, Context context) {
        String jdbcUrl = "jdbc:postgresql://<endpoint-amazon>:5432/livraria-db";
        String username = "<usuario-da-instancia>";
        String password = "<senha-da-instancia>";

        JSONArray resultadoJson = new JSONArray();

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("select tbl_autores.id, tbl_autores.nome_autor, tbl_autores.data_nascimento, tbl_autores.data_falecimento, \n" +
                     "tbl_livros.titulo, tbl_livros.descricao, tbl_livros.isbn, tbl_livros.ano_publicacao, tbl_livros.numero_pagina\n" +
                     "from tbl_autores\n" +
                     "inner join tbl_livros on tbl_autores.id = tbl_livros.autor_id\n" +
                     "order by tbl_autores.nome_autor, tbl_livros.titulo");) {

            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("id", rs.getInt("id"));
                obj.put("nome_autor", rs.getString("nome_autor"));
                obj.put("data_nascimento", rs.getDate("data_nascimento"));
                obj.put("data_falecimento", rs.getDate("data_falecimento"));
                obj.put("titulo", rs.getString("titulo"));
                obj.put("descricao", rs.getString("descricao"));
                obj.put("isbn", rs.getString("isbn"));
                obj.put("ano_publicacao", rs.getInt("ano_publicacao"));
                obj.put("numero_pagina", rs.getInt("numero_pagina"));
                resultadoJson.put(obj);
            }

            return resultadoJson.toString();

        } catch (Exception e) {
            return "{\"Erro\": \"" + e.getMessage() + "\"}";
        }
    }
}
