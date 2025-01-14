package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.excecoes.ErroDeConversaoDeAnoException;
import br.com.alura.screenmatch.modelos.Titulo;
import br.com.alura.screenmatch.modelos.TituloOmdb;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Scanner;

public class PrincipalComBusca {
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner leitura = new Scanner(System.in);
        ArrayList<Titulo> titulos = new ArrayList<>();
        char answer = 'y';
        String json = "";
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).setPrettyPrinting().create();

        while (answer == 'y') {
            System.out.print("Digite um filme para busca: ");
            var busca = leitura.nextLine();

            String endereco = "https://www.omdbapi.com/?t=" + busca.replace(" ", "+") + "&apikey=1e4cf23f";
            System.out.println("link: " + endereco);
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(endereco)).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                json = response.body();
                System.out.println(json);

                TituloOmdb meuTituloOmdb = gson.fromJson(json, TituloOmdb.class);
                //System.out.println(meuTituloOmdb);

                Titulo meuTitulo = new Titulo(meuTituloOmdb);
                //System.out.println(meuTitulo);

                titulos.add(meuTitulo);
            } catch (IOException e) {
                System.out.println("Erro de entrada e saída (I/O)");
                System.out.println(e.getMessage());

            } catch (ErroDeConversaoDeAnoException e) {
                System.out.println(e.getMessage());

            } catch (Exception e) {
                System.out.println("Erro indefinido!");
                System.out.println(e.getMessage());
            }

            System.out.print("Deseja continuar a busca? <y/n>\nR: ");
            answer = leitura.nextLine().charAt(0);

        }

        if (!titulos.isEmpty()) {
            File file = new File("C:\\java\\projects\\alura\\screenmatch\\titulos.json");
            FileWriter escrita = new FileWriter(file);
            escrita.write(gson.toJson(titulos));
            escrita.close();
        }


    }
}
